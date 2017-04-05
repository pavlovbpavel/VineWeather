package com.pavel_bojidar.vineweather.model;

/**
 * Created by Pavel Pavlov on 4/5/2017.
 */

public class CityInfo {

    private String realName;
    private String filteredName;

    public CityInfo(String realName, String filteredName) {
        this.realName = realName;
        this.filteredName = filteredName;
    }

    public String getRealName() {
        return realName;
    }

    public String getFilteredName() {
        return filteredName;
    }
}