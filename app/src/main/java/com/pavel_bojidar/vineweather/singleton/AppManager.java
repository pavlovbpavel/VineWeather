package com.pavel_bojidar.vineweather.singleton;

import com.pavel_bojidar.vineweather.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class AppManager {
    
    private Location currentLocation;

    private static AppManager ourInstance;
    private List<Location> favoriteList;

    private AppManager() {
    }

    public static AppManager getOurInstance() {
        if (ourInstance == null) {
            ourInstance = new AppManager();
        }
        return ourInstance;

    }

    public List<Location> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(List<Location> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
