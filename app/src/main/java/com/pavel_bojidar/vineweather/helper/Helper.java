package com.pavel_bojidar.vineweather.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.pavel_bojidar.vineweather.R;

import java.io.IOException;
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
        return df.format(input);
    }

    public static String filterCityName(String realName) {
        String filteredResult = null;
        for (int i = 0; i < realName.length(); i++) {
            if (realName.charAt(i) == ',') {
                filteredResult = realName.substring(0, i);
                break;
            }
        }
        return filteredResult;
    }

    public static Drawable chooseConditionIcon(Context context, boolean isDay, String condition) {
        if (isDay) {
            switch (condition) {
                case "Sunny":
                    return context.getResources().getDrawable(R.drawable.clear_day_icon);
                case "Patchy light drizzle":
                    return context.getResources().getDrawable(R.drawable.mostly_cloudy_icon);
                case "Freezing fog":
                    return context.getResources().getDrawable(R.drawable.haze_weather_icon);
                case "Overcast":
                    return context.getResources().getDrawable(R.drawable.windy_weather_icon);
                case "Partly cloudy":
                    return context.getResources().getDrawable(R.drawable.partly_cloudy_icon);
                case "Patchy sleet nearby":
                    return context.getResources().getDrawable(R.drawable.rain_snow_day_icon);
                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    return context.getResources().getDrawable(R.drawable.storm_weather_day_icon);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                    return context.getResources().getDrawable(R.drawable.thunder_day_icon);
                case "Blowing snow":
                    return context.getResources().getDrawable(R.drawable.snow_weather_icon);
                case "Patchy snow nearby":
                case "Light snow showers":
                    return context.getResources().getDrawable(R.drawable.snow_day_icon);
                case "Light sleet":
                case "Light sleet showers":
                case "Patchy sleet possible":
                    return context.getResources().getDrawable(R.drawable.rain_snow_day_icon);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    return context.getResources().getDrawable(R.drawable.cloudy_weather_icon);
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Blizzard":
                    return context.getResources().getDrawable(R.drawable.thunder_weather_icon);
                case "Patchy rain nearby":
                case "Patchy freezing drizzle nearby":
                case "Light drizzle":
                case "Patchy light rain":
                case "Patchy rain possible":
                case "Light rain shower":
                    return context.getResources().getDrawable(R.drawable.rainy_day_icon);
                case "Freezing drizzle":
                case "Heavy freezing drizzle":
                case "Light freezing rain":
                case "Moderate or heavy freezing rain":
                case "Moderate or heavy sleet":
                case "Moderate or heavy sleet showers":
                    return context.getResources().getDrawable(R.drawable.rain_snow_icon);
                case "Moderate or heavy rain shower":
                case "Torrential rain shower":
                case "Light rain":
                case "Moderate rain at times":
                case "Moderate rain":
                case "Heavy rain at times":
                case "Heavy rain":
                    return context.getResources().getDrawable(R.drawable.rainy_weather_icon);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    return context.getResources().getDrawable(R.drawable.snow_weather_icon);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                    return context.getResources().getDrawable(R.drawable.storm_weather_icon);
                default:
                    return context.getResources().getDrawable(R.drawable.unknown);
            }
        } else {
            switch (condition) {
                case "Clear":
                    return context.getResources().getDrawable(R.drawable.clear_night_icon);
                case "Patchy light drizzle":
                    return context.getResources().getDrawable(R.drawable.mostly_cloudy_night_icon);
                case "Freezing fog":
                    return context.getResources().getDrawable(R.drawable.haze_night_icon);
                case "Overcast":
                    return context.getResources().getDrawable(R.drawable.windy_night_icon);
                case "Partly cloudy":
                    return context.getResources().getDrawable(R.drawable.partly_cloudy_night_icon);
                case "Blowing snow":
                case "Patchy snow nearby":
                case "Light snow showers":
                    return context.getResources().getDrawable(R.drawable.snow_night_icon);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    return context.getResources().getDrawable(R.drawable.cloudy_weather_icon);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Blizzard":
                    return context.getResources().getDrawable(R.drawable.thunder_night_icon);
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
                    return context.getResources().getDrawable(R.drawable.rain_snow_night_icon);
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
                    return context.getResources().getDrawable(R.drawable.rainy_night_icon);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    return context.getResources().getDrawable(R.drawable.snow_weather_icon);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    return context.getResources().getDrawable(R.drawable.storm_weather_night_icon);
                default:
                    return context.getResources().getDrawable(R.drawable.unknown);
            }
        }
    }
}
