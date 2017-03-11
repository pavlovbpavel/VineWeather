package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.model.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public abstract class WeatherFragment extends Fragment {

    protected Location currentLocation;

    protected BroadcastReceiver locationChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onLocationChanged();
            Log.d("Receiver", "I received the message");
        }
    };

    public BroadcastReceiver getLocationChangedReceiver() {
        return locationChangedReceiver;
    }

    abstract void onLocationChanged();

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
