package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * Created by Pavel Pavlov on 3/7/2017.
 */

public class FragmentNow extends WeatherFragment {

    TextView degrees;
    TextView condition;
    TextView pressure;
    TextView humidity;
    TextView windSpeed;
    TextView windDirection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, null);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        pressure = (TextView) view.findViewById(R.id.fragment_1_pressure);
        humidity = (TextView) view.findViewById(R.id.fragment_1_humidity);
        windSpeed = (TextView) view.findViewById(R.id.fragment_1_wind_speed);
        windDirection = (TextView) view.findViewById(R.id.fragment_1_wind_direction);
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
                Log.e("fragment now", "i received the broadcast");
                if (AppManager.getInstance().getCurrentLocation() != null) {
                    bindData();
                }
            }
        };
    }

    @Override
    protected String getFragment() {
        return "Fragment Now";
    }

    private void bindData() {
        Location currentLocation = AppManager.getInstance().getCurrentLocation();
        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTemperature() - Constants.COEF_FOR_CONVERT_CELSIUS)+ "\u00b0");
        condition.setText(currentLocation.getCurrentWeather().getWeatherCondition());
        pressure.setText("Pressure: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getPressure()) + "Pa");
        humidity.setText("Humidity: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getHumidity()) + "%");
        windSpeed.setText("Wind: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getWindSpeed()) + "km/h");
        windDirection.setText(String.valueOf(currentLocation.getCurrentWeather().getWindDirection()));
    }
}
