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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, null);
        conditionImage = (ImageView) view.findViewById(R.id.condition_image_now);
        description = (TextView) view.findViewById(R.id.fragment_1_description);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        pressure = (TextView) view.findViewById(R.id.fragment_1_pressure);
        humidity = (TextView) view.findViewById(R.id.fragment_1_humidity);
        windSpeed = (TextView) view.findViewById(R.id.fragment_1_wind_speed);
        windDirection = (ImageView) view.findViewById(R.id.fragment_1_wind_direction);
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
//        String units = AppManager.getInstance().getUnits();
//        if (currentLocation.getCurrentWeather() == null) {
//            return;
//        }
//        if (units.equals(Constants.KEY_CELSIUS)) {
//            degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTemperature() - Constants.COEF_FOR_CONVERT_CELSIUS) + Constants.CELSIUS_SYMBOL);
//        } else {
//            degrees.setText(Helper.decimalFormat(Helper.kelvinToFahrenheit(currentLocation.getCurrentWeather().getTemperature())) + Constants.FAHRENHEIT_SYMBOL);
//        }
        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC()) + Constants.CELSIUS_SYMBOL);
        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());
        pressure.setText("Pressure: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getPressureMb()) + Constants.PRESSURE_SYMBOL);
        humidity.setText("Humidity: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getHumidity()) + Constants.HUMIDITY_SYMBOL);
        windSpeed.setText("Wind: " + Helper.decimalFormat(currentLocation.getCurrentWeather().getWindKph()) + Constants.KM_H);
        windDirection.setRotation((float) currentLocation.getCurrentWeather().getWindDegree());
//        description.setText("Description: " + currentLocation.getCurrentWeather().getWeatherConditionDescription());
//
//        switch (currentLocation.getCurrentWeather().getWeatherCondition()) {
//
//            case "Rain":
//                conditionImage.setBackgroundResource(R.drawable.drizzle);
//                break;
//            case "Clouds":
//                conditionImage.setBackgroundResource(R.drawable.cloudy);
//                break;
//            case "Clear":
//                if(Helper.isNight(currentLocation.getCurrentWeather().getUnixTimestamp())){
//                    conditionImage.setBackgroundResource(R.drawable.night);
//                } else {
//                    conditionImage.setBackgroundResource(R.drawable.clear);
//                }
//                break;
//            case "Snow":
//                conditionImage.setBackgroundResource(R.drawable.snow);
//                break;
//            case "Fog":
//                conditionImage.setBackgroundResource(R.drawable.fog);
//                break;
//            case "Mist":
//                conditionImage.setBackgroundResource(R.drawable.mist);
//        }

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
