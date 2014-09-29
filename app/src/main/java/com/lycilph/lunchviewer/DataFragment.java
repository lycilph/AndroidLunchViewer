package com.lycilph.lunchviewer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class DataFragment extends Fragment {
    public static final String FILENAME = "menus.txt";

    private static final String TAG = "DataFragment";

    private List<WeekMenu> menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Context ctx = getActivity().getApplicationContext();
        try {
            MobileServiceClient mobileClient = new MobileServiceClient("https://lunchviewer.azure-mobile.net/", "SVzovNQtJGFXALLJDUskHXIZqDSBwL46", ctx);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    public WeekMenu getMenu(int position) {
        return menus.get(position);
    }

    public void setMenu(int position, WeekMenu menu) {
        menus.set(position, menu);
    }

    private void loadData() {
        Log.i(TAG, "Loading data");

        // Load file here and parse saved data
        File file = new File(getActivity().getFilesDir(), FILENAME);
        if (file.exists()) {
            Log.i(TAG, "Reading saved file");

            String json = "";
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    json += line;
                }

                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            WeekMenu[] savedMenus = gson.fromJson(json, WeekMenu[].class);
            menus = Arrays.asList(savedMenus);
        } else {
            Log.i(TAG, "No saved file found");
            menus = Arrays.asList(new WeekMenu(), new WeekMenu(), new WeekMenu());
        }
    }

    private void saveData() {
        Log.i(TAG, "Saving data");

        // Save data here
        File file = new File(getActivity().getFilesDir(), FILENAME);
        Gson gson = new Gson();
        String json = gson.toJson(menus);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.close();
            Log.i(TAG, "File written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
