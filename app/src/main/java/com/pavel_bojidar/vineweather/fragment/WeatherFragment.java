package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavel_bojidar.vineweather.BroadcastActions;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public abstract class WeatherFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", getFragment());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getReceiver(), new IntentFilter(BroadcastActions.ACTION_LOCATION_UPDATED));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("onCreateView", getFragment());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", getFragment());
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(getReceiver());
    }


    protected abstract BroadcastReceiver getReceiver();

    protected abstract String getFragment();
}
