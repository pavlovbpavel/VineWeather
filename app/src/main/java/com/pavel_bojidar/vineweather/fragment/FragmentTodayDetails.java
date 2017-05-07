package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

public class FragmentTodayDetails extends WeatherFragment {

    private TextView humidity, wind, pressure, visibility, sunDetail, moonDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_detail, container, false);
        humidity = (TextView) view.findViewById(R.id.today_humidity_percentage);
        wind = (TextView) view.findViewById(R.id.today_wind_detail);
        pressure = (TextView) view.findViewById(R.id.today_pressure_detail);
        visibility = (TextView) view.findViewById(R.id.today_visibility_detail);
        sunDetail = (TextView) view.findViewById(R.id.today_sun_detail);
        moonDetail = (TextView) view.findViewById(R.id.today_moon_detail);
        return view;
    }

    private void bindData() {
        Location currentLocation = AppManager.getInstance().getCurrentLocation();
        humidity.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getHumidity()) + " " + Constants.HUMIDITY_SYMBOL);
        if (WeatherActivity.isImperialUnits) {
            wind.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getWindMph()) + " " + Constants.MPH);
            pressure.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getPressureIn()).concat(Constants.IN));
            visibility.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getVisabilityMi()) + " " + Constants.M);
        } else {
            wind.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getWindKph()) + " " + Constants.KM_H);
            pressure.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getPressureMb()).concat(Constants.KEY_PRESSURE_MBar));
            visibility.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getVisabilityKm()) + " " + Constants.KM);
        }
        sunDetail.setText(currentLocation.getForecast().getDayForecasts().get(0).getSunrise().concat(", ")
                .concat(currentLocation.getForecast().getDayForecasts().get(0).getSunset()));
        moonDetail.setText(currentLocation.getForecast().getDayForecasts().get(0).getMoonrise().concat(", ")
                .concat(currentLocation.getForecast().getDayForecasts().get(0).getMoonset()));
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AppManager.getInstance().getCurrentLocation() != null) {
                    if (intent.getAction().equals(BroadcastActions.ACTION_UNIT_SWAPPED) || intent.getAction().equals(BroadcastActions.ACTION_LOCATION_UPDATED)) {
                        bindData();
                    }
                }
            }
        };
    }
}
