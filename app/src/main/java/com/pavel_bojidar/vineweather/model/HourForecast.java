package com.pavel_bojidar.vineweather.model;

import java.io.Serializable;

public class HourForecast implements Serializable {

    private int timeEpoch;
    private int windDegree;
    private int humidity;
    private int cloud;
    private int willItRain;
    private int willItSnow;
    private int isDay;
    private double tempC;
    private double tempF;
    private double windMph;
    private double windKph;
    private double pressureMb;
    private double pressureIn;
    private double precipMm;
    private double precipIn;
    private double feelslikeC;
    private double feelslikeF;
    private double windchillC;
    private double windchillF;
    private double heatindexC;
    private double heatindexF;
    private double dewpointC;
    private double dewpointF;
    private double visibilityKm;
    private double visibilityMiles;
    private String time;
    private String windDir;
    private Condition condition;

    public int getTimeEpoch() {
        return timeEpoch;
    }

    public void setTimeEpoch(int mTimeEpoch) {
        this.timeEpoch = mTimeEpoch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String mTime) {
        this.time = mTime;
    }

    public double getTempC() {
        return tempC;
    }

    public void setTempC(Double mTempC) {
        this.tempC = mTempC;
    }

    public double getTempF() {
        return tempF;
    }

    public void setTempF(Double mTempF) {
        this.tempF = mTempF;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition mCondition) {
        this.condition = mCondition;
    }

    public double getWindMph() {
        return windMph;
    }

    public void setWindMph(double mWindMph) {
        this.windMph = mWindMph;
    }

    public double getWindKph() {
        return windKph;
    }

    public void setWindKph(double mWindKph) {
        this.windKph = mWindKph;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(int mWindDegree) {
        this.windDegree = mWindDegree;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String mWindDir) {
        this.windDir = mWindDir;
    }

    public double getPressureMb() {
        return pressureMb;
    }

    public void setPressureMb(double mPressureMb) {
        this.pressureMb = mPressureMb;
    }

    public double getPressureIn() {
        return pressureIn;
    }

    public void setPressureIn(double mPressureIn) {
        this.pressureIn = mPressureIn;
    }

    public double getPrecipMm() {
        return precipMm;
    }

    public void setPrecipMm(double mPrecipMm) {
        this.precipMm = mPrecipMm;
    }

    public double getPrecipIn() {
        return precipIn;
    }

    public void setPrecipIn(double mPrecipIn) {
        this.precipIn = mPrecipIn;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int mHumidity) {
        this.humidity = mHumidity;
    }

    public int getCloud() {
        return cloud;
    }

    public void setCloud(int mCloud) {
        this.cloud = mCloud;
    }

    public double getFeelslikeC() {
        return feelslikeC;
    }

    public void setFeelslikeC(double mFeelslikeC) {
        this.feelslikeC = mFeelslikeC;
    }

    public double getFeelslikeF() {
        return feelslikeF;
    }

    public void setFeelslikeF(double mFeelslikeF) {
        this.feelslikeF = mFeelslikeF;
    }

    public double getWindchillC() {
        return windchillC;
    }

    public void setWindchillC(double mWindchillC) {
        this.windchillC = mWindchillC;
    }

    public double getWindchillF() {
        return windchillF;
    }

    public void setWindchillF(double mWindchillF) {
        this.windchillF = mWindchillF;
    }

    public double getHeatindexC() {
        return heatindexC;
    }

    public void setHeatindexC(double mHeatindexC) {
        this.heatindexC = mHeatindexC;
    }

    public double getHeatindexF() {
        return heatindexF;
    }

    public void setHeatindexF(double mHeatIndexF) {
        this.heatindexF = mHeatIndexF;
    }

    public double getDewpointC() {
        return dewpointC;
    }

    public void setDewpointC(double mDewpointC) {
        this.dewpointC = mDewpointC;
    }

    public double getDewpointF() {
        return dewpointF;
    }

    public void setDewpointF(double mDewpointF) {
        this.dewpointF = mDewpointF;
    }

    public int getWillItRain() {
        return willItRain;
    }

    public void setWillItRain(int mWillItRain) {
        this.willItRain = mWillItRain;
    }

    public int getWillItSnow() {
        return willItSnow;
    }

    public void setWillItSnow(int mWillItSnow) {
        this.willItSnow = mWillItSnow;
    }

    public int getIsDay() {
        return isDay;
    }

    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    public double getVisibilityKm() {
        return visibilityKm;
    }

    public void setVisibilityKm(double visibilityKm) {
        this.visibilityKm = visibilityKm;
    }

    public double getVisibilityMiles() {
        return visibilityMiles;
    }

    public void setVisibilityMiles(double visibilityMiles) {
        this.visibilityMiles = visibilityMiles;
    }
}
