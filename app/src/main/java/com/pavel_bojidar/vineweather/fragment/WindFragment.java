package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyTempAdapter;
import com.pavel_bojidar.vineweather.adapter.HourlyWindAdapter;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class WindFragment extends WeatherFragment {

    RecyclerView rvWind;
    TextView condition, speed;
    Forecast forecast;
    static WindFragment instance;
    int index;

    public static WindFragment newInstance(int index) {
        WindFragment fragment = new WindFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        index = instance.getArguments().getInt("index");
        if (index == 1) {
            view = inflater.inflate(R.layout.fragment_wind_tomorrow, container, false);
            rvWind = (RecyclerView) view.findViewById(R.id.tomorrow_hourly_wind);
            condition = (TextView) view.findViewById(R.id.tomorrow_wind_condition);
            speed = (TextView) view.findViewById(R.id.tomorrow_wind_speed);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppManager.getInstance().getCurrentLocation() != null) {
            bindData();
        }
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bindData();
            }
        };
    }

    private void bindData() {
        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        if (index == 1) {
            rvWind.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvWind.setAdapter(new HourlyWindAdapter(forecast.getDayForecasts().get(1).getHourForecasts()));
        }
    }
}
