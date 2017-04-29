package com.pavel_bojidar.vineweather.model.maindata;

import com.pavel_bojidar.vineweather.model.DayForecast;

import java.io.Serializable;
import java.util.ArrayList;

public class Forecast implements Serializable {

    private ArrayList<DayForecast> dayForecasts = new ArrayList<>();

    public ArrayList<DayForecast> getDayForecasts() {
        return dayForecasts;
    }

    public void setDayForecasts(ArrayList<DayForecast> dayForecasts) {
        this.dayForecasts = dayForecasts;
    }
}