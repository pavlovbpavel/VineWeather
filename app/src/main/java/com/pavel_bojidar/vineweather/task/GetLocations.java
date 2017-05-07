package com.pavel_bojidar.vineweather.task;

import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.WeatherActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class GetLocations extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String strJSON = "";
        String validInput = null;
        try {
            validInput = URLEncoder.encode(params[0], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(
                    "http://api.apixu.com/v1/search.json?key=" + Constants.API_KEY + "&q=" + validInput);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (http.getResponseCode() > 400) {
                WeatherActivity.isConnected = false;
            } else {
                WeatherActivity.isConnected = true;
                Scanner s = new Scanner(url.openStream());
                while (s.hasNext()) {
                    strJSON = s.nextLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strJSON;
    }
}
