package com.pavel_bojidar.vineweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

/**
 * Created by ASUS on 25.4.2017 г..
 */

public class OnBootReceiver extends BroadcastReceiver{

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            MaPaWidgetProvider.startService(context);
        }
    }
}
