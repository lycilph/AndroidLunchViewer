package com.lycilph.lunchviewer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

public class MasterFragment extends Fragment {
    private WeekMenuPagerAdapter pagerAdapter;

    public static MasterFragment newInstance() {
        return new MasterFragment();
    }
    public MasterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapter = new WeekMenuPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container, false);

        ViewPager vp = (ViewPager) view.findViewById(R.id.pager);
        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(1);

        return view;
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
