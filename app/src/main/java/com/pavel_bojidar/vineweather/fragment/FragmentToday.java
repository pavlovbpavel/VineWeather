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

    LinearLayout parent;
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

    RecyclerView recyclerView;
    Forecast forecast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todayy, null);

       // conditionImage = (ImageView) view.findViewById(R.id.condition_image_now);
       // description = (TextView) view.findViewById(R.id.fragment_1_description);
        degrees = (TextView) view.findViewById(R.id.fragment_1_degrees);
        condition = (TextView) view.findViewById(R.id.fragment_1_condition);
        pressure = (TextView) view.findViewById(R.id.fragment_1_pressure);
       // humidity = (TextView) view.findViewById(R.id.fragment_1_humidity);
      // windSpeed = (TextView) view.findViewById(R.id.fragment_1_wind_speed);
       // windDirection = (ImageView) view.findViewById(R.id.fragment_1_wind_direction);

        feelsLike = (TextView) view.findViewById(R.id.fragment_1_feels_like);
        date = (TextView) view.findViewById(R.id.fragment_1_date);

        recyclerView = (RecyclerView) view.findViewById(R.id.layout_rv_hours_forecast);

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

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CurrentDetailFragment fragmentCurrentDetails = new CurrentDetailFragment();

        fragmentTransaction.add(R.id.layout_current_detail, fragmentCurrentDetails);
        fragmentTransaction.add(R.id.layout_wind_detail, WindFragment.newInstance(0));
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
        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        DayForecast currentDay = forecast.getDayForecasts().get(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new HourlyTempAdapter(currentDay.getHourForecasts(), true));

        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void bindData() {

        SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM dd, hh:mm");
        simpleDate.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String format = simpleDate.format(new Date());

        Location currentLocation = AppManager.getInstance().getCurrentLocation();

        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC())  + Constants.CELSIUS_SYMBOL);
        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());
        feelsLike.setText("Feels like " + currentLocation.getCurrentWeather().getFeelslikeC());
        date.setText(format);
        //Helper.getUnixCustomDate(unixTS).substring(1)
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
