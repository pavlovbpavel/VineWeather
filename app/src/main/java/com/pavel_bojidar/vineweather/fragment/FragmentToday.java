package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyTempAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Pavel Pavlov on 3/7/2017.
 */

public class FragmentToday extends WeatherFragment {

    Forecast forecast;
    LinearLayout parent;
    DayForecast currentDay;
    Location currentLocation;
    RecyclerView recyclerView;
    ImageView weatherIcon, windDirection;
    TextView degrees, condition, pressure, windCondition, windSpeed, date, feelsLike;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todayy, null);

        date = (TextView) view.findViewById(R.id.fragment_1_date);
        windSpeed = (TextView) view.findViewById(R.id.today_wind_speed);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        pressure = (TextView) view.findViewById(R.id.fragment_1_pressure);
        weatherIcon = (ImageView) view.findViewById(R.id.fragment_1_image);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        feelsLike = (TextView) view.findViewById(R.id.fragment_1_feels_like);
        windCondition = (TextView) view.findViewById(R.id.today_wind_condition);
        windDirection = (ImageView) view.findViewById(R.id.today_wind_direction);

        recyclerView = (RecyclerView) view.findViewById(R.id.layout_rv_hours_forecast);

        currentLocation = AppManager.getInstance().getCurrentLocation();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        parent = (LinearLayout) getActivity().findViewById(R.id.today_parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;

        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        currentDay = forecast.getDayForecasts().get(0);
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CurrentDetailFragment fragmentCurrentDetails = new CurrentDetailFragment();

        fragmentTransaction.add(R.id.layout_current_detail, fragmentCurrentDetails);
        fragmentTransaction.add(R.id.layout_wind_detail, WindFragment.newInstance(false));
        fragmentTransaction.add(R.id.layout_precip_detail, PrecipitationFragment.newInstance(false));
        fragmentTransaction.commit();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppManager.getInstance().getCurrentLocation() != null) {
            bindData();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new HourlyTempAdapter(currentDay.getHourForecasts(), 0));

        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void bindData() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM dd, hh:mm");
        simpleDate.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String format = simpleDate.format(new Date());

        String weather = currentLocation.getCurrentWeather().getCondition().getText();
        if (currentLocation.getCurrentWeather().getIs_day() == 1) {
            switch (weather) {
                case Constants.SUNNY:
                    parent.setBackgroundResource(R.drawable.sunny_day);
                    break;
                case Constants.PARTLY_CLOUDY:
                    parent.setBackgroundResource(R.drawable.partly_cloudy_day);
                    break;
                case Constants.CLEAR:
                    parent.setBackgroundResource(R.drawable.clear_day);
                    break;
                case Constants.HEAVY_RAIN:
                    parent.setBackgroundResource(R.drawable.heavy_rain_day);
                    break;
            }
        }
        if (currentLocation.getCurrentWeather().getIs_day() == 0) {
            switch (weather) {
                case Constants.PARTLY_CLOUDY:
                    parent.setBackgroundResource(R.drawable.partly_cloudy_night);
                    break;
                case Constants.CLEAR:
                    parent.setBackgroundResource(R.drawable.clear_night);
                    break;
                case Constants.HEAVY_RAIN:
                    parent.setBackgroundResource(R.drawable.heavy_rain_night);
                    break;
            }
        }

        weatherIcon.setImageDrawable(Helper.chooseWeatherConditionIcon(parent.getContext(), currentLocation.getCurrentWeather().getIs_day() == 1,
                currentLocation.getCurrentWeather().getCondition().getIcon()));
        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC()).concat(Constants.CELSIUS_SYMBOL));
        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());
        feelsLike.setText("Feels like ".concat(Helper.decimalFormat(currentLocation.getCurrentWeather().getFeelslikeC())));
        date.setText(format);

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
