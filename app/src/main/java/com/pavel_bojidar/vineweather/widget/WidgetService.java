package com.pavel_bojidar.vineweather.widget;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class WidgetService extends Service {

    static String locationName;
    static String condition;
    static int isDay;
    static double degree;

    public WidgetService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String widgetLocation = null;
        if (intent.getStringExtra("location") != null) {
            widgetLocation = intent.getStringExtra("location");
        }

        new AsyncTask<String, Void, String>() {
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
                            "http://api.apixu.com/v1/current.json?key=954d7898a6e84f25a6f123340172604&q=" + validInput);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();

                    Scanner sc = new Scanner(connection.getInputStream());

                    while (sc.hasNextLine()) {
                        strJSON = sc.nextLine();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Log.e("mimi", strJSON);

                return strJSON;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject callObject = new JSONObject(result);

                    JSONObject locationObj = callObject.getJSONObject(Constants.NODE_LOCATION);
                    locationName = locationObj.getString(Constants.KEY_NAME);

                    JSONObject currentObj = callObject.getJSONObject(Constants.NODE_CURRENT);
                    if (WeatherActivity.isImperialUnits) {
                        degree = currentObj.getDouble(Constants.KEY_TEMP_F);
                    } else {
                        degree = currentObj.getDouble(Constants.KEY_TEMP_C);
                    }
                    isDay = currentObj.getInt(Constants.KEY_IS_DAY);

                    JSONObject conditionObj = currentObj.getJSONObject(Constants.NODE_CONDITION);
                    condition = conditionObj.getString(Constants.KEY_TEXT);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(Helper.filterCityName(widgetLocation));

        stopSelf();
        return START_STICKY;
    }
}
