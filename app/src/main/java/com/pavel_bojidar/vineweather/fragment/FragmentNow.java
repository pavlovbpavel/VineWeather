package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * Created by Pavel Pavlov on 3/7/2017.
 */

public class FragmentNow extends WeatherFragment {

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
        if (AppManager.getOurInstance().getCurrentLocation() != null) {
            degrees.setText(String.valueOf(AppManager.getOurInstance().getCurrentLocation().getForecasts().get(0).getTemperature()).concat(getResources().getString(R.string.c)));
        }
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Receiver", "I received the message");
            }
        };
    }
}
