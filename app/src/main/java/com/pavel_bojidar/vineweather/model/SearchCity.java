package com.pavel_bojidar.vineweather.model;

import java.util.Date;

/**
 * Created by ASUS on 7.4.2017 г..
 */

public class SearchCity{

    private String name;
    private Date date;

    public SearchCity(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }


}
