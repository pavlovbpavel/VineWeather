package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.model.Location;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public abstract class WeatherFragment extends Fragment {

    protected Location currentLocation;

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getReceiver(), new IntentFilter(BroadcastActions.ACTION_LOCATION_CHANGED));
    }

    protected abstract BroadcastReceiver getReceiver();

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(getReceiver());
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
