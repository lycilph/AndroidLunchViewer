package com.lycilph.lunchviewer.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lycilph.lunchviewer.R;

import java.util.Arrays;
import java.util.List;

public class NavigationDrawerFragment extends Fragment implements ListView.OnItemClickListener {
    private static final String TAG = "NavigationDrawerFragment";

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private OnNavigationDrawerInteractionListener listener;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private View fragmentContainerView;

    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;
    private String title;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the drawer
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        fromSavedInstanceState = (savedInstanceState != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        List<String> items = Arrays.asList(getResources().getStringArray(R.array.navigation_sections));
        ListView sectionsListView = (ListView) v.findViewById(R.id.sections_list);
        sectionsListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, items));
        sectionsListView.setOnItemClickListener(this);

        List<String> subItems = Arrays.asList(getResources().getStringArray(R.array.navigation_options));
        ListView optionsListView = (ListView) v.findViewById(R.id.options_list);
        optionsListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, subItems));
        optionsListView.addHeaderView(new View(getActivity()), null, true);
        optionsListView.addFooterView(new View(getActivity()), null, true);
        optionsListView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnNavigationDrawerInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNavigationDrawerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (drawerLayout != null && isDrawerOpen()) {
            menu.clear();
            inflater.inflate(R.menu.global, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(fragmentContainerView);
    }

    public void setUp() {
        fragmentContainerView = getActivity().findViewById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.navigation_drawer_layout);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                drawerLayout,                     /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                //getActivity().setTitle(title); // Restore title
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!userLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    userLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                // Save the title, so that it can be restored, when drawer closes
                title = getActivity().getTitle().toString();
                getActivity().setTitle(getString(R.string.app_name)); // Set "global" context title

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!userLearnedDrawer && !fromSavedInstanceState) {
            drawerLayout.openDrawer(fragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void updateActionBar(int backStackEntryCount) {
        drawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        drawerLayout.closeDrawer(fragmentContainerView);

        View parent = (View) view.getParent();
        if (parent.getId() == R.id.options_list) {
            switch (i) {
                case 1: {
                    listener.onNavigationInteraction(R.id.action_settings);
                    break;
                }
                case 2: {
                    listener.onNavigationInteraction(R.id.action_about);
                    break;
                }
            }
        } else {
            switch (i) {
                case 0: {
                    listener.onNavigationInteraction(R.id.action_home);
                    break;
                }
                case 1: {
                    listener.onNavigationInteraction(R.id.action_today);
                    break;
                }
                case 2: {
                    listener.onNavigationInteraction(R.id.action_next);
                    break;
                }
            }
        }
    }

    public interface OnNavigationDrawerInteractionListener {
        public void onNavigationInteraction(int id);
    }
}
