package com.pavel_bojidar.vineweather.singleton;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.model.maindata.Location;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class AppManager {

    private static AppManager instance;

    private Location currentLocation = new Location();

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void onLocationUpdated(Context context) {
        Log.e("broadcast", "app manager - on location updated");
        Intent intent = new Intent(BroadcastActions.ACTION_LOCATION_UPDATED);
        intent.putExtra(Constants.KEY_LOCATION_NAME, currentLocation.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
