package com.lycilph.lunchviewer.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.lycilph.lunchviewer.R;

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

        setHasOptionsMenu(true);

        List<String> items = getLogItems();
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, items));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh_log: {
                updateLog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLog() {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)getListAdapter();
        adapter.clear();
        adapter.addAll(getLogItems());
        adapter.notifyDataSetChanged();
    }

    private List<String> getLogItems() {
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
