package com.lycilph.lunchviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceQuery;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends Activity implements ActionBar.TabListener, WeekMenuFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String FILENAME = "menus.txt";

    MobileServiceClient mClient;
    MobileServiceTable<WeekMenu> menuTable;
    MobileServiceTable<WeekMenuItem> menuItemTable;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    List<WeekMenu> menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mViewPager.setCurrentItem(1);

        try {
            mClient = new MobileServiceClient("https://lunchviewer.azure-mobile.net/", "SVzovNQtJGFXALLJDUskHXIZqDSBwL46", this);
            menuTable = mClient.getTable("Menu", WeekMenu.class);
            menuItemTable = mClient.getTable("Item", WeekMenuItem.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load file here and parse saved data
        File file = new File(getFilesDir(), FILENAME);
        if (file.exists()) {
            Log.i(TAG, "Reading saved file");

            String json = new String();
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    json += line;
                }

                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            WeekMenu[] savedMenus = gson.fromJson(json, WeekMenu[].class);
            menus = Arrays.asList(savedMenus);
        } else {
            Log.i(TAG, "No saved file found");
            menus = Arrays.asList(new WeekMenu[] {new WeekMenu(), new WeekMenu(), new WeekMenu()});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save data here
        File file = new File(getFilesDir(), FILENAME);
        Gson gson = new Gson();
        String json = gson.toJson(menus);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.close();
            Log.i(TAG, "File written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshMenus();
            return true;
        } else if (id == R.id.action_clear) {
            clearMenus();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMenus() {
        for (int i = 0; i <= 2; i++) {
            refreshMenu(menus.get(i), i-1);
        }
    }

    private void clearMenus() {
        for (WeekMenu menu : menus) {
            menu.clear();
        }
    }

    private void refreshMenu(WeekMenu menu, int weekOffset)
    {
        Log.i(TAG, "Refreshing menu " + menu.toString());

        DateTime dt = new DateTime();
        int week = dt.plusWeeks(weekOffset).weekOfWeekyear().get();

        if (menu.getWeek() != week) {
            Log.i(TAG, "Menu out of date");
            loadMenu(menu, week);
        } else {
            Log.i(TAG, "Menu up to date");
        }
    }

    private void loadMenu(final WeekMenu menuToUpdate, final int newWeekNumber) {
        Log.i(TAG, "Loading menu for week " + newWeekNumber);

        menuTable.where().field("Week").eq(newWeekNumber).execute(new TableQueryCallback<WeekMenu>() {
            public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if (result.isEmpty()) {
                        Log.i(TAG, "No menu found for week " + newWeekNumber);
                    }

                    for (WeekMenu wm : result) {
                        loadMenuItems(menuToUpdate, wm);
                    }
                } else {
                    Log.e(TAG, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    private void loadMenuItems(final WeekMenu menuToUpdate, final WeekMenu newWeekMenu) {
        Log.i(TAG, "Loading items for " + newWeekMenu.toString());

        MobileServiceQuery<TableQueryCallback<WeekMenuItem>> query = menuItemTable.where();
        query.setQueryText(String.format("ParentId eq guid'%s'", newWeekMenu.getMenuId()));
        query.execute(new TableQueryCallback<WeekMenuItem>() {
            public void onCompleted(List<WeekMenuItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    for (WeekMenuItem item : result) {
                        Log.i(TAG, "Found item " + item.toString());
                    }
                    newWeekMenu.setItems(result);
                    menuToUpdate.update(newWeekMenu);
                } else {
                    Log.e(TAG, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onFragmentInteraction(String id) {}

    public WeekMenu getMenu(int position) {
        return menus.get(position);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WeekMenuFragment.newInstance(position);
        }

        @Override
        public int getCount() { return 3; }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.previous_week_label).toUpperCase(l);
                case 1:
                    return getString(R.string.current_week_label).toUpperCase(l);
                case 2:
                    return getString(R.string.next_week_label).toUpperCase(l);
            }
            return null;
        }
    }
}
