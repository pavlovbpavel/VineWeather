package com.pavel_bojidar.vineweather.fragment;


import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class WindFragment extends WeatherFragment {

    RecyclerView rvWind;
    Forecast forecast;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.fragment_wind, container, false);
        rvWind = (RecyclerView) row.findViewById(R.id.rv_wind);
        return row;
    }

    @Override
    public void onStart() {
        super.onStart();

        forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        rvWind.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return null;
    }


}
