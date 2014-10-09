package com.lycilph.lunchviewer.fragments;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.misc.DataService;
import com.lycilph.lunchviewer.models.WeekMenuItem;
import com.lycilph.lunchviewer.activities.MainActivity;

public class WeekMenuItemFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_POSITION = "ARG_POSITION";
    private static final String ARG_ITEM = "ARG_ITEM";

    private int position;
    private int item;
    private WeekMenuItem weekMenuItem;

    public static WeekMenuItemFragment newInstance(int position, int item) {
        WeekMenuItemFragment fragment = new WeekMenuItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    public WeekMenuItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            item = getArguments().getInt(ARG_ITEM);
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        DataService dataService = mainActivity.getDataService();
        weekMenuItem = dataService.getMenu(position).getItem(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_menu_item, container, false);

        String day = weekMenuItem.getDate().dayOfWeek().getAsText();
        String date = weekMenuItem.getDate().toString();

        TextView dayTv = (TextView) view.findViewById(R.id.day);
        dayTv.setText(day);

        TextView dateTv = (TextView) view.findViewById(R.id.date);
        dateTv.setText(date);

        TextView textTv = (TextView) view.findViewById(R.id.text);
        textTv.setText(weekMenuItem.getText());

        Button buyButton = (Button) view.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            Intent showMenuIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weekMenuItem.getLink()));
            startActivity(showMenuIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
