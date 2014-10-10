package com.lycilph.lunchviewer.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LogFragment extends ListFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> items = getLogItems();
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, items));
    }

    public List<String> getLogItems() {
        ArrayList<String> items = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time AzureService:V DownloadService:V PushNotificationHandler:V *:S");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                items.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }
}
