package com.pavel_bojidar.vineweather.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

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

    public static String getUnixDate(long unixTS) {
        Date date = new Date(unixTS * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getWeekDay(String dateInput) {
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy/");
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

    @Nullable
    public static Drawable chooseIcon(Context context, boolean isDay, String path) {
        int index = path.lastIndexOf("/");
        String filteredPath = path.substring(index, path.length());
        try {
            return Drawable.createFromStream(context.getAssets().open(isDay ? "day".concat(filteredPath) : "night".concat(filteredPath)), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; //todo return default drawable
    }
}
