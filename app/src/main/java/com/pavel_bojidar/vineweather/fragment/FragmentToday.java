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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.adapter.HourlyTempAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.model.maindata.Location;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import static com.pavel_bojidar.vineweather.Constants.FEELS_LIKE;
import static com.pavel_bojidar.vineweather.Constants.LAST_UPDATED;

public class FragmentToday extends WeatherFragment {

    protected Forecast forecast;
    private RelativeLayout parent;
    protected DayForecast currentDay;
    private Location currentLocation;
    private RecyclerView recyclerView;
    protected ImageView weatherIcon, windDirection;
    protected TextView degrees, condition, windCondition, windSpeed, feelsLike, lastUpdated;
    protected FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todayy, null, false);
        windSpeed = (TextView) view.findViewById(R.id.today_wind_speed);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        weatherIcon = (ImageView) view.findViewById(R.id.fragment_1_image);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        feelsLike = (TextView) view.findViewById(R.id.fragment_1_feels_like);
        windCondition = (TextView) view.findViewById(R.id.today_wind_condition);
        windDirection = (ImageView) view.findViewById(R.id.today_wind_direction);
        recyclerView = (RecyclerView) view.findViewById(R.id.layout_rv_hours_forecast);
        lastUpdated = (TextView) view.findViewById(R.id.last_updated_tv);
        currentLocation = AppManager.getInstance().getCurrentLocation();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        parent = (RelativeLayout) getActivity().findViewById(R.id.today_parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;

        fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTodayDetails fragmentCurrentDetails = new FragmentTodayDetails();

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

    private void bindData() {

        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        currentDay = forecast.getDayForecasts().get(0);

        lastUpdated.setText(LAST_UPDATED.concat(AppManager.getInstance().getCurrentLocation().getCurrentWeather().getLastUpdated()));
        weatherIcon.setImageDrawable(Helper.chooseConditionIcon(weatherIcon.getContext(),
                currentLocation.getCurrentWeather().getIs_day() == 1, false,
                currentLocation.getCurrentWeather().getCondition().getText()));

        if (WeatherActivity.isImperialUnits) {
            degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempF()).concat(Constants.CELSIUS_SYMBOL));
            feelsLike.setText(FEELS_LIKE.concat(Helper.decimalFormat(currentLocation.getCurrentWeather().getFeelslikeF())).concat(Constants.CELSIUS_SYMBOL));
        } else {
            degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC()).concat(Constants.CELSIUS_SYMBOL));
            feelsLike.setText(FEELS_LIKE.concat(Helper.decimalFormat(currentLocation.getCurrentWeather().getFeelslikeC())).concat(Constants.CELSIUS_SYMBOL));
        }

        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new HourlyTempAdapter(currentDay.getHourForecasts(), 0));

        parent.setBackgroundDrawable(Helper.chooseFragmentBackground(parent.getContext(),
                currentLocation.getCurrentWeather().getCondition().getText(),
                currentLocation.getCurrentWeather().getIs_day() == 1));
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadcastActions.ACTION_UNIT_SWAPPED)) {
                    bindData();
                } else {
                    if (AppManager.getInstance().getCurrentLocation() != null) {
                        bindData();
                    }
                }
            }
        };
    }
}
