package com.pavel_bojidar.vineweather.task;

import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by Pavel Pavlov on 4/1/2017.
 */

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
            Scanner s = new Scanner(url.openStream());
            while (s.hasNext()) {
                strJSON = s.nextLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strJSON;
    }
}
