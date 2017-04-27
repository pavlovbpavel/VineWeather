package com.pavel_bojidar.vineweather.model;

import java.io.Serializable;

/**
 * Created by Pavel Pavlov on 4/1/2017.
 */

public class Astro implements Serializable {

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
}
