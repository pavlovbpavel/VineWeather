package com.pavel_bojidar.vineweather.helper;

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

    public static double kelvinToFahrenheit(double kelvin) {
        double fahrenheit = ((kelvin - 273) * (9 / 5)) + 32;
        return fahrenheit;
    }
}
