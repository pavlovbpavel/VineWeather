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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyTempAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.Day;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;

import static com.pavel_bojidar.vineweather.Constants.ARROW_DOWN;
import static com.pavel_bojidar.vineweather.Constants.ARROW_UP;
import static com.pavel_bojidar.vineweather.Constants.CELSIUS_SYMBOL;
import static com.pavel_bojidar.vineweather.Constants.DAY;
import static com.pavel_bojidar.vineweather.Constants.INTERPUNKT;
import static com.pavel_bojidar.vineweather.Constants.NIGHT;


/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentTomorrow extends WeatherFragment {

    RelativeLayout mainLayout;
    RecyclerView hourlyTempForecast;
    ArrayList<HourForecast> tomorrowHourly;
    Day tomorrow;
    TextView date, condition, temp;
    ImageView conditionImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tomorrow, null);
        hourlyTempForecast = (RecyclerView) view.findViewById(R.id.tomorrow_hourly_temp);
        date = (TextView) view.findViewById(R.id.tomorrow_date);
        condition = (TextView) view.findViewById(R.id.tomorrow_condition);
        temp = (TextView) view.findViewById(R.id.tomorrow_max_min);
        conditionImage = (ImageView) view.findViewById(R.id.tomorrow_image);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        mainLayout = (RelativeLayout) getActivity().findViewById(R.id.main_layout_tomorrow);
        LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTomorrowDetails fragment = new FragmentTomorrowDetails();
        fragmentTransaction.add(R.id.tomorrow_details_container, fragment);
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
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bindData();
            }
        };
    }

    private void bindData() {
        tomorrowHourly = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getHourForecasts();
        tomorrow = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDay();
        int unixTS = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDateEpoch();
        date.setText(Helper.getWeekDay(Helper.getUnixDate(unixTS)).concat(Helper.getUnixCustomDate(unixTS)));
        condition.setText(tomorrow.getCondition().getText());
        temp.setText(DAY
                        .concat(String.valueOf(Helper.decimalFormat(tomorrow.getMaxtempC())
                        .concat(CELSIUS_SYMBOL).concat(ARROW_UP).concat(INTERPUNKT).concat(NIGHT)
                        .concat(Helper.decimalFormat(tomorrow.getMintempC()).concat(CELSIUS_SYMBOL).concat(ARROW_DOWN)))));
//        conditionImage.setImageResource(R.drawable.partly_cloudy);
        conditionImage.setImageDrawable(Helper.chooseIcon(getContext(), true, tomorrow.getCondition().getIcon()));
        hourlyTempForecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hourlyTempForecast.setAdapter(new HourlyTempAdapter(tomorrowHourly, true));
    }
}
