package com.lycilph.lunchviewer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.lycilph.lunchviewer.R;

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = "SettingsFragment";

    public static final String SHOW_LOG_PREFERENCE = "pref_key_show_log_menu_item";
    public static final String SHOW_CLEAR_PREFERENCE = "pref_key_show_clear_menu_item";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "Creating menu");
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        String title = String.format("%s - Settings", getString(R.string.app_name));
        getActivity().setTitle(title);

        super.onResume();
    }

    public static boolean getShowLog(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SHOW_LOG_PREFERENCE, false);
    }

    public static boolean getShowClear(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SHOW_CLEAR_PREFERENCE, false);
    }
}
