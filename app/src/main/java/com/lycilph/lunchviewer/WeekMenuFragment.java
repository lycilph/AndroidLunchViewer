package com.lycilph.lunchviewer;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class WeekMenuFragment extends ListFragment implements AbsListView.OnItemClickListener {

    private static final String ARG_WEEK_NUMBER = "-1";
    private int weekNumber;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private WeekMenuAdapter mAdapter;

    public static WeekMenuFragment newInstance(int week) {
        WeekMenuFragment fragment = new WeekMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WEEK_NUMBER, week);
        fragment.setArguments(args);
        return fragment;
    }

    public int getWeekNumber() { return weekNumber; }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeekMenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            weekNumber = getArguments().getInt(ARG_WEEK_NUMBER);
        }

        List<WeekMenu> wm = new ArrayList<WeekMenu>();

        mAdapter = new WeekMenuAdapter(wm, getActivity());

        /*try {
            MobileServiceClient mClient = new MobileServiceClient("https://lunchviewer.azure-mobile.net/", "SVzovNQtJGFXALLJDUskHXIZqDSBwL46", getActivity());
            MobileServiceTable<WeekMenu> menuTable = mClient.getTable("Menu", WeekMenu.class);

            menuTable.execute(new TableQueryCallback<WeekMenu>() {
                public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        mAdapter.Update(result);
                    } else {
                        Log.e("LunchViewer", exception.getMessage());
                    }
                }
            });

        } catch (MalformedURLException e) {
            Log.d("Test", e.getMessage());
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekmenu, container, false);

        // Get the header
        String header = String.format(getString(R.string.header_label), weekNumber);

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
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
