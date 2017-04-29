package com.pavel_bojidar.vineweather.model;

import java.io.Serializable;

public class DayDetails implements Serializable {

    private double maxtempC;
    private double maxtempF;
    private double mintempC;
    private double mintempF;
    private double avgtempC;
    private double avgtempF;
    private double maxwindMph;
    private double maxwindKph;
    private double totalprecipMm;
    private double totalprecipIn;
    private double avgVisibilityKm;
    private double avgVisibilityMiles;
    private double avgHumidity;
    private Condition condition;

    public double getMaxtempC() {
        return maxtempC;
    }

    public void setMaxtempC(double maxtempC) {
        this.maxtempC = maxtempC;
    }

    public double getMaxtempF() {
        return maxtempF;
    }

    public void setMaxtempF(double maxtempF) {
        this.maxtempF = maxtempF;
    }

    public double getMintempC() {
        return mintempC;
    }

    public void setMintempC(double mintempC) {
        this.mintempC = mintempC;
    }

    public double getMintempF() {
        return mintempF;
    }

    public void setMintempF(double mintempF) {
        this.mintempF = mintempF;
    }

    public double getAvgtempC() {
        return avgtempC;
    }

    public void setAvgtempC(double avgtempC) {
        this.avgtempC = avgtempC;
    }

    public double getAvgtempF() {
        return avgtempF;
    }

    public void setAvgtempF(double avgtempF) {
        this.avgtempF = avgtempF;
    }

    public double getMaxwindMph() {
        return maxwindMph;
    }

    public void setMaxwindMph(double maxwindMph) {
        this.maxwindMph = maxwindMph;
    }

    public double getMaxwindKph() {
        return maxwindKph;
    }

    public void setMaxwindKph(double maxwindKph) {
        this.maxwindKph = maxwindKph;
    }

    public double getTotalprecipMm() {
        return totalprecipMm;
    }

    public void setTotalprecipMm(double totalprecipMm) {
        this.totalprecipMm = totalprecipMm;
    }

    public double getTotalprecipIn() {
        return totalprecipIn;
    }

    public void setTotalprecipIn(double totalprecipIn) {
        this.totalprecipIn = totalprecipIn;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public double getAvgVisibilityKm() {
        return avgVisibilityKm;
    }

    public void setAvgVisibilityKm(double avgVisibilityKm) {
        this.avgVisibilityKm = avgVisibilityKm;
    }

    public double getAvgVisibilityMiles() {
        return avgVisibilityMiles;
    }

    public void setAvgVisibilityMiles(double avgVisibilityMiles) {
        this.avgVisibilityMiles = avgVisibilityMiles;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }
}
