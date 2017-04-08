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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.pavel_bojidar.vineweather.R;


/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentTomorrow extends WeatherFragment {

    LinearLayout mainLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tomorrow, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        mainLayout = (LinearLayout) getActivity().findViewById(R.id.main_layout_tomorrow);
        LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
        AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        int appbarHeight = appbar.getHeight();
        params.height = screenHeight - getStatusBarHeight() - appbarHeight;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTomorrowDetails fragment = new FragmentTomorrowDetails();
        fragmentTransaction.add(R.id.layout2, fragment);
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
                Log.e("received broadcast: ", "fragment tomorrow");

            }
        };
    }
}
