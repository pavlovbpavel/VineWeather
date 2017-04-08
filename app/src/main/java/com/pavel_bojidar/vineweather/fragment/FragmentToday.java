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
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * Created by Pavel Pavlov on 3/7/2017.
 */

public class FragmentToday extends WeatherFragment {

    TextView degrees;
    TextView description;
    TextView condition;
    TextView pressure;
    TextView humidity;
    TextView windSpeed;
    ImageView windDirection;
    ImageView conditionImage;

    TextView date;
    TextView feelsLike;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todayy, null);

        conditionImage = (ImageView) view.findViewById(R.id.condition_image_now);
        description = (TextView) view.findViewById(R.id.fragment_1_description);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        pressure = (TextView) view.findViewById(R.id.fragment_1_pressure);
        humidity = (TextView) view.findViewById(R.id.fragment_1_humidity);
        windSpeed = (TextView) view.findViewById(R.id.fragment_1_wind_speed);
        windDirection = (ImageView) view.findViewById(R.id.fragment_1_wind_direction);

        feelsLike = (TextView) view.findViewById(R.id.fragment_1_feels_like);
        date = (TextView) view.findViewById(R.id.fragment_1_date);

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

        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC()) + Constants.CELSIUS_SYMBOL);
        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());
        feelsLike.setText("Feels like " + currentLocation.getCurrentWeather().getFeelslikeC());
        date.setText(currentLocation.getLocaltime()+"");

      // pressure.setText("Pressure: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getPressureMb()) + Constants.PRESSURE_SYMBOL);
      // humidity.setText("Humidity: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getHumidity()) + Constants.HUMIDITY_SYMBOL);
      // windSpeed.setText("Wind: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getWindKph()) + Constants.KM_H);
      // windDirection.setRotation((float) currentLocation.getCurrentWeather().getWindDegree());
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("received broadcast: ", "fragment today");
                if (AppManager.getInstance().getCurrentLocation() != null) {
                    bindData();
                }
            }
        };
    }
}
