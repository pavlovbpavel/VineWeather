package com.pavel_bojidar.vineweather.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DayForecast implements Serializable {

    private String date;
    private int dateEpoch;
    private DayDetails dayDetails;
    private ArrayList<HourForecast> hourForecasts;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDateEpoch() {
        return dateEpoch;
    }

    public void setDateEpoch(int dateEpoch) {
        this.dateEpoch = dateEpoch;
    }

    public DayDetails getDayDetails() {
        return dayDetails;
    }

    public void setDayDetails(DayDetails dayDetails) {
        this.dayDetails = dayDetails;
    }

    public ArrayList<HourForecast> getHourForecasts() {
        return hourForecasts;
    }

    public void setHourForecasts(ArrayList<HourForecast> mHourForecast) {
        this.hourForecasts = mHourForecast;
    }
}
