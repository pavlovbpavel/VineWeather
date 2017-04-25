package com.pavel_bojidar.vineweather.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.pavel_bojidar.vineweather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Pavel Pavlov on 3/18/2017.
 */

public class Helper {

    public static int imageWidget;

    public static String getUnixHour(long unixTS) {
        Date date = new Date(unixTS * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getUnixAmPmHour(long unixTS) {
        Date date = new Date(unixTS * 1000L);
        DateFormat dateFormat = new SimpleDateFormat("h a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public static String getUnixDate(long unixTS) {
        Date date = new Date(unixTS * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getUnixCustomDate(long unixTS) {
        Date date = new Date(unixTS * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat(", MMM dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getWeekDay(String dateInput) {
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        Date dt1 = null;
        try {
            dt1 = format1.parse(dateInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String finalDay = format2.format(dt1);
        return finalDay;
    }

    public static String decimalFormat(double input) {
        DecimalFormat df = new DecimalFormat("##");
        if(input < 0 && input > -1){
            return "0";
        }
        return df.format(input);
    }

    public static String filterCityName(String realName) {
        String filteredResult = null;
        boolean hasComma = false;
        for (int i = 0; i < realName.length(); i++) {
            if (realName.charAt(i) == ',') {
                hasComma = true;
                filteredResult = realName.substring(0, i);
                break;
            }
        }
        return hasComma ? filteredResult : realName;
    }

    public static Drawable choosePrecipitationIcon(Context context, double amount){
        if(amount <= 0.1){
            return context.getResources().getDrawable(R.drawable.droplet1);
        }
        if(amount > 0.1 && amount <= 0.5){
            return context.getResources().getDrawable(R.drawable.droplet2);
        }
        if(amount > 0.5 && amount <= 1){
            return context.getResources().getDrawable(R.drawable.droplet3);
        }
        if(amount > 1 && amount <= 1.5){
            return context.getResources().getDrawable(R.drawable.droplet4);
        }
        if(amount > 1.5 && amount <= 2){
            return context.getResources().getDrawable(R.drawable.droplet5);
        }
        if(amount > 2 && amount <= 3){
            return context.getResources().getDrawable(R.drawable.droplet6);
        }
        if(amount > 3 && amount <= 4){
            return context.getResources().getDrawable(R.drawable.droplet7);
        }
        if(amount > 4 && amount <= 5){
            return context.getResources().getDrawable(R.drawable.droplet8);
        }
        if(amount > 5 && amount <= 6.5){
            return context.getResources().getDrawable(R.drawable.droplet9);
        }
        if(amount > 6.5 && amount <= 7.5){
            return context.getResources().getDrawable(R.drawable.droplet10);
        }
        if(amount > 7.5 && amount <= 8.5){
            return context.getResources().getDrawable(R.drawable.droplet11);
        }
        if(amount > 8.5 && amount <= 10){
            return context.getResources().getDrawable(R.drawable.droplet12);
        }
        if(amount > 10){
            return context.getResources().getDrawable(R.drawable.droplet13);
        }
        return context.getResources().getDrawable(R.drawable.droplet1);
    }

    public static Drawable chooseConditionIcon(Context context, boolean isDay, boolean isForecast, String condition) {

        Resources resources = context.getResources();

        if (isDay) {
            switch (condition) {
                case "Sunny":
                    imageWidget = R.drawable.clear_day_icon;
                    return resources.getDrawable(R.drawable.clear_day_icon);
                case "Patchy light drizzle":
                    imageWidget = R.drawable.mostly_cloudy_icon;
                    return isForecast ? resources.getDrawable(R.drawable.mostly_cloudy_icon_rv) : resources.getDrawable(R.drawable.mostly_cloudy_icon);
                case "Freezing fog":
                    imageWidget = R.drawable.haze_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.haze_weather_icon_rv) : resources.getDrawable(R.drawable.haze_weather_icon);
                case "Overcast":
                    imageWidget = R.drawable.windy_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.windy_weather_icon_rv) : resources.getDrawable(R.drawable.windy_weather_icon);
                case "Partly cloudy":
                    imageWidget = R.drawable.partly_cloudy_icon;
                    return isForecast ? resources.getDrawable(R.drawable.partly_cloudy_icon_rv) : resources.getDrawable(R.drawable.partly_cloudy_icon);

                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    imageWidget = R.drawable.storm_weather_day_icon;
                    return isForecast ? resources.getDrawable(R.drawable.storm_weather_day_icon_rv) : resources.getDrawable(R.drawable.storm_weather_day_icon);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                    imageWidget = R.drawable.thunder_day_icon;
                    return isForecast ? resources.getDrawable(R.drawable.thunder_day_icon_rv) : resources.getDrawable(R.drawable.thunder_day_icon);
                case "Blowing snow":
                    return isForecast ? resources.getDrawable(R.drawable.snow_weather_icon_rv) : resources.getDrawable(R.drawable.snow_weather_icon);
                case "Patchy snow nearby":
                case "Light snow showers":
                case "Patchy snow possible":
                    imageWidget = R.drawable.snow_day_icon;
                    return isForecast ? resources.getDrawable(R.drawable.snow_day_icon_rv) : resources.getDrawable(R.drawable.snow_day_icon);
                case "Light sleet":
                case "Light sleet showers":
                case "Patchy sleet possible":
                case "Patchy sleet nearby":
                    imageWidget = R.drawable.rain_snow_day_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rain_snow_day_icon_rv) : resources.getDrawable(R.drawable.rain_snow_day_icon);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    imageWidget = R.drawable.cloudy_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.cloudy_weather_icon_rv) : resources.getDrawable(R.drawable.cloudy_weather_icon);
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Blizzard":
                    imageWidget = R.drawable.thunder_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.thunder_weather_icon_rv) : resources.getDrawable(R.drawable.thunder_weather_icon);
                case "Patchy rain nearby":
                case "Patchy freezing drizzle nearby":
                case "Light drizzle":
                case "Patchy light rain":
                case "Patchy rain possible":
                case "Light rain shower":
                    imageWidget = R.drawable.rainy_day_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rainy_day_icon_rv) : resources.getDrawable(R.drawable.rainy_day_icon);
                case "Freezing drizzle":
                case "Heavy freezing drizzle":
                case "Light freezing rain":
                case "Moderate or heavy freezing rain":
                case "Moderate or heavy sleet":
                case "Moderate or heavy sleet showers":
                    imageWidget = R.drawable.rain_snow_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rain_snow_icon_rv) : resources.getDrawable(R.drawable.rain_snow_icon);
                case "Moderate or heavy rain shower":
                case "Torrential rain shower":
                case "Light rain":
                case "Moderate rain at times":
                case "Moderate rain":
                case "Heavy rain at times":
                case "Heavy rain":
                    imageWidget = R.drawable.rainy_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rainy_weather_icon_rv) : resources.getDrawable(R.drawable.rainy_weather_icon);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    imageWidget = R.drawable.snow_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.snow_weather_icon_rv) : resources.getDrawable(R.drawable.snow_weather_icon);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                case "Moderate or heavy rain with thunder":
                    imageWidget = R.drawable.storm_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.storm_weather_icon_rv) : resources.getDrawable(R.drawable.storm_weather_icon);
                default:
                    imageWidget = R.drawable.unknown;
                    return resources.getDrawable(R.drawable.unknown);
            }
        } else {
            switch (condition) {
                case "Clear":
                    imageWidget = R.drawable.clear_night_icon;
                    return resources.getDrawable(R.drawable.clear_night_icon);
                case "Patchy light drizzle":
                    imageWidget = R.drawable.mostly_cloudy_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.mostly_cloudy_night_icon_rv) : resources.getDrawable(R.drawable.mostly_cloudy_night_icon);
                case "Freezing fog":
                    imageWidget = R.drawable.haze_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.haze_night_icon_rv) : resources.getDrawable(R.drawable.haze_night_icon);
                case "Overcast":
                    imageWidget = R.drawable.windy_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.windy_night_icon_rv) : resources.getDrawable(R.drawable.windy_night_icon);
                case "Partly cloudy":
                    imageWidget = R.drawable.partly_cloudy_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.partly_cloudy_night_icon_rv) : resources.getDrawable(R.drawable.partly_cloudy_night_icon);
                case "Blowing snow":
                case "Patchy snow nearby":
                case "Light snow showers":
                case "Patchy snow possible":
                    imageWidget = R.drawable.snow_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.snow_night_icon_rv) : resources.getDrawable(R.drawable.snow_night_icon);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    imageWidget = R.drawable.cloudy_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.cloudy_weather_icon_rv) : resources.getDrawable(R.drawable.cloudy_weather_icon);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Blizzard":
                    imageWidget = R.drawable.thunder_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.thunder_night_icon_rv) : resources.getDrawable(R.drawable.thunder_night_icon);
                case "Freezing drizzle":
                case "Heavy freezing drizzle":
                case "Light freezing rain":
                case "Moderate or heavy freezing rain":
                case "Moderate or heavy sleet":
                case "Moderate or heavy sleet showers":
                case "Light sleet":
                case "Patchy sleet nearby":
                case "Light sleet showers":
                case "Patchy sleet possible":
                    imageWidget = R.drawable.rain_snow_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rain_snow_night_icon_rv) : resources.getDrawable(R.drawable.rain_snow_night_icon);
                case "Moderate or heavy rain shower":
                case "Torrential rain shower":
                case "Light rain":
                case "Moderate rain at times":
                case "Moderate rain":
                case "Heavy rain at times":
                case "Heavy rain":
                case "Patchy rain nearby":
                case "Patchy freezing drizzle nearby":
                case "Light drizzle":
                case "Patchy rain possible":
                case "Patchy light rain":
                case "Light rain shower":
                    imageWidget = R.drawable.rainy_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.rainy_night_icon_rv) : resources.getDrawable(R.drawable.rainy_night_icon);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    imageWidget = R.drawable.snow_weather_icon;
                    return isForecast ? resources.getDrawable(R.drawable.snow_weather_icon_rv) : resources.getDrawable(R.drawable.snow_weather_icon);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                case "Moderate or heavy rain with thunder":
                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    imageWidget = R.drawable.storm_weather_night_icon;
                    return isForecast ? resources.getDrawable(R.drawable.storm_weather_night_icon_rv) : resources.getDrawable(R.drawable.storm_weather_night_icon);
                default:
                    imageWidget = R.drawable.unknown;
                    return resources.getDrawable(R.drawable.unknown);
            }
        }
    }
}
