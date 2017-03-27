package com.pavel_bojidar.vineweather.singleton;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.model.Location;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class AppManager {

    private ConcurrentHashMap<String, Integer> allCities = new ConcurrentHashMap<>();

    private Location currentLocation = new Location();

    private String currentLocationName;

    private static AppManager instance;

    private String units = "celsius";

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
        currentLocationName = currentLocation.getName();
        Intent intent = new Intent(BroadcastActions.ACTION_LOCATION_UPDATED);
        intent.putExtra(Constants.KEY_LOCATION_ID, currentLocation.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public void setCurrentLocationName(String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }

    public ConcurrentHashMap<String, Integer> getAllCities() {
        return allCities;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
