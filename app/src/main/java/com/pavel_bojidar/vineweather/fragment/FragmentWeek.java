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
import com.pavel_bojidar.vineweather.adapter.WeekForecastAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentWeek extends WeatherFragment {

    RecyclerView recyclerView;
    String currentDate = Helper.getUnixDate(AppManager.getInstance().getCurrentLocation().getForecasts().get(0).getUnixTimestamp());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.weekly_forecast);
        ArrayList<Forecast> fiveDayForecast = new ArrayList<>();
        for (int i = 0; i < AppManager.getInstance().getCurrentLocation().getForecasts().size(); i++) {
            Forecast currentItem = AppManager.getInstance().getCurrentLocation().getForecasts().get(i);
            if (!currentDate.equalsIgnoreCase(Helper.getUnixDate(currentItem.getUnixTimestamp()))) {
                //todo calculate min - max temp for current day
                fiveDayForecast.add(AppManager.getInstance().getCurrentLocation().getForecasts().get(i));
                currentDate = Helper.getUnixDate(currentItem.getUnixTimestamp());
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new WeekForecastAdapter(fiveDayForecast));
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
                Log.e("fragment week", "i received the broadcast");
                int currentLocationId = intent.getIntExtra(Constants.KEY_LOCATION_ID, -1);
                if (currentLocationId != -1 && recyclerView.getAdapter() != null) {
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
