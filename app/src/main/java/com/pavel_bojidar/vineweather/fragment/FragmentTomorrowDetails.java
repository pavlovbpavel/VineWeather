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

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayDetails;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import static com.pavel_bojidar.vineweather.Constants.CELSIUS_SYMBOL;
import static com.pavel_bojidar.vineweather.Constants.HUMIDITY_SYMBOL;

public class FragmentTomorrowDetails extends WeatherFragment {

    private TextView humidity, max, min, visibility, astro;

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

    private void bindData() {
        Location currentLocation = AppManager.getInstance().getCurrentLocation();
        DayForecast tomorrowForecast = currentLocation.getForecast().getDayForecasts().get(1);
        DayDetails tomorrowDetails = currentLocation.getForecast().getDayForecasts().get(1).getDayDetails();
        humidity.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getAvgHumidity()).concat(HUMIDITY_SYMBOL)));
        if (WeatherActivity.isImperialUnits) {
            max.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getMaxtempF()).concat(CELSIUS_SYMBOL).concat(", ")));
            min.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getMintempF()).concat(CELSIUS_SYMBOL)));
            visibility.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getAvgVisibilityMiles()).concat(" " + Constants.M)));
        } else {
            max.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getMaxtempC()).concat(CELSIUS_SYMBOL).concat(", ")));
            min.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getMintempC()).concat(CELSIUS_SYMBOL)));
            visibility.setText(String.valueOf(Helper.decimalFormat(tomorrowDetails.getAvgVisibilityKm()).concat(" " + Constants.KM)));
        }
        astro.setText(tomorrowForecast.getSunrise().concat(", ").concat(tomorrowForecast.getSunset()));
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
