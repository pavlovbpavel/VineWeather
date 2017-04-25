package com.pavel_bojidar.vineweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;

/**
 * Implementation of App Widget functionality.
 */
public class MaPaWidgetProvider extends AppWidgetProvider {

    public static int[] allWidgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

         ComponentName thisWidget = new ComponentName(context, MaPaWidgetProvider.class);
         allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

         startService(context.getApplicationContext());

        for (int widgetId : allWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ma_pa_widget);

            views.setTextViewText(R.id.location_widget, WidgetService.locationName + "");
            views.setTextViewText(R.id.degree_widget, Helper.decimalFormat(WidgetService.degree) + Constants.CELSIUS_SYMBOL);
            views.setTextViewText(R.id.min_max_degree_widget, WidgetService.min + Constants.CELSIUS_SYMBOL +
                    "/" + WidgetService.max + Constants.CELSIUS_SYMBOL);
            views.setTextViewText(R.id.condition_widget, WidgetService.condition);

            if(WidgetService.condition != null) {
                Drawable drawable = Helper.chooseConditionIcon(context, WidgetService.isDay == 1, false, WidgetService.condition);
            }

            views.setImageViewResource(R.id.image_view_widget, Helper.imageWidget);

            Intent intent1 = new Intent(context.getApplicationContext(), WeatherActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

