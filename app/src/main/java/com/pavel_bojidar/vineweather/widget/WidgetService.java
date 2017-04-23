package com.pavel_bojidar.vineweather.widget;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.pavel_bojidar.vineweather.Constants;

import org.json.JSONArray;
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
    static double min;
    static double max;

    public WidgetService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
                    Log.e("mimi", "1");
                    URL url = new URL(
                            "http://api.apixu.com/v1/forecast.json?key=af8f97f36ad940fab0b191628171204&q=" + validInput + "&days=1");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    Log.e("mimi", "2");

                    Scanner sc = new Scanner(connection.getInputStream());
                    Log.e("mimi", "3");
                    while (sc.hasNextLine()) {
                        strJSON = sc.nextLine();
                    }
                    Log.e("mimi", "4");
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

                    JSONObject locationObj = callObject.getJSONObject("location");
                    locationName = locationObj.getString("name");

                    JSONObject currentObj = callObject.getJSONObject("current");
                    degree = currentObj.getDouble("temp_c");
                    isDay = currentObj.getInt(Constants.KEY_IS_DAY);

                    JSONObject conditionObj = currentObj.getJSONObject(Constants.NODE_CONDITION);
                    condition = conditionObj.getString(Constants.KEY_TEXT);


                    JSONObject forecastObj = callObject.getJSONObject(Constants.NODE_FORECAST);
                    JSONArray forecastDays = forecastObj.getJSONArray(Constants.NODE_FORECASTDAY);
                    JSONObject todayObj = forecastDays.getJSONObject(0).getJSONObject(Constants.NODE_DAY);
                    min = todayObj.getDouble(Constants.KEY_MINTEMP_C);
                    max = todayObj.getDouble(Constants.KEY_MAXTEMP_C);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute("Sofia");

        return START_STICKY;
    }
}
