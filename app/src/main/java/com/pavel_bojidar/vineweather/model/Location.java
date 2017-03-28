package com.pavel_bojidar.vineweather.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Location implements Serializable {

    private String name;
    private int id;
    private Forecast currentWeather;
    private List<Forecast> forecasts = new ArrayList<>();

    public Location(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Location() {
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Forecast getCurrentWeather() {
        return currentWeather;
    }


    public static class Forecast {

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

        Forecast(JSONObject jsonObject) throws JSONException {
            if (jsonObject.has(JSON_KEY_TIMESTAMP)) {
                this.unixTimestamp = jsonObject.getLong(JSON_KEY_TIMESTAMP);
            }
            if (jsonObject.has(JSON_NODE_MAIN)) {
                JSONObject mainNode = jsonObject.getJSONObject(JSON_NODE_MAIN);
                if (mainNode.has(JSON_KEY_TEMPERATURE)) {
                    this.temperature = mainNode.getDouble(JSON_KEY_TEMPERATURE);
                }
                if (mainNode.has(JSON_KEY_HUMIDITY)) {
                    this.humidity = mainNode.getDouble(JSON_KEY_HUMIDITY);
                }
                if (mainNode.has(JSON_KEY_PRESSURE)) {
                    this.pressure = mainNode.getDouble(JSON_KEY_PRESSURE);
                }
                if (mainNode.has(JSON_KEY_CLOUDS)) {
                    this.clouds = mainNode.getDouble(JSON_KEY_CLOUDS);
                }
            }

            if(jsonObject.has(JSON_NODE_WIND)){
                JSONObject windNode = jsonObject.getJSONObject(JSON_NODE_WIND);
                if (windNode.has(JSON_KEY_WIND_SPEED)) {
                    this.windSpeed = windNode.getDouble(JSON_KEY_WIND_SPEED);
                }
                if (windNode.has(JSON_KEY_WIND_DEGREES)) {
                    this.windDirection = windNode.getDouble(JSON_KEY_WIND_DEGREES);
                }
            }

            JSONArray weatherCondition = jsonObject.getJSONArray(JSON_NODE_WEATHER);
            if (weatherCondition != null && weatherCondition.length() > 0) {
                this.weatherConditionId = weatherCondition.getJSONObject(0).getInt(JSON_KEY_WEATHER_ID);
                this.weatherCondition = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_CONDITION);
                this.weatherConditionDescription = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_DESCRIPTION);
                this.weatherConditionIcon = weatherCondition.getJSONObject(0).getString(JSON_KEY_WEATHER_ICON);
            }
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

    public static class CityInfo {
        private String name;
        private int id;
        private int position;

        public CityInfo(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}