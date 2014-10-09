package com.lycilph.lunchviewer.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.activities.MainActivity;
import com.lycilph.lunchviewer.misc.AzureService;
import com.lycilph.lunchviewer.misc.DataService;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting download of new data");

        DataService dataService = new DataService(this);
        AzureService azureService = new AzureService(this, dataService);

        String eventName = getString(R.string.update_finished_event);
        LocalBroadcastManager.getInstance(this).registerReceiver(updateDoneReceiver, new IntentFilter(eventName));

        azureService.updateAllMenus();

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("LunchViewer")
                        .setStyle(new Notification.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private BroadcastReceiver updateDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Download of new data finished");
            sendNotification("New data was downloaded");

            LocalBroadcastManager.getInstance(DownloadService.this).unregisterReceiver(updateDoneReceiver);
            stopSelf();
        }
    };
}
