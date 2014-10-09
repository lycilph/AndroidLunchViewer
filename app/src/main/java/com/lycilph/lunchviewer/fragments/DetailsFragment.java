package com.lycilph.lunchviewer.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lycilph.lunchviewer.R;

public class DetailsFragment extends Fragment {
    private static final String ARG_POSITION = "ARG_POSITION";
    private static final String ARG_ITEM = "ARG_ITEM";

    private WeekMenuItemPagerAdapter pagerAdapter;

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

        ViewPager vp = (ViewPager) view.findViewById(R.id.pager);
        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(item);

        return view;
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
