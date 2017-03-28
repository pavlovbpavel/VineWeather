package com.pavel_bojidar.vineweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Pavel Pavlov on 3/27/2017.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private ConnectivityChanged listener;

    public NetworkReceiver() {
    }

    public NetworkReceiver(ConnectivityChanged listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                if (listener != null) {
                    listener.onConnected();
                }
            }
        }
    }

    interface ConnectivityChanged {
        void onConnected();
    }
}