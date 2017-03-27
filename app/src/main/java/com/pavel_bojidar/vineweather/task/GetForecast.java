package com.pavel_bojidar.vineweather.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.model.Location.CityInfo;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by Pavel Pavlov on 3/15/2017.
 */

public class GetForecast extends AsyncTask<CityInfo, Void, Void> {

    WeakReference<Activity> activityWeakReference;

    public GetForecast(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
    }

    @Override
    protected Void doInBackground(CityInfo... params) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(
                    "http://api.openweathermap.org/data/2.5/forecast?id=" + params[0].getId() + "&APPID=6c733d7c5da372180a412e7ff1aef0d3");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JSONObject jo;
        try {
            jo = new JSONObject(result.toString());
            JSONArray jarr = jo.getJSONArray("list");
            AppManager.getInstance().getCurrentLocation().setForecasts(jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = activityWeakReference.get();
        if (activity != null && activity instanceof WeatherActivity) {
            ((WeatherActivity) activity).onLocationUpdated();
        }
    }
}
