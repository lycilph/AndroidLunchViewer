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
import com.lycilph.lunchviewer.models.WeekMenuItem;

public class DetailsFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private static final String TAG = "DetailsFragment";

    private static final String ARG_POSITION = "ARG_POSITION";
    private static final String ARG_ITEM = "ARG_ITEM";

    private WeekMenuItemPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private int position;
    private int item;

    public static DetailsFragment newInstance(int position, int item) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            item = getArguments().getInt(ARG_ITEM);
        }

        pagerAdapter = new WeekMenuItemPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);

        showItem(item);

        return view;
    }

    public void showItem(int i) {
        if (viewPager.getCurrentItem() == i) {
            onPageSelected(i);
        } else {
            viewPager.setCurrentItem(i);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int item) {
        Log.i(TAG, "Page changed - current " + item);

        MainActivity activity = (MainActivity) getActivity();
        DataService dataService = activity.getDataService();
        WeekMenu weekMenu = dataService.getMenu(position);
        WeekMenuItem weekMenuItem = weekMenu.getItem(item);

        String appName = getString(R.string.app_name);
        String day = weekMenuItem.getDate().dayOfWeek().getAsText();
        String title = String.format("%s - %s (%s)", appName, weekMenuItem.getDate(), day);
        activity.setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public class WeekMenuItemPagerAdapter extends FragmentPagerAdapter {
        public WeekMenuItemPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return WeekMenuItemFragment.newInstance(DetailsFragment.this.position, position);
        }

        @Override
        public int getCount() { return 5; }
    }
}
