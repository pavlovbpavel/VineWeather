package com.pavel_bojidar.vineweather.model;

import com.pavel_bojidar.vineweather.model.maindata.Day;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 3/25/2017.
 */

public class DayForecast implements Serializable{

    private String date;
    private int date_epoch;
    private Day day = new Day();
    private Astro astro = new Astro();
    private ArrayList<HourForecast> hourForecasts = new ArrayList<>();

    public String getDate()
    {
        return date;
    }

    public void setDate(String mDate)
    {
        this.date = mDate;
    }

    public int getDateEpoch()
    {
        return date_epoch;
    }

    public void setDateEpoch(int mDateEpoch)
    {
        this.date_epoch = mDateEpoch;
    }

    public Day getDay()
    {
        return day;
    }

    public void setDay(Day mDay)
    {
        this.day = mDay;
    }

    public Astro getAstro()
    {
        return astro;
    }

    public void setAstro(Astro mAstro)
    {
        this.astro = mAstro;
    }

    public ArrayList<HourForecast> getHourForecasts()
    {
        return hourForecasts;
    }

    public void setHourForecasts(ArrayList<HourForecast> mHourForecast) {
        this.hourForecasts = mHourForecast;
    }
}
