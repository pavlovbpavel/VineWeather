package com.pavel_bojidar.vineweather.model;

import java.io.Serializable;

/**
 * Created by Pavel Pavlov on 4/1/2017.
 */

public class Condition implements Serializable{

    private String text;
    private String icon;
    private int code;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
