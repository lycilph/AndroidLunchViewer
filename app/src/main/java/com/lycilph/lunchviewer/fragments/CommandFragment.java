package com.lycilph.lunchviewer.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.lycilph.lunchviewer.misc.AzureService;

public class CommandFragment extends Fragment implements Button.OnClickListener {
    public CommandFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_command, container, false);

        Button sendNotificationButton = (Button) v.findViewById(R.id.action_send_notification);
        sendNotificationButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onClick(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        AzureService azureService = mainActivity.getAzureService();

        azureService.sendCommand(getString(R.string.command_send_notification));
    }
}
