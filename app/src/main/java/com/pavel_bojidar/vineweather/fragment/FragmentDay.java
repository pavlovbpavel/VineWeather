package com.pavel_bojidar.vineweather.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.ForecastAdapter;

import com.pavel_bojidar.vineweather.singleton.AppManager;



/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentDay extends WeatherFragment{

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, null);
//        recyclerView = (RecyclerView) view.findViewById(R.id.hourly_forecast);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(new ForecastAdapter(AppManager.getOurInstance().getCurrentLocation().getForecasts()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(getLocationChangedReceiver(), new IntentFilter(BroadcastActions.ACTION_LOCATION_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(getLocationChangedReceiver());
    }

    @Override
    void onLocationChanged() {

    }

}
