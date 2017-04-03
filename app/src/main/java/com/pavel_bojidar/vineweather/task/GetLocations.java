package com.pavel_bojidar.vineweather.task;

import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Pavel Pavlov on 4/1/2017.
 */

public class GetLocations extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String strJSON = "";
        try {
            URL url = new URL(
                    "http://api.apixu.com/v1/search.json?key=" + Constants.API_KEY + "&q=" + params[0]);
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
