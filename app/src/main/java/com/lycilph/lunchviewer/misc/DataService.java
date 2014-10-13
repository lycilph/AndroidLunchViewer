package com.lycilph.lunchviewer.misc;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.models.WeekMenu;
import com.lycilph.lunchviewer.models.WeekMenuItem;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataService {
    private static final String TAG = "DataFragment";
    private static final String FILENAME = "menus.txt";

    private Context ctx;
    private List<WeekMenu> menus;

    public DataService(Context context) {
        ctx = context;
        clearAllMenus();
    }

    public List<WeekMenu> loadData() {
        Log.i(TAG, "Loading data");

        // Load file here and parse saved data
        File file = new File(ctx.getFilesDir(), FILENAME);
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
            clearAllMenus();
        }

        return menus;
    }

    public void saveData() {
        Log.i(TAG, "Saving data");

        // Save data here
        File file = new File(ctx.getFilesDir(), FILENAME);
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

    public void updateMenus() {
        boolean dataChanged = false;

        // Update previous menu
        int previousWeekNumber = DateUtils.getWeekNumber(-1);
        if (menus.get(0).getWeek() != previousWeekNumber) {
            menus.set(0, getMenuForWeek(previousWeekNumber));
            dataChanged = true;
        }

        // Update current menu
        int currentWeekNumber = DateUtils.getWeekNumber(0);
        if (menus.get(1).getWeek() != currentWeekNumber) {
            menus.set(1, getMenuForWeek(currentWeekNumber));
            dataChanged = true;
        }

        // Update next menu
        int nextWeekNumber = DateUtils.getWeekNumber(1);
        if (menus.get(2).getWeek() != nextWeekNumber) {
            menus.set(2, new WeekMenu(DateUtils.getYear(), nextWeekNumber));
            dataChanged = true;
        }

        if (dataChanged) {
            Intent intent = new Intent(ctx.getString(R.string.data_changed_event));
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        }
    }

    public boolean allEmpty() {
        for (WeekMenu menu : menus) {
            if (!menu.getItems().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void clearAllMenus() {
        menus = Arrays.asList(new WeekMenu(), new WeekMenu(), new WeekMenu());

        Intent intent = new Intent(ctx.getString(R.string.data_changed_event));
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    public WeekMenu getMenuForWeek(int weekNumber) {
        for (WeekMenu menu : menus) {
            if (menu.getWeek() == weekNumber)
                return menu;
        }

        int year = DateTime.now().year().get();
        return new WeekMenu(year, weekNumber);
    }

    public WeekMenuItem getMenuItemForDate(LocalDate nextDate) {
        for (WeekMenu menu : menus) {
            for (WeekMenuItem item : menu.getItems()) {
                if (nextDate.compareTo(item.getDate()) == 0) {
                    return item;
                }
            }
        }
        return null;
    }

    public WeekMenu getMenu(int position) {
        return menus.get(position);
    }

    public void setMenu(int position, WeekMenu menu) {
        menus.set(position, menu);
    }
}
