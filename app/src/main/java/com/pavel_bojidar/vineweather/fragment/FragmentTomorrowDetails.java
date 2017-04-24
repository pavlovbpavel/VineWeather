package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.Astro;
import com.pavel_bojidar.vineweather.model.maindata.Day;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import static com.pavel_bojidar.vineweather.Constants.CELSIUS_SYMBOL;
import static com.pavel_bojidar.vineweather.Constants.HUMIDITY_SYMBOL;

/**
 * Created by Pavel Pavlov on 4/8/2017.
 */

public class FragmentTomorrowDetails extends WeatherFragment {

    TextView humidity, max, min, visibility, astro;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tomorrow_details, null);
        humidity = (TextView) view.findViewById(R.id.tomorrow_humidity_percentage);
        max = (TextView) view.findViewById(R.id.tomorrow_max_temp);
        min = (TextView) view.findViewById(R.id.tomorrow_min_temp);
        visibility = (TextView) view.findViewById(R.id.tomorrow_visibility);
        astro = (TextView) view.findViewById(R.id.tomorrow_astro);
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
        Day tomorrow = currentLocation.getForecast().getDayForecasts().get(1).getDay();
        Astro tomorrowAstro = currentLocation.getForecast().getDayForecasts().get(1).getAstro();

        humidity.setText(String.valueOf(Helper.decimalFormat(tomorrow.getAvgHumidity()).concat(HUMIDITY_SYMBOL)));
        if (WeatherActivity.isImperialUnits) {
            max.setText(String.valueOf(Helper.decimalFormat(tomorrow.getMaxtempF()).concat(CELSIUS_SYMBOL).concat(", ")));
            min.setText(String.valueOf(Helper.decimalFormat(tomorrow.getMintempF()).concat(CELSIUS_SYMBOL)));
            visibility.setText(String.valueOf(Helper.decimalFormat(tomorrow.getAvgVisibility_miles()).concat(" " + Constants.M)));
        } else {
            max.setText(String.valueOf(Helper.decimalFormat(tomorrow.getMaxtempC()).concat(CELSIUS_SYMBOL).concat(", ")));
            min.setText(String.valueOf(Helper.decimalFormat(tomorrow.getMintempC()).concat(CELSIUS_SYMBOL)));
            visibility.setText(String.valueOf(Helper.decimalFormat(tomorrow.getAvgVisibility_km()).concat(" " + Constants.KM)));
        }
        astro.setText(tomorrowAstro.getSunrise().concat(", ").concat(tomorrowAstro.getSunset()));
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
}
