package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.ForecastAdapter;
import com.pavel_bojidar.vineweather.singleton.AppManager;


/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentDay extends WeatherFragment {

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.hourly_forecast);
        return view;
    }

    @Override
    public void onStart() {
        //register receiver
        super.onStart();
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Receiver", "I am fragment day");
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                int currentLocationId = intent.getIntExtra(Constants.KEY_LOCATION_ID, - 1);
                if (currentLocationId != -1) {
                    recyclerView.setAdapter(new ForecastAdapter(AppManager.getInstance().getLocations().get(currentLocationId).getForecasts()));
                }
            }
        };
    }
}
