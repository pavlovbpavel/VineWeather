package com.pavel_bojidar.vineweather.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;

import com.pavel_bojidar.vineweather.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Helper {

    public static int imageWidget;
    //todo get timezone from device timezone



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
        for (int i = 0; i < realName.length(); i++) {
            if (realName.charAt(i) == ',') {
                return realName.substring(0, i);
            }
            if (realName.charAt(i) == '(') {
                return realName.substring(0, i);
            }
        }
        return realName;
    }

    public static Drawable choosePrecipitationIcon(Context context, double amount){

        Resources resources = context.getResources();

        if(amount <= 0.1){
            return  ResourcesCompat.getDrawable(resources,R.drawable.droplet1, null);
        }
        if(amount > 0.1 && amount <= 0.5){
            return ResourcesCompat.getDrawable(resources,R.drawable.droplet2, null);
        }
        if(amount > 0.5 && amount <= 1){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet3, null);
        }
        if(amount > 1 && amount <= 1.5){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet4, null);
        }
        if(amount > 1.5 && amount <= 2){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet5, null);
        }
        if(amount > 2 && amount <= 3){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet6, null);
        }
        if(amount > 3 && amount <= 4){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet7, null);
        }
        if(amount > 4 && amount <= 5){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet8, null);
        }
        if(amount > 5 && amount <= 6.5){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet9, null);
        }
        if(amount > 6.5 && amount <= 7.5){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet10, null);
        }
        if(amount > 7.5 && amount <= 8.5){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet11, null);
        }
        if(amount > 8.5 && amount <= 10){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet12, null);
        }
        if(amount > 10){
            return ResourcesCompat.getDrawable(resources, R.drawable.droplet13, null);
        }
        return ResourcesCompat.getDrawable(resources, R.drawable.droplet1, null);
    }

    public static Drawable chooseConditionIcon(Context context, boolean isDay, boolean isForecast, String condition) {

        Resources resources = context.getResources();

        if (isDay) {
            switch (condition) {
                case "Sunny":
                    imageWidget = R.drawable.clear_day_icon;
                    return ResourcesCompat.getDrawable(resources, R.drawable.clear_day_icon, null);
                case "Patchy light drizzle":
                    imageWidget = R.drawable.mostly_cloudy_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.mostly_cloudy_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.mostly_cloudy_icon, null);
                case "Freezing fog":
                    imageWidget = R.drawable.haze_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.haze_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.haze_weather_icon, null);
                case "Overcast":
                    imageWidget = R.drawable.windy_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.windy_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.windy_weather_icon, null);
                case "Partly cloudy":
                    imageWidget = R.drawable.partly_cloudy_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.partly_cloudy_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.partly_cloudy_icon, null);

                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    imageWidget = R.drawable.storm_weather_day_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_day_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_day_icon, null);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                    imageWidget = R.drawable.thunder_day_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.thunder_day_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.thunder_day_icon, null);
                case "Blowing snow":
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon, null);
                case "Patchy snow nearby":
                case "Light snow showers":
                case "Patchy snow possible":
                    imageWidget = R.drawable.snow_day_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.snow_day_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.snow_day_icon, null);
                case "Light sleet":
                case "Light sleet showers":
                case "Patchy sleet possible":
                case "Patchy sleet nearby":
                    imageWidget = R.drawable.rain_snow_day_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_day_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_day_icon, null);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    imageWidget = R.drawable.cloudy_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.cloudy_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.cloudy_weather_icon, null);
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Moderate or heavy snow with thunder":
                case "Blizzard":
                    imageWidget = R.drawable.thunder_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.thunder_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.thunder_weather_icon, null);
                case "Patchy rain nearby":
                case "Patchy freezing drizzle nearby":
                case "Light drizzle":
                case "Patchy light rain":
                case "Patchy rain possible":
                case "Light rain shower":
                    imageWidget = R.drawable.rainy_day_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rainy_day_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rainy_day_icon, null);
                case "Freezing drizzle":
                case "Heavy freezing drizzle":
                case "Light freezing rain":
                case "Moderate or heavy freezing rain":
                case "Moderate or heavy sleet":
                case "Moderate or heavy sleet showers":
                    imageWidget = R.drawable.rain_snow_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_icon, null);
                case "Moderate or heavy rain shower":
                case "Torrential rain shower":
                case "Light rain":
                case "Moderate rain at times":
                case "Moderate rain":
                case "Heavy rain at times":
                case "Heavy rain":
                    imageWidget = R.drawable.rainy_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rainy_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rainy_weather_icon, null);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    imageWidget = R.drawable.snow_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon, null);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                case "Moderate or heavy rain with thunder":
                    imageWidget = R.drawable.storm_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_icon, null);
                default:
                    imageWidget = R.drawable.unknown;
                    return ResourcesCompat.getDrawable(resources, R.drawable.unknown, null);
            }
        } else {
            switch (condition) {
                case "Clear":
                    imageWidget = R.drawable.clear_night_icon;
                    return ResourcesCompat.getDrawable(resources, R.drawable.clear_night_icon, null);
                case "Patchy light drizzle":
                    imageWidget = R.drawable.mostly_cloudy_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.mostly_cloudy_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.mostly_cloudy_night_icon, null);
                case "Freezing fog":
                    imageWidget = R.drawable.haze_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.haze_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.haze_night_icon, null);
                case "Overcast":
                    imageWidget = R.drawable.windy_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.windy_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.windy_night_icon, null);
                case "Partly cloudy":
                    imageWidget = R.drawable.partly_cloudy_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.partly_cloudy_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.partly_cloudy_night_icon, null);
                case "Blowing snow":
                case "Patchy snow nearby":
                case "Light snow showers":
                case "Patchy snow possible":
                    imageWidget = R.drawable.snow_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.snow_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.snow_night_icon, null);
                case "Mist":
                case "Fog":
                case "Cloudy":
                    imageWidget = R.drawable.cloudy_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.cloudy_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.cloudy_weather_icon, null);
                case "Thundery outbreaks in nearby":
                case "Thundery outbreaks possible":
                case "Patchy light snow in area with thunder":
                case "Moderate or heavy snow in area with thunder":
                case "Moderate or heavy snow with thunder":
                case "Blizzard":
                    imageWidget = R.drawable.thunder_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.thunder_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.thunder_night_icon, null);
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
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rain_snow_night_icon, null);
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
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.rainy_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.rainy_night_icon, null);
                case "Patchy light snow":
                case "Light snow":
                case "Patchy moderate snow":
                case "Moderate snow":
                case "Patchy heavy snow":
                case "Heavy snow":
                case "Moderate or heavy snow showers":
                    imageWidget = R.drawable.snow_weather_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.snow_weather_icon, null);
                case "Ice pellets":
                case "Moderate or heavy showers of ice pellets":
                case "Patchy light rain in area with thunder":
                case "Moderate or heavy rain in area with thunder":
                case "Moderate or heavy rain with thunder":
                case "Light showers of ice pellets":
                case "Patchy light rain with thunder":
                    imageWidget = R.drawable.storm_weather_night_icon;
                    return isForecast ? ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_night_icon_rv, null) :
                            ResourcesCompat.getDrawable(resources, R.drawable.storm_weather_night_icon, null);
                default:
                    imageWidget = R.drawable.unknown;
                    return ResourcesCompat.getDrawable(resources, R.drawable.unknown, null);
            }
        }
    }

    public static Drawable chooseFragmentBackground(Context context, String condition, boolean isDay){

        Resources resources = context.getResources();

        switch (condition){
            case "Clear":
            case "Partly cloudy":
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_clear_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_clear_background_night, null);
            case "Freezing fog":
            case "Overcast":
            case "Mist":
            case "Fog":
            case "Cloudy":
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_overcast_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_overcast_background_night, null);
            case "Freezing drizzle":
            case "Heavy freezing drizzle":
            case "Light freezing rain":
            case "Moderate or heavy freezing rain":
            case "Moderate or heavy sleet":
            case "Moderate or heavy sleet showers":
            case "Patchy light drizzle":
            case "Light sleet":
            case "Patchy sleet nearby":
            case "Light sleet showers":
            case "Patchy sleet possible":
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
            case "Moderate or heavy snow showers":
            case "Light showers of ice pellets":
            case "Moderate or heavy showers of ice pellets":
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_rainy_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_rainy_background_night, null);
            case "Blizzard":
            case "Patchy snow possible":
            case "Blowing snow":
            case "Patchy snow nearby":
            case "Light snow showers":
            case "Patchy light snow":
            case "Light snow":
            case "Patchy moderate snow":
            case "Moderate snow":
            case "Patchy heavy snow":
            case "Heavy snow":
            case "Ice pellets":
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_snowy_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_snowy_background_night, null);
            case "Thundery outbreaks in nearby":
            case "Thundery outbreaks possible":
            case "Patchy light snow in area with thunder":
            case "Moderate or heavy snow in area with thunder":
            case "Moderate or heavy snow with thunder":
            case "Patchy light rain in area with thunder":
            case "Moderate or heavy rain in area with thunder":
            case "Moderate or heavy rain with thunder":
            case "Patchy light rain with thunder":
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_thundery_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_thundery_background_night, null);
            default:
                return isDay ? ResourcesCompat.getDrawable(resources, R.drawable.condition_clear_background_day, null) :
                        ResourcesCompat.getDrawable(resources, R.drawable.condition_clear_background_night, null);
        }
    }

    public static int[] chooseConditionColorSet (String condition, boolean isDay){

        int[] colorSet = new int[2];

        switch (condition){
            case "Clear":
            case "Partly cloudy":
                if(isDay){
                    colorSet[0] = R.color.conditionClearDay;
                    colorSet[1] = R.color.conditionClearDayDark;
                } else {
                    colorSet[0] = R.color.conditionClearNight;
                    colorSet[1] = R.color.conditionClearNightDark;
                }
                return colorSet;
            case "Freezing fog":
            case "Overcast":
            case "Mist":
            case "Fog":
            case "Cloudy":
                if(isDay){
                    colorSet[0] = R.color.conditionOvercastDay;
                    colorSet[1] = R.color.conditionOvercastDayDark;
                } else {
                    colorSet[0] = R.color.conditionOvercastNight;
                    colorSet[1] = R.color.conditionOvercastNightDark;
                }
                return colorSet;
            case "Freezing drizzle":
            case "Heavy freezing drizzle":
            case "Light freezing rain":
            case "Moderate or heavy freezing rain":
            case "Moderate or heavy sleet":
            case "Moderate or heavy sleet showers":
            case "Patchy light drizzle":
            case "Light sleet":
            case "Patchy sleet nearby":
            case "Light sleet showers":
            case "Patchy sleet possible":
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
            case "Moderate or heavy snow showers":
            case "Light showers of ice pellets":
            case "Moderate or heavy showers of ice pellets":
                if(isDay){
                    colorSet[0] = R.color.conditionRainyDay;
                    colorSet[1] = R.color.conditionRainyDayDark;
                } else {
                    colorSet[0] = R.color.conditionRainyNight;
                    colorSet[1] = R.color.conditionRainyNightDark;
                }
                return colorSet;
            case "Blizzard":
            case "Patchy snow possible":
            case "Blowing snow":
            case "Patchy snow nearby":
            case "Light snow showers":
            case "Patchy light snow":
            case "Light snow":
            case "Patchy moderate snow":
            case "Moderate snow":
            case "Patchy heavy snow":
            case "Heavy snow":
            case "Ice pellets":
                if(isDay){
                    colorSet[0] = R.color.conditionSnowyDay;
                    colorSet[1] = R.color.conditionSnowyDayDark;
                } else {
                    colorSet[0] = R.color.conditionSnowyNight;
                    colorSet[1] = R.color.conditionSnowyNightDark;
                }
                return colorSet;
            case "Thundery outbreaks in nearby":
            case "Thundery outbreaks possible":
            case "Patchy light snow in area with thunder":
            case "Moderate or heavy snow in area with thunder":
            case "Patchy light rain in area with thunder":
            case "Moderate or heavy rain in area with thunder":
            case "Moderate or heavy snow with thunder":
            case "Moderate or heavy rain with thunder":
            case "Patchy light rain with thunder":
                if(isDay){
                    colorSet[0] = R.color.conditionThunderyDay;
                    colorSet[1] = R.color.conditionThunderyDayDark;
                } else {
                    colorSet[0] = R.color.conditionThunderyNight;
                    colorSet[1] = R.color.conditionThunderyNightDark;
                }
                return colorSet;
            default:
                if(isDay){
                    colorSet[0] = R.color.conditionClearDay;
                    colorSet[1] = R.color.conditionClearDayDark;
                } else {
                    colorSet[0] = R.color.conditionClearNight;
                    colorSet[1] = R.color.conditionClearNightDark;
                }
                return colorSet;
        }
    }

    public static GradientDrawable chooseHeaderColorSet (Context context, int[] colorSet) {
        int colorLight = ResourcesCompat.getColor(context.getResources(), colorSet[0], null);
        int colorDark = ResourcesCompat.getColor(context.getResources(), colorSet[1], null);
        return new GradientDrawable(Orientation.TOP_BOTTOM, new int[] {colorLight, colorDark});
    }
}
