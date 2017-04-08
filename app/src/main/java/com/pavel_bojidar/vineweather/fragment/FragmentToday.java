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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        CurrentDetailFragment fragment = new CurrentDetailFragment();

        fragmentTransaction.add(R.id.layout_current_detail, fragment);
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
    }

    private void bindData() {
        Location currentLocation = AppManager.getInstance().getCurrentLocation();

        degrees.setText(Helper.decimalFormat(currentLocation.getCurrentWeather().getTempC())  + Constants.CELSIUS_SYMBOL);
        condition.setText(currentLocation.getCurrentWeather().getCondition().getText());
        feelsLike.setText("Feels like " + currentLocation.getCurrentWeather().getFeelslikeC());
        date.setText(Helper.getUnixCustomDate(currentLocation.getLocaltime_epoch()).substring(1) + ", " +
        Helper.getUnixHour(currentLocation.getLocaltime_epoch()));

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
