package com.lycilph.lunchviewer.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.fragments.DataFragment;
import com.lycilph.lunchviewer.fragments.DetailsFragment;
import com.lycilph.lunchviewer.fragments.LogFragment;
import com.lycilph.lunchviewer.fragments.MasterFragment;
import com.lycilph.lunchviewer.fragments.NavigationDrawerFragment;
import com.lycilph.lunchviewer.fragments.SettingsFragment;
import com.lycilph.lunchviewer.fragments.WeekMenuFragment;
import com.lycilph.lunchviewer.misc.AzureService;
import com.lycilph.lunchviewer.misc.DataService;
import com.lycilph.lunchviewer.misc.DateUtils;
import com.lycilph.lunchviewer.misc.PushNotificationHandler;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class MainActivity
        extends Activity
        implements FragmentManager.OnBackStackChangedListener,
                   WeekMenuFragment.OnItemSelectedListener,
                   NavigationDrawerFragment.OnNavigationDrawerInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String SENDER_ID = "449754482340";

    private ProgressBar progressBar;
    private DataFragment dataFragment;
    private NavigationDrawerFragment navigationFragment;

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

        navigationFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationFragment.setUp();

        if (savedInstanceState == null) {
            Log.i(TAG, "Creating new master fragment");
            MasterFragment mf = MasterFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mf)
                    .commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null && !SettingsFragment.getShowLog(this)) {
            MenuItem item = menu.findItem(R.id.action_log);
            if (item != null)
                item.setVisible(false);
        }

        if (menu != null && !SettingsFragment.getShowClear(this)) {
            MenuItem item = menu.findItem(R.id.action_clear);
            if (item != null)
                item.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
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
                getAzureService().updateAllMenus();
                return true;
            }
            case R.id.action_clear: {
                getDataService().clearAllMenus();
                return true;
            }
            case R.id.action_log: {
                showLog();
                return true;
            }
            case R.id.action_settings: {
                showSettings();
                return true;
            }
            case R.id.action_about: {
                showAbout();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationInteraction(int id) {
        switch (id) {
            case R.id.action_home: {
                showHome();
                break;
            }
            case R.id.action_today: {
                showToday();
                break;
            }
            case R.id.action_next: {
                showNext();
                break;
            }
            case R.id.action_settings: {
                showSettings();
                break;
            }
            case R.id.action_about: {
                showAbout();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        String updateStartedEventName = getString(R.string.update_started_event);
        broadcastManager.registerReceiver(updateStartedMessageReceiver, new IntentFilter(updateStartedEventName));

        String updateFinishedEventName = getString(R.string.update_finished_event);
        broadcastManager.registerReceiver(updateFinishedMessageReceiver, new IntentFilter(updateFinishedEventName));

        getFragmentManager().addOnBackStackChangedListener(this);
        updateActionBar();

        DataService dataService = getDataService();
        if (dataService.allEmpty()) {
            getAzureService().updateAllMenus();
        } else {
            dataService.updateMenus();
        }

        NotificationsManager.handleNotifications(this, SENDER_ID, PushNotificationHandler.class);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getFragmentManager().removeOnBackStackChangedListener(this);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        broadcastManager.unregisterReceiver(updateStartedMessageReceiver);
        broadcastManager.unregisterReceiver(updateFinishedMessageReceiver);
    }

    @Override
    public void onBackStackChanged() {
        updateActionBar();
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

    private void showHome() {
        MasterFragment f = (MasterFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        f.showItem(1);
    }

    private void showToday() {
        int index = DateTime.now().dayOfWeek().get() - 1;
        onItemSelected(1, index);
    }

    private void showNext() {
        LocalDate nextDate = DateUtils.getNextValidDate();
        int position = getDataService().getPositionForDate(nextDate);
        int item = getDataService().getItemIndexForDate(nextDate);

        if (position > -1 && item > -1) {
            onItemSelected(position, item);
        }
    }

    private void showLog() {
        LogFragment lf = new LogFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, lf)
                .addToBackStack(null)
                .commit();
    }

    private void showSettings() {
        SettingsFragment f = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }

    private void showAbout() {
        // Implement this
    }

    private void updateActionBar() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        Log.i(TAG, "Backstack count = " + backStackEntryCount);

        navigationFragment.updateActionBar(backStackEntryCount);
    }

    public AzureService getAzureService() {
        return dataFragment.getAzureService();
    }

    public DataService getDataService() {
        return dataFragment.getDataService();
    }

    private BroadcastReceiver updateStartedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressBar != null)
                progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    };

    private BroadcastReceiver updateFinishedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressBar != null)
                progressBar.setVisibility(ProgressBar.INVISIBLE);

            Toast.makeText(MainActivity.this, "Update done!", Toast.LENGTH_SHORT).show();
        }
    };
}