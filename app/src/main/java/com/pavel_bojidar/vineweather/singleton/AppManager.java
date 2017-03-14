package com.pavel_bojidar.vineweather.singleton;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.pavel_bojidar.vineweather.BroadcastActions;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.model.Location;

import java.util.HashMap;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class AppManager {

    //contains all cities for search
    private HashMap<String, Integer> allCities = new HashMap<>();

    //todo current location only
    private HashMap<Integer, Location> locations = new HashMap<>();

    //current location name
    private String currentLocationName;

    private static AppManager instance;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public void onLocationUpdated(Context context, Location location){
        locations.put(location.getId(), location);
        currentLocationName = location.getName();
        Intent intent = new Intent(BroadcastActions.ACTION_LOCATION_UPDATED);
        intent.putExtra(Constants.KEY_LOCATION_ID, location.getId());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public HashMap<Integer, Location> getLocations() {
        return locations;
    }

    public void setLocations(HashMap<Integer, Location> locations) {
        this.locations = locations;
    }

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public void setCurrentLocationName(String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }

    public HashMap<String, Integer> getAllCities() {
        return allCities;
    }
}
