package com.lycilph.lunchviewer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataFragment extends Fragment {
    private static final String TAG = "DataFragment";
    private static final String FILENAME = "menus.txt";

    List<WeekMenu> menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
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
