package com.pavel_bojidar.vineweather.model.maindata;

import com.pavel_bojidar.vineweather.model.Condition;

import java.io.Serializable;

public class CurrentWeather implements Serializable {

    private int lastUpdatedEpoch;
    private int windDegree;
    private int humidity;
    private int cloud;
    private String lastUpdated;
    private String windDir;
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
    private int isDay;
    private double visabilityKm;
    private double visabilityMi;

    private Condition condition = new Condition();

    public int getLastUpdateEpoch() {
        return lastUpdatedEpoch;
    }

    public void setLastUpdateEpoch(int mLastUpdateEpoch) {
        this.lastUpdatedEpoch = mLastUpdateEpoch;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String mLastUpdated) {
        this.lastUpdated = mLastUpdated;
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

    public int getIsDay() {
        return isDay;
    }

    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    public double getVisabilityKm() {
        return visabilityKm;
    }

    public void setVisabilityKm(double visabilityKm) {
        this.visabilityKm = visabilityKm;
    }

    public double getVisabilityMi() {
        return visabilityMi;
    }

    public void setVisabilityMi(double visabilityMi) {
        this.visabilityMi = visabilityMi;
    }
}
