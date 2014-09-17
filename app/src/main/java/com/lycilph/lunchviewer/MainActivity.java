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

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;

public class MainActivity extends Activity implements ActionBar.TabListener, WeekMenuFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

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

        /*WeekMenu menu = new WeekMenu(2014, 38);
        Gson gson = new Gson();
        String json = gson.toJson(menu);
        Log.i(TAG, json);

        File file = new File(getFilesDir(), "Temp.txt");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.close();
            Log.i(TAG, "File written");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*String json = new String();
        try {
            File file = new File(getFilesDir(), "Temp.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;

            while ((line = br.readLine()) != null) {
                json += line;
                Log.i(TAG, "Found line: " + line);
            }
            Log.i(TAG, json);

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        WeekMenu menu = gson.fromJson(json, WeekMenu.class);
        Log.i(TAG, "Menu found: " + menu.getYear() + "-" + menu.getWeek());*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Find week numbers
        Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        Log.i(TAG, "Current week number " + currentWeek);

        now.add(Calendar.WEEK_OF_YEAR, -1);
        int previousWeek = now.get(Calendar.WEEK_OF_YEAR);
        Log.i(TAG, "Previous week number " + previousWeek);

        now.add(Calendar.WEEK_OF_YEAR, 2);
        int nextWeek = now.get(Calendar.WEEK_OF_YEAR);
        Log.i(TAG, "Next week number " + nextWeek);

        // Check if data is current
        WeekMenuFragment fragment = mSectionsPagerAdapter.getFragment(1);
        if (fragment != null) {
            Log.i(TAG, "Got fragment for current week");

            if (fragment.getWeekNumber() == currentWeek) {
                Log.i(TAG, "Current week menu is valid");
            } else {
                Log.i(TAG, "Current week menu is invalid");
            }
        } else {
            Log.i(TAG, "No fragment for current week found");
        }

        // 1. Check current week (and date)
        // 2. Load saved data
        // 3. Update data if necessary
        // 4. Mark current date
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
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        protected Hashtable<Integer, WeakReference<WeekMenuFragment>> fragmentReferences = new Hashtable<Integer, WeakReference<WeekMenuFragment>>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Calendar now = Calendar.getInstance();
            int week = now.get(Calendar.WEEK_OF_YEAR);
            switch (position) {
                case 0:
                    now.add(Calendar.WEEK_OF_YEAR, -1);
                    week = now.get(Calendar.WEEK_OF_YEAR);
                    break;
                case 2:
                    now.add(Calendar.WEEK_OF_YEAR, 1);
                    week = now.get(Calendar.WEEK_OF_YEAR);
                    break;
            }
            WeekMenuFragment fragment = WeekMenuFragment.newInstance(week);
            fragmentReferences.put(position, new WeakReference<WeekMenuFragment>(fragment));
            return fragment;
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

        public WeekMenuFragment getFragment(int position)
        {
            WeakReference<WeekMenuFragment> ref = fragmentReferences.get(position);
            return ref == null ? null : ref.get();
        }
    }
}
