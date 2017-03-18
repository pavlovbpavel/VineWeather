package com.pavel_bojidar.vineweather.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Pavel Pavlov on 3/15/2017.
 */

public class GetCurrentWeather extends AsyncTask<Integer, Void, Void> {

    WeakReference<Activity> activityWeakReference;

    public GetCurrentWeather(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        String strJSON = "";
        try {
            URL url = new URL(
                    "http://api.openweathermap.org/data/2.5/weather?id=" + params[0] + "&APPID=6c733d7c5da372180a412e7ff1aef0d3");
            Scanner s = new Scanner(url.openStream());
            while (s.hasNext()) {
                strJSON = s.nextLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject jo = new JSONObject(strJSON);
            AppManager.getInstance().getCurrentLocation().setCurrentWeather(jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = activityWeakReference.get();
        if(activity != null && activity instanceof WeatherActivity){
            ((WeatherActivity)activity).onLocationUpdated();
        }
    }
}
