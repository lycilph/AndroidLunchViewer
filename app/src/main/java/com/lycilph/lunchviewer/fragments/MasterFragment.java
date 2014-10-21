package com.lycilph.lunchviewer.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.lycilph.lunchviewer.misc.DataService;
import com.lycilph.lunchviewer.models.WeekMenu;

import java.util.Locale;

public class MasterFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private static final String TAG = "MasterFragment";

    private WeekMenuPagerAdapter pagerAdapter;

    public static MasterFragment newInstance() {
        return new MasterFragment();
    }
    public MasterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MasterFragment onCreate");

        super.onCreate(savedInstanceState);
        pagerAdapter = new WeekMenuPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "MasterFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_master, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(this);

        onPageSelected(1);

        return view;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "Page changed - current " + position);

        MainActivity activity = (MainActivity) getActivity();
        DataService dataService = activity.getDataService();
        WeekMenu weekMenu = dataService.getMenu(position);

        String appName = getString(R.string.app_name);
        String title = String.format("%s - Week %d", appName, weekMenu.getWeek());
        activity.setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class WeekMenuPagerAdapter extends FragmentPagerAdapter {
        public WeekMenuPagerAdapter(FragmentManager fm) {
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
