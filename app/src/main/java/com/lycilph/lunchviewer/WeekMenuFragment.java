package com.lycilph.lunchviewer;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WeekMenuFragment extends ListFragment implements AbsListView.OnItemClickListener, WeekMenu.onWeekMenuUpdatedListener {
    private static final String TAG = "WeekMenuFragment";
    private static final String ARG_WEEK_MENU = "ARG_POSITION";
    private WeekMenu weekMenu;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private WeekMenuAdapter mAdapter;

    public static WeekMenuFragment newInstance(WeekMenu wm) {
        WeekMenuFragment fragment = new WeekMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEEK_MENU, wm.toJson());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeekMenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            weekMenu = WeekMenu.fromJson(getArguments().getString(ARG_WEEK_MENU));
            weekMenu.setUpdateListener(this);
        }

        //MainActivity ma = (MainActivity) getActivity();
        //WeekMenu currentWeekMenu = ma.currentWeekMenu;

        List<WeekMenu> wm = new ArrayList<WeekMenu>();

        mAdapter = new WeekMenuAdapter(wm, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekmenu, container, false);

        // Get the header
        String header = String.format(getString(R.string.header_label), weekMenu.getWeek());

        // Set the header
        TextView tv = (TextView) view.findViewById(R.id.header);
        tv.setText(header);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
            mListener.onFragmentInteraction("Test");
        }
    }

    @Override
    public void onUpdated() {
        Log.i(TAG, "Menu updated");
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }
}
