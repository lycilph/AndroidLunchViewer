package com.lycilph.lunchviewer.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.lycilph.lunchviewer.misc.DataService;
import com.lycilph.lunchviewer.misc.DateUtils;
import com.lycilph.lunchviewer.models.WeekMenuItem;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class LunchViewerAppWidget extends AppWidgetProvider {
    private static final String TAG = "LunchViewerAppWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "Updating widget");

        // Load data
        DataService dataService = new DataService(context);
        dataService.loadData();

        LocalDate nextDate = DateUtils.getNextValidDate();
        WeekMenuItem item = dataService.getMenuItemForDate(nextDate);

        String day = nextDate.dayOfWeek().getAsText();
        String date = nextDate.toString();
        String text = (item == null ? "No menu found!" : item.getText());

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
}


