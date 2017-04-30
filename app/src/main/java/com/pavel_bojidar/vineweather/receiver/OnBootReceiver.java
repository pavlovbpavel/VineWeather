package com.pavel_bojidar.vineweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.task.GetForecastService;
import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

import static android.content.Context.MODE_PRIVATE;

public class OnBootReceiver extends BroadcastReceiver {

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
            String widgetLocation = prefs.getString(Constants.KEY_LOCATION_NAME, null);
            Intent startService = new Intent(context.getApplicationContext(), GetForecastService.class);
            if (widgetLocation != null) {
                startService.putExtra("params", widgetLocation);
            }
            context.startService(startService);
        }
    }
}
