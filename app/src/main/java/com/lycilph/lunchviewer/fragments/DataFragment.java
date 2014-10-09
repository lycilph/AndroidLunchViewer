package com.lycilph.lunchviewer.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.lycilph.lunchviewer.misc.AzureService;
import com.lycilph.lunchviewer.misc.DataService;

public class DataFragment extends Fragment {
    private DataService dataService;
    private AzureService azureService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Context ctx = getActivity().getApplicationContext();
        dataService = new DataService(ctx);
        dataService.loadData();

        azureService = new AzureService(ctx, dataService);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataService.saveData();
    }

    public DataService getDataService() {
        return dataService;
    }

    public AzureService getAzureService() {
        return azureService;
    }
}
