package com.pavel_bojidar.vineweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;

import static com.pavel_bojidar.vineweather.BroadcastActions.ACTION_DOWNLOAD_FINISHED;

/**
 * Created by Pavel Pavlov on 4/29/2017.
 */

public class ForecastResponseReceiver extends BroadcastReceiver {

    private ForecastUpdate listener;

    public ForecastResponseReceiver(ForecastUpdate listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_DOWNLOAD_FINISHED)) {
            if (listener != null) {
                listener.onLocationUpdate(intent.getStringExtra("locationName"),
                        (CurrentWeather) intent.getSerializableExtra("currentWeatherResult"),
                        (Forecast) intent.getSerializableExtra("forecastResult"));
            }
        }
    }

    public interface ForecastUpdate {
        void onLocationUpdate(String currentLocationName, CurrentWeather currentWeather, Forecast forecast);
    }
}
