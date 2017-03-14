package com.pavel_bojidar.vineweather.task;

import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.singleton.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Pavel Pavlov on 3/15/2017.
 */

public class LoadCitiesFromFile extends AsyncTask<InputStream, Void, Void> {

    @Override
    protected Void doInBackground(InputStream... params) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(params[0]));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject object = new JSONObject(line);
                AppManager.getInstance().getAllCities().put(object.getString("name"), object.getInt("_id"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
