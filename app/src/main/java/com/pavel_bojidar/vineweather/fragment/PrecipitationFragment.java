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

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyPrecipAdapter;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.Day;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 4/13/2017.
 */

public class PrecipitationFragment extends WeatherFragment {

    RecyclerView rvPrecipitation;
    TextView volume, dailyVolume;
    ArrayList<HourForecast> hourlyPrecipForecast;
    Day currentDay;

    public static PrecipitationFragment newInstance(ArrayList<HourForecast> hourlyPrecipForecast, Day day) {
        PrecipitationFragment fragment = new PrecipitationFragment();
        Bundle args = new Bundle();
        args.putSerializable("precipitation", hourlyPrecipForecast);
        args.putSerializable("currentDay", day);
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
    public void onStart() {
        super.onStart();
        if (AppManager.getInstance().getCurrentLocation() != null) {
            bindData();
        }
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
        hourlyPrecipForecast = (ArrayList<HourForecast>) getArguments().getSerializable("precipitation");
        currentDay = (Day) getArguments().getSerializable("currentDay");
        rvPrecipitation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPrecipitation.setAdapter(new HourlyPrecipAdapter(hourlyPrecipForecast));
        dailyVolume.setText("  ".concat(String.valueOf(currentDay.getTotalprecipMm() == 0 ? Helper.decimalFormat(currentDay.getTotalprecipMm()) : currentDay.getTotalprecipMm()).concat(" mm")));
        volume.setText("Volume\n(mm)");
    }
}
