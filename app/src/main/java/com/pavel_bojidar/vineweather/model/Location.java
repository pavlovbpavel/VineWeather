package com.pavel_bojidar.vineweather.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Location implements Serializable{

    private String location;
    private int id;
    private Forecast currentWeather;
    private List<Forecast> forecasts = new ArrayList<>();


    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(JSONArray forecastsArray) {
        forecasts.clear();
        for (int i = 0; i < forecastsArray.length(); i++) {
            try {
                forecasts.add(new Forecast(forecastsArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentWeather(JSONObject json) throws JSONException {
        this.currentWeather = new Forecast(json);
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Forecast getCurrentWeather() {
        return currentWeather;
    }


    public static class Forecast {
        private static final double COEF_FOR_CONVERT_CELSIUS = 273.15;
        private static final String JSON_NODE_MAIN = "main";
        private static final String JSON_NODE_WIND = "wind";
        private static final String JSON_NODE_CLOUDS = "clouds";
        private static final String JSON_NODE_WEATHER = "weather";
        private static final String JSON_KEY_TIMESTAMP = "dt";
        private static final String JSON_KEY_TEMPERATURE = "temp";
        private static final String JSON_KEY_HUMIDITY = "humidity";
        private static final String JSON_KEY_PRESSURE = "pressure";
        private static final String JSON_KEY_WIND_SPEED = "speed";
        private static final String JSON_KEY_WIND_DEGREES = "deg";
        private static final String JSON_KEY_CLOUDS = "all";
        private static final String JSON_KEY_WEATHER_ID = "id";
        private static final String JSON_KEY_WEATHER_CONDITION = "main";
        private static final String JSON_KEY_WEATHER_DESCRIPTION = "description";
        private static final String JSON_KEY_WEATHER_ICON = "icon";

        private long unixTimestamp;
        private double humidity;
        private double pressure;
        private double temperature;
        private double windSpeed;
        private double windDirection;
        private double clouds;
        private int weatherConditionId;
        private String weatherCondition;
        private String weatherConditionDescription;
        private String weatherConditionIcon;

        private String date;

        Forecast(JSONObject jsonObject) throws JSONException {
            this.unixTimestamp = jsonObject.getLong(JSON_KEY_TIMESTAMP);
            this.temperature = jsonObject.getJSONObject(JSON_NODE_MAIN).getDouble(JSON_KEY_TEMPERATURE);
            this.humidity = jsonObject.getJSONObject(JSON_NODE_MAIN).getDouble(JSON_KEY_HUMIDITY);
            this.pressure = jsonObject.getJSONObject(JSON_NODE_MAIN).getDouble(JSON_KEY_PRESSURE);
            this.windSpeed = jsonObject.getJSONObject(JSON_NODE_WIND).getDouble(JSON_KEY_WIND_SPEED);
            this.windDirection = jsonObject.getJSONObject(JSON_NODE_WIND).getDouble(JSON_KEY_WIND_DEGREES);
            this.clouds = jsonObject.getJSONObject(JSON_NODE_CLOUDS).getDouble(JSON_KEY_CLOUDS);
            this.date = jsonObject.getString("dt_txt");

            JSONArray weatherCondition = jsonObject.getJSONArray(JSON_NODE_WEATHER);
            if (weatherCondition != null && weatherCondition.length() > 0) {
                this.weatherConditionId = weatherCondition.getJSONObject(0).getInt(JSON_KEY_WEATHER_ID);
                this.weatherCondition = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_CONDITION);
                this.weatherConditionDescription = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_DESCRIPTION);
                this.weatherConditionIcon = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_ICON);
            }
        }

        public Forecast(Integer temp) {
            temperature = temp;
        }

        public long getUnixTimestamp() {
            return unixTimestamp;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public double getWindDirection() {
            return windDirection;
        }

        public double getClouds() {
            return clouds;
        }

        public int getWeatherConditionId() {
            return weatherConditionId;
        }

        public String getWeatherCondition() {
            return weatherCondition;
        }

        public String getWeatherConditionDescription() {
            return weatherConditionDescription;
        }

        public String getWeatherConditionIcon() {
            return weatherConditionIcon;
        }
    }
}