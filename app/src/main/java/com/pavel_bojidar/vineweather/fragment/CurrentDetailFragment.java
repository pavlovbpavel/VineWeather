package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentDetailFragment extends WeatherFragment {

    TextView humidity, wind, pressure, visibility, sunDetail, moonDetail;


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

    @Override
    public void onStart() {
        super.onStart();
        if (AppManager.getInstance().getCurrentLocation() != null) {
            bindData();
        }
    }

    private void bindData() {
        Location currentLocation = AppManager.getInstance().getCurrentLocation();

        humidity.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getHumidity()) + " " + Constants.HUMIDITY_SYMBOL);
        wind.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getWindKph()) + " " + Constants.KM_H);
        pressure.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getPressureMb()) + Constants.KEY_PRESSURE_MBar);
        visibility.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getVisability_km()) + " " + Constants.KM);
        sunDetail.setText(currentLocation.getForecast().getDayForecasts().get(0).getAstro().getSunrise().concat(", ")
                .concat(currentLocation.getForecast().getDayForecasts().get(0).getAstro().getSunset()));
        moonDetail.setText(currentLocation.getForecast().getDayForecasts().get(0).getAstro().getMoonrise().concat(", ")
                .concat(currentLocation.getForecast().getDayForecasts().get(0).getAstro().getMoonset()));
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
}
