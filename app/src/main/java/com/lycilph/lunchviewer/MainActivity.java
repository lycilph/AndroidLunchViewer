package com.lycilph.lunchviewer;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceQuery;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements FragmentManager.OnBackStackChangedListener, WeekMenuFragment.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static final String SENDER_ID = "449754482340";

    private MobileServiceClient mobileClient;
    private MobileServiceTable<WeekMenu> menuTable;
    private MobileServiceTable<WeekMenuItem> menuItemTable;

    private ProgressBar progressBar;

    private DataFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        dataFragment = (DataFragment) getFragmentManager().findFragmentByTag("data");
        if (dataFragment == null) {
            Log.i(TAG, "Creating new data fragment");
            dataFragment = new DataFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(dataFragment, "data")
                    .commit();
        } else {
            Log.i(TAG, "Retained data fragment found");
        }

        if (savedInstanceState == null) {
            MasterFragment mf = MasterFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mf)
                    .commit();
        }

        try {
            mobileClient = new MobileServiceClient("https://lunchviewer.azure-mobile.net/", "SVzovNQtJGFXALLJDUskHXIZqDSBwL46", this).withFilter(new ProgressFilter());
            menuTable = mobileClient.getTable("Menu", WeekMenu.class);
            menuItemTable = mobileClient.getTable("Item", WeekMenuItem.class);

            NotificationsManager.handleNotifications(this, SENDER_ID, PushNotificationHandler.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh: {
                refreshAllMenus();
                return true;
            }
            case R.id.action_clear: {
                clearAllMenus();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHome();
    }

    @Override
    protected void onPause() {
        super.onPause();

        getFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHome();
    }

    @Override
    public boolean onNavigateUp() {
        getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onItemSelected(int position, int item) {
        DetailsFragment df = DetailsFragment.newInstance(position, item);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, df)
                .addToBackStack(null)
                .commit();
    }

    private void shouldDisplayHome() {
        boolean canBack = getFragmentManager().getBackStackEntryCount() > 0;
        getActionBar().setDisplayHomeAsUpEnabled(canBack);
    }

    public WeekMenu getMenu(int position) {
        return dataFragment.getMenu(position);
    }

    public void setMenu(int position, WeekMenu menu)
    {
        dataFragment.setMenu(position, menu);

        String eventName = getString(R.string.menu_update_event);
        Intent intent = new Intent(eventName);
        intent.putExtra("Position", position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.i(TAG, "Update sent for position " + position);
    }

    private void refreshAllMenus() {
        for (int i = 0; i <= 2; i++) {
            refreshMenu(i, i-1);
        }
    }

    private void clearAllMenus() {
        for (int i = 0; i <= 2; i++) {
            setMenu(i, new WeekMenu());
        }
    }

    private void refreshMenu(int position, int weekOffset) {
        WeekMenu menu = getMenu(position);
        Log.i(TAG, "Refreshing menu " + menu.toString());

        DateTime dt = DateTime.now().plusWeeks(weekOffset);
        int week = dt.weekOfWeekyear().get();
        int year = dt.year().get();
        if (menu.getWeek() != week || menu.getItems().isEmpty()) {
            Log.i(TAG, "Menu out of date or empty");
            downloadMenu(position, week, year);
        } else {
            Log.i(TAG, "Menu up to date");
        }
    }

    private void downloadMenu(final int position, final int week, final int year) {
        Log.i(TAG, String.format("Downloading menu for week %d of year %d", week, year));

        menuTable.where().field("Week").eq(week)
                   .and().field("Year").eq(year)
                 .execute(new TableQueryCallback<WeekMenu>() {
            public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if (result.isEmpty()) {
                        Log.i(TAG, "No menu found for week " + week);
                        WeekMenu menu = new WeekMenu();
                        menu.setWeek(week);
                        menu.setYear(year);
                        setMenu(position, menu);
                        return;
                    }
                    if (result.size() > 1) {
                        Log.i(TAG, "Multiple menus found for week " + week + " (only first is used)");
                    }

                    downloadMenuItems(position, result.get(0));
                } else {
                    Log.e(TAG, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    private void downloadMenuItems(final int position, final WeekMenu menu) {
        Log.i(TAG, "Downloading items for " + menu.toString());

        MobileServiceQuery<TableQueryCallback<WeekMenuItem>> query = menuItemTable.where();
        query.setQueryText(String.format("ParentId eq guid'%s'", menu.getMenuId()));

        query.execute(new TableQueryCallback<WeekMenuItem>() {
            public void onCompleted(List<WeekMenuItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    for (WeekMenuItem item : result) {
                        Log.i(TAG, "Found item " + item.toString());
                    }
                    menu.setItems(result);
                    setMenu(position, menu);
                } else {
                    Log.e(TAG, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    public void registerForPush(String gcmRegistrationId) {
        mobileClient.getPush().register(gcmRegistrationId,null,new RegistrationCallback() {
            @Override
            public void onRegister(Registration registration, Exception exception) {
                if (exception == null) {
                    Log.i(TAG, "Registration of push notifications done");
                } else {
                    exception.printStackTrace();
                }
            }
        });
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public void handleRequest(ServiceFilterRequest request,
                                  NextServiceFilterCallback nextServiceFilterCallback,
                                  final ServiceFilterResponseCallback responseCallback) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressBar != null)
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (progressBar != null)
                                progressBar.setVisibility(ProgressBar.INVISIBLE);

                            Toast.makeText(MainActivity.this, "Update done!", Toast.LENGTH_SHORT)
                                 .show();
                        }
                    });

                    if (responseCallback != null)  responseCallback.onResponse(response, exception);
                }
            });
        }
    }
}