package com.pavel_bojidar.vineweather.model.maindata;

import com.pavel_bojidar.vineweather.model.DayForecast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 4/1/2017.
 */

public class Forecast implements Serializable {

    public ArrayList<DayForecast> dayForecasts = new ArrayList<>();

    public ArrayList<DayForecast> getDayForecasts() {
        return dayForecasts;
    }

    public void setDayForecasts(ArrayList<DayForecast> dayForecasts) {
        this.dayForecasts = dayForecasts;
    }
}