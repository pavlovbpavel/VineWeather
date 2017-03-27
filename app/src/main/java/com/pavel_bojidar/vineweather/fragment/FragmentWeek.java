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
import com.pavel_bojidar.vineweather.adapter.WeekForecastAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentWeek extends WeatherFragment {

    RecyclerView recyclerView;
    String currentDate;
    ArrayList<DayForecast> fiveDayForecast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.weekly_forecast);
        return view;
    }

    @Override
    public void onStart() {
        currentDate = Helper.getUnixDate(AppManager.getInstance().getCurrentLocation().getForecasts().get(0).getUnixTimestamp());
        fiveDayForecast = new ArrayList<>();
        ArrayList<Forecast> currentDayForecasts = new ArrayList<>();
        for (int i = 0; i < AppManager.getInstance().getCurrentLocation().getForecasts().size(); i++) {
            Forecast currentItem = AppManager.getInstance().getCurrentLocation().getForecasts().get(i);
            if (currentDate.equalsIgnoreCase(Helper.getUnixDate(currentItem.getUnixTimestamp()))) {
                currentDayForecasts.add(currentItem);
            } else {
                fiveDayForecast.add(new DayForecast(currentDayForecasts));
                currentDayForecasts = new ArrayList<>();
                currentDate = Helper.getUnixDate(currentItem.getUnixTimestamp());
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new WeekForecastAdapter(fiveDayForecast));
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
                Log.e("fragment week", "i received the broadcast");

//                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                int currentLocationId = intent.getIntExtra(Constants.KEY_LOCATION_ID, -1);
                if (currentLocationId != -1 && recyclerView.getAdapter() != null) {
//                    recyclerView.setAdapter(new WeekForecastAdapter(fiveDayForecast));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    protected String getFragment() {
        return "Fragment Week";
    }
}
