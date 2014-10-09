package com.lycilph.lunchviewer.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.lycilph.lunchviewer.fragments.DataFragment;
import com.lycilph.lunchviewer.models.WeekMenu;
import com.lycilph.lunchviewer.models.WeekMenuItem;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LunchViewerAppWidget extends AppWidgetProvider {
    private static final String TAG = "LunchViewerAppWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String day = DateTime.now().dayOfWeek().getAsText();
        String date = DateTime.now().toLocalDate().toString();

        //WeekMenu[] menus = loadMenus(context);
        //String text = getCurrentMenuItemText(menus);
        String text = "ABC";

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lunch_viewer_app_widget);
        views.setTextViewText(R.id.day, day);
        views.setTextViewText(R.id.date, date);
        views.setTextViewText(R.id.text, text);

        // Add click handler
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout_root, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static String getCurrentMenuItemText(WeekMenu[] menus) {
        if (menus != null) {
            int week = DateTime.now().weekOfWeekyear().get();
            for (WeekMenu menu : menus) {
                if (menu.getWeek() == week)
                    return getCurrentMenuItemText(menu);
            }
        }
        return "No menu found";
    }

    private static String getCurrentMenuItemText(WeekMenu menu) {
        LocalTime lunchEnd = DateTime.now().withTime(13, 15, 0, 0).toLocalTime();
        LocalTime now = DateTime.now().toLocalTime();
        LocalDate nextLunchDate = (lunchEnd.isBefore(now) ? DateTime.now().plusDays(1).toLocalDate() : DateTime.now().toLocalDate());

        for (WeekMenuItem item : menu.getItems()) {
            if (nextLunchDate.compareTo(item.getDate()) == 0) {
                return item.getText();
            }
        }
        return "No menu found";
    }

    /*private static WeekMenu[] loadMenus(Context context) {
        File file = new File(context.getFilesDir(), DataFragment.FILENAME);
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
            return gson.fromJson(json, WeekMenu[].class);

        } else {
            Log.i(TAG, "No file found");
            return null;
        }
    }*/
}


