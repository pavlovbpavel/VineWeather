package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavel_bojidar.vineweather.BroadcastActions;

public abstract class WeatherFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastActions.ACTION_LOCATION_UPDATED);
        intentFilter.addAction(BroadcastActions.ACTION_UNIT_SWAPPED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getReceiver(), intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(getReceiver());
    }

    protected abstract BroadcastReceiver getReceiver();
}
