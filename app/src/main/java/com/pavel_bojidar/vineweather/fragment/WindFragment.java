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
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyWindAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import static com.pavel_bojidar.vineweather.Constants.KM_H;

/**
 * A simple {@link Fragment} subclass.
 */
public class WindFragment extends WeatherFragment {

    RecyclerView rvWind;
    TextView condition, speed;
    ImageView conditionImage;
    TextView currentWindSpeed, windDirection;
    Forecast forecast;
    boolean isTomorrow;
    CurrentWeather currentWeather;

    public static WindFragment newInstance(boolean isTomorrow) {
        WindFragment fragment = new WindFragment();
        Bundle args = new Bundle();
        args.putBoolean("isTomorrow", isTomorrow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        isTomorrow = getArguments().getBoolean("isTomorrow");
        if (isTomorrow) {
            view = inflater.inflate(R.layout.fragment_wind_tomorrow, container, false);
            rvWind = (RecyclerView) view.findViewById(R.id.tomorrow_hourly_wind);
            condition = (TextView) view.findViewById(R.id.tomorrow_wind_condition);
            speed = (TextView) view.findViewById(R.id.tomorrow_wind_speed);
        } else {
            view = inflater.inflate(R.layout.fragment_today_wind, container, false);
            rvWind = (RecyclerView) view.findViewById(R.id.rv_wind_today);
            condition = (TextView) view.findViewById(R.id.today_wind_condition);
            conditionImage = (ImageView) view.findViewById(R.id.today_wind_direction);
            currentWindSpeed = (TextView) view.findViewById(R.id.today_wind_speed);
            windDirection = (TextView) view.findViewById(R.id.today_wind_direction_string);
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
                if (AppManager.getInstance().getCurrentLocation() != null) {
                    bindData();
                }
            }
        };
    }

    private void bindData() {
        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();
        double maxWind = 0;
        double minWind = Integer.MAX_VALUE;
        double average;
        for (int i = 0; i < forecast.getDayForecasts().get(1).getHourForecasts().size(); i++) {
            HourForecast currentHour = forecast.getDayForecasts().get(1).getHourForecasts().get(i);
            if (currentHour.getWindKph() > maxWind) {
                maxWind = currentHour.getWindKph();
            }
            if (currentHour.getWindKph() < minWind) {
                minWind = currentHour.getWindKph();
            }
        }
        rvWind.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (isTomorrow) {
            rvWind.setAdapter(new HourlyWindAdapter(forecast.getDayForecasts().get(1).getHourForecasts()));
            speed.setText(Helper.decimalFormat(minWind).concat("-").concat(Helper.decimalFormat(maxWind).concat(" " + KM_H)));
            average = (minWind + maxWind) / 2;
            condition.setText(getCondition(average));
        } else {
            rvWind.setAdapter(new HourlyWindAdapter(forecast.getDayForecasts().get(0).getHourForecasts()));
            conditionImage.setRotation(currentWeather.getWindDegree());
            condition.setText(getCondition(currentWeather.getWindKph()));
            windDirection.setText(currentWeather.getWindDir());
            currentWindSpeed.setText(Helper.decimalFormat(currentWeather.getWindKph()));
        }
    }

    private String getCondition(double average) {
        if (average < 2) {
            return "Calm";
        }
        if (average > 1 && average < 7) {
            return "Light air";
        }
        if (average > 6 && average < 12) {
            return "Light breeze";
        }
        if (average > 11 && average < 21) {
            return "Gentle breeze";
        }
        if (average > 20 && average < 31) {
            return "Moderate breeze";
        }
        if (average > 30 && average < 41) {
            return "Fresh breeze";
        }
        if (average > 40 && average < 51) {
            return "Strong breeze";
        }
        if (average > 50 && average < 62) {
            return "Moderate gale";
        }
        if (average > 61 && average < 75) {
            return "Fresh gale";
        }
        if (average > 74 && average < 88) {
            return "Strong gale";
        }
        if (average > 87 && average < 103) {
            return "Whole gale";
        }
        if (average > 102 && average < 118) {
            return "Storm";
        }
        return "Hurricane";
    }
}
