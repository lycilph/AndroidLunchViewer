package com.lycilph.lunchviewer.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.misc.DataService;
import com.lycilph.lunchviewer.misc.DateUtils;
import com.lycilph.lunchviewer.models.WeekMenu;
import com.lycilph.lunchviewer.models.WeekMenuItem;
import com.lycilph.lunchviewer.adapters.WeekMenuItemAdapter;
import com.lycilph.lunchviewer.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class WeekMenuFragment extends ListFragment {
    private static final String TAG = "WeekMenuFragment";
    private static final String ARG_POSITION = "ARG_POSITION";

    private OnItemSelectedListener listener;
    private WeekMenuItemAdapter adapter;

    private int position;

    public static WeekMenuFragment newInstance(int position) {
        WeekMenuFragment fragment = new WeekMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public WeekMenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }

        List<WeekMenuItem> items = new ArrayList<WeekMenuItem>();
        adapter = new WeekMenuItemAdapter(items, getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekmenu, container, false);

        // Set the adapter
        AbsListView listView = (AbsListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String eventName = getActivity().getString(R.string.data_changed_event);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, new IntentFilter(eventName));

        update();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (listener != null) {
            listener.onItemSelected(this.position, position);
        }
    }

    private void update() {
        MainActivity mainActivity = (MainActivity) getActivity();
        DataService dataService = mainActivity.getDataService();
        WeekMenu menu = dataService.getMenu(position);

        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.header);
            String header = String.format(getString(R.string.header_label), menu.getWeek());
            tv.setText(header);

            adapter.update(menu.getItems());
        } else {
            Log.i(TAG, "No view found for fragment belonging to menu " + menu.toString());
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Updating menu for position " + position);
            update();
        }
    };

    public interface OnItemSelectedListener {
        public void onItemSelected(int position, int item);
    }
}
