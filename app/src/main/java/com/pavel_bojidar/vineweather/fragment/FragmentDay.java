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
import com.pavel_bojidar.vineweather.adapter.DayForecastAdapter;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;


/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentDay extends WeatherFragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.hourly_forecast);
        if (AppManager.getInstance().getCurrentLocation().getCurrentWeather() != null) {
            ArrayList<Forecast> dailyForecast = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                dailyForecast.add(AppManager.getInstance().getCurrentLocation().getForecasts().get(i));
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new DayForecastAdapter(dailyForecast));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Forecast> dailyForecast = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    dailyForecast.add(AppManager.getInstance().getCurrentLocation().getForecasts().get(i));
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                int currentLocationId = intent.getIntExtra(Constants.KEY_LOCATION_ID, -1);
                if (currentLocationId != -1 && recyclerView.getAdapter() != null) {
                    recyclerView.setAdapter(new DayForecastAdapter(dailyForecast));
                }
            }
        };
    }
}
