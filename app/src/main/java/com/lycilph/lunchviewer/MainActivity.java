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

    WeekMenu previousWeekMenu;
    WeekMenu currentWeekMenu;
    WeekMenu nextWeekMenu;

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

        // Find week numbers
        /*Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        now.add(Calendar.WEEK_OF_YEAR, -1);
        int previousWeek = now.get(Calendar.WEEK_OF_YEAR);
        now.add(Calendar.WEEK_OF_YEAR, 2);
        int nextWeek = now.get(Calendar.WEEK_OF_YEAR);

        Log.i(TAG, "Previous week number " + previousWeek);
        Log.i(TAG, "Current week number " + currentWeek);
        Log.i(TAG, "Next week number " + nextWeek);*/

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
            WeekMenu[] menus = gson.fromJson(json, WeekMenu[].class);
            previousWeekMenu = menus[0];
            currentWeekMenu = menus[1];
            nextWeekMenu = menus[2];

            Log.i(TAG,json);
        } else {
            Log.i(TAG, "No saved file found");
            previousWeekMenu = new WeekMenu();
            currentWeekMenu = new WeekMenu();
            nextWeekMenu = new WeekMenu();
        }

        loadMenus();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save data here
        File file = new File(getFilesDir(), FILENAME);
        List<WeekMenu> menus = Arrays.asList(previousWeekMenu, currentWeekMenu, nextWeekMenu);
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
            loadMenus();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMenus() {
        Log.i(TAG, "Loading menus");
            /*menuTable.execute(new TableQueryCallback<WeekMenu>() {
                public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        for (WeekMenu wm : result) {
                            loadMenuItems(wm);
                        }
                    } else {
                        Log.e(TAG, exception.getMessage());
                    }
                }
            });*/
        menuTable.where().field("Week").eq(38).execute(new TableQueryCallback<WeekMenu>() {
            public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    for (WeekMenu wm : result) {
                        loadMenuItems(wm);
                    }
                } else {
                    Log.e(TAG, exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    private void loadMenuItems(final WeekMenu wm) {
        Log.i(TAG, "Loading items for " + wm.toString());

        MobileServiceQuery<TableQueryCallback<WeekMenuItem>> query = menuItemTable.where();
        query.setQueryText(String.format("ParentId eq guid'%s'", wm.getMenuId()));
        query.execute(new TableQueryCallback<WeekMenuItem>() {
            public void onCompleted(List<WeekMenuItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    for (WeekMenuItem item : result) {
                        Log.i(TAG, "Found item " + item.toString());
                    }
                    wm.setItems(result);
                    currentWeekMenu.Update(wm);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return WeekMenuFragment.newInstance(previousWeekMenu);
                case 1: return WeekMenuFragment.newInstance(currentWeekMenu);
                case 2: return WeekMenuFragment.newInstance(nextWeekMenu);
            }
            return null;
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
