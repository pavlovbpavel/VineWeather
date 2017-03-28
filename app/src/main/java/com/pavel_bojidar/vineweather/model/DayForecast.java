package com.pavel_bojidar.vineweather.model;

import com.pavel_bojidar.vineweather.model.Location.Forecast;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavel Pavlov on 3/25/2017.
 */

public class DayForecast {

    private List<Forecast> forecasts;

    public DayForecast(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public String getMidCondition() {

        HashMap<String, Integer> conditionCount = new HashMap<>();

        for (Forecast forecast : forecasts) {
            Integer count = conditionCount.get(forecast);
            if (count == null) {
                count = new Integer(0);
            }
            count++;
            conditionCount.put(forecast.getWeatherCondition(), count);
        }
        Map.Entry<String, Integer> mostRepeated = null;
        for (Map.Entry<String, Integer> e : conditionCount.entrySet()) {
            if (mostRepeated == null || mostRepeated.getValue() < e.getValue()){
                mostRepeated = e;
            }
        }
        if(mostRepeated != null){
            return mostRepeated.getKey();
        }
        return null;
    }

    public double getMidTemperature() {
        double tempSum = 0;
        for (Forecast forecast : forecasts) {
            tempSum += forecast.getTemperature();
        }
        return tempSum / forecasts.size();
    }

    public double getMinTemperature() {
        double tempMin = getMidTemperature();
        for (int i = 0; i < forecasts.size(); i++) {
            if(forecasts.get(i).getTemperature() < tempMin){
                tempMin = forecasts.get(i).getTemperature();
            }
        }
        return tempMin;
    }

    public double getMaxTemperature() {
        double tempMax = getMidTemperature();
        for (int i = 0; i < forecasts.size(); i++) {
            if(forecasts.get(i).getTemperature() > tempMax){
                tempMax = forecasts.get(i).getTemperature();
            }
        }
        return tempMax;
    }

    public List<Forecast> getForecasts() {
        return Collections.unmodifiableList(forecasts);
    }
}
