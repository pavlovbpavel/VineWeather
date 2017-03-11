package com.pavel_bojidar.vineweather.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import com.pavel_bojidar.vineweather.R;

/**
 * Created by Pavel Pavlov on 3/10/2017.
 */

public class FragmentWeek extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week, null);
        return view;
    }
}
