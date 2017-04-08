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
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentDetailFragment extends WeatherFragment {

    TextView humidity;
    TextView wind;
    TextView pressure;
    TextView visibility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_current_detail, container, false);

        humidity = (TextView) view.findViewById(R.id.today_humidity_percentage);
        wind = (TextView) view.findViewById(R.id.today_wind_detail);
        pressure = (TextView) view.findViewById(R.id.today_pressure_detail);
        visibility = (TextView) view.findViewById(R.id.today_visibility_detail);

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

        humidity.setText(currentLocation.getCurrentWeather().getHumidity() + " " + Constants.HUMIDITY_SYMBOL);
        wind.setText(currentLocation.getCurrentWeather().getWindKph() + " " + Constants.KM_H);
        pressure.setText(currentLocation.getCurrentWeather().getPressureMb() + Constants.KEY_PRESSURE_MBar);
        visibility.setText(currentLocation.getCurrentWeather().getVisability_km() + " " + Constants.KM);

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
