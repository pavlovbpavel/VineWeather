package com.pavel_bojidar.vineweather.widget;

import android.appwidget.AppWidgetProvider;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class MaPaWidgetProvider extends AppWidgetProvider {

    private static HashMap<String, ArrayList<Integer>> widgetLocations = new HashMap<>();

    public static HashMap<String, ArrayList<Integer>> getWidgetLocations() {
        return widgetLocations;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int id : appWidgetIds) {
            widgetLocations.remove(id);
        }
    }
}

