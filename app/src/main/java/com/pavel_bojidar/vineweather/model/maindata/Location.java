package com.pavel_bojidar.vineweather.model.maindata;


import java.io.Serializable;

public class Location implements Serializable {

    private String name;

    private CurrentWeather currentWeather;

    private Forecast forecast;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }
}