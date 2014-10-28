package com.lycilph.lunchviewer.misc;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.lycilph.lunchviewer.R;
import com.lycilph.lunchviewer.models.WeekMenu;
import com.lycilph.lunchviewer.models.WeekMenuItem;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.ApiOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceQuery;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AzureService {
    private static final String TAG = "AzureService";

    private Context context;
    private DataService dataService;

    private MobileServiceClient mobileClient;
    private MobileServiceTable<WeekMenu> menuTable;

    private boolean[] status;

    public AzureService(Context context, DataService dataService) {
        Log.i(TAG, "AzureService constructor");

        this.context = context;
        this.dataService = dataService;

        status = new boolean[3];

        try {
            Log.i(TAG, "Creating mobile service client");
            //noinspection SpellCheckingInspection
            mobileClient = new MobileServiceClient("https://lunchviewer.azure-mobile.net/", "aTdlxVHCxVFVJRZUavPFzofedYUkyl29", context);
            menuTable = mobileClient.getTable("Menu", WeekMenu.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void registerForPush(final String gcmRegistrationId) {
        mobileClient.getPush().register(gcmRegistrationId,null,new RegistrationCallback() {
            @Override
            public void onRegister(Registration registration, Exception exception) {
                if (exception != null) {
                    Log.e(TAG, "Registration exception: " + exception.getMessage());
                    exception.printStackTrace();
                }
                Log.i(TAG, String.format("Registration of push notifications done [%s]", gcmRegistrationId));
            }
        });
    }

    public void updateAllMenus() {
        Log.i(TAG, "Menus update starting");

        Intent intent = new Intent(context.getString(R.string.update_started_event));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        Arrays.fill(status, false);
        for (int position = 0; position < 3; position++) {
            WeekMenu menu = dataService.getMenu(position);
            int weekOffset = position-1;
            DateTime dt = DateTime.now().plusWeeks(weekOffset);
            int week = dt.weekOfWeekyear().get();
            int year = dt.year().get();
            if (menu.getWeek() != week || menu.getItems().isEmpty()) {
                Log.i(TAG, "Menu out of date or empty");
                downloadMenu(position, week, year);
            } else {
                Log.i(TAG, "Menu up to date");
                updateDone(position, null);
            }
        }
    }

    private void updateDone(int position, WeekMenu menu) {
        status[position] = true;

        if (menu != null)
            dataService.setMenu(position, menu);

        // If all menus have been updated
        if (allTrue(status)) {
            Log.i(TAG, "Menus update finished");

            dataService.saveData();

            Intent intent = new Intent(context.getString(R.string.update_finished_event));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            intent = new Intent(context.getString(R.string.data_changed_event));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private void downloadMenu(final int position, final int week, final int year) {
        final String datePart = String.format("week %d of year %d", week, year);
        Log.i(TAG, "Downloading menu for " + datePart);

        menuTable.where().field("week").eq(week)
                   .and().field("year").eq(year)
                .execute(new TableQueryCallback<WeekMenu>() {
                    public void onCompleted(List<WeekMenu> result, int count, Exception exception, ServiceFilterResponse response) {
                        Log.i(TAG, "Download execute done");

                        if (exception == null) {
                            if (result.isEmpty()) {
                                Log.i(TAG, "No menu found for " + datePart);
                                updateDone(position, new WeekMenu(year, week));
                                return;
                            }

                            if (result.size() > 1) {
                                Log.i(TAG, "Multiple menus found for " + datePart + " (only first is used, total " + result.size() + ")");
                            } else {
                                Log.i(TAG, "Found menu for " + datePart);
                            }

                            updateDone(position, result.get(0));
                        } else {
                            Log.e(TAG, "Download error: " + exception.getMessage());
                            exception.printStackTrace();
                        }
                    }
                });
    }

    private static boolean allTrue(boolean[] values) {
        for (boolean value : values) {
            if (!value)
                return false;
        }
        return true;
    }

    public void sendCommand(final String command) {
        List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
        parameters.add(new Pair<String, String>("command", "SendNotification"));

        mobileClient.invokeApi("Command", "Post", parameters, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement jsonElement, Exception e, ServiceFilterResponse serviceFilterResponse) {
                String message = (e == null ? String.format("Command %s sent", command) : "Error: " + e.getMessage());
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
