package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.model.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * Created by Pavel Pavlov on 3/7/2017.
 */

public class FragmentNow extends WeatherFragment{

    TextView degrees;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_now, null);
        degrees = (TextView) view.findViewById(R.id.fragment_1_text);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(AppManager.getOurInstance().getCurrentLocation() != null){
            degrees.setText(String.valueOf(AppManager.getOurInstance().getCurrentLocation().getForecasts().get(0).getTemperature()).concat(getResources().getString(R.string.c)));
        }
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getLocationChangedReceiver(), new IntentFilter(BroadcastActions.ACTION_LOCATION_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(getLocationChangedReceiver());
    }

    @Override
    void onLocationChanged() {
        degrees.setText(String.valueOf(AppManager.getOurInstance().getCurrentLocation().getForecasts().get(0).getTemperature()));
    }
}
