package com.pavel_bojidar.vineweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.adapter.HourlyPrecipAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.Day;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;

import static com.pavel_bojidar.vineweather.Constants.IN;
import static com.pavel_bojidar.vineweather.Constants.IS_TOMORROW;
import static com.pavel_bojidar.vineweather.Constants.MM;
import static com.pavel_bojidar.vineweather.Constants.VOLUME_IN;
import static com.pavel_bojidar.vineweather.Constants.VOLUME_MM;

public class PrecipitationFragment extends WeatherFragment {

    private RecyclerView rvPrecipitation;
    private TextView volume, dailyVolume;
    protected ArrayList<HourForecast> hourlyPrecipForecast;

    public static PrecipitationFragment newInstance(boolean isTomorrow) {
        PrecipitationFragment fragment = new PrecipitationFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_TOMORROW, isTomorrow);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precipitation_fragment, container, false);
        rvPrecipitation = (RecyclerView) view.findViewById(R.id.precipitation_hourly);
        volume = (TextView) view.findViewById(R.id.precipitation_volume);
        dailyVolume = (TextView) view.findViewById(R.id.precipitation_daily_volume);
        return view;
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

    private void bindData() {
        boolean isTomorrow = getArguments().getBoolean(IS_TOMORROW);
        hourlyPrecipForecast = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(isTomorrow ? 1 : 0).getHourForecasts();
        rvPrecipitation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPrecipitation.setAdapter(new HourlyPrecipAdapter(hourlyPrecipForecast));
        Day currentDay = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(isTomorrow ? 1 : 0).getDay();
        if (WeatherActivity.isImperialUnits) {
            dailyVolume.setText("  ".concat(String.valueOf(currentDay.getTotalprecipIn() == 0 ? Helper.decimalFormat(currentDay.getTotalprecipIn()) : currentDay.getTotalprecipIn()).concat(IN)));
            volume.setText(VOLUME_IN);
        } else {
            dailyVolume.setText("  ".concat(String.valueOf(currentDay.getTotalprecipMm() == 0 ? Helper.decimalFormat(currentDay.getTotalprecipMm()) : currentDay.getTotalprecipMm()).concat(MM)));
            volume.setText(VOLUME_MM);
        }
    }
}
