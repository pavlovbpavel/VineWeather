package com.pavel_bojidar.vineweather.task;

import android.os.AsyncTask;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.model.Condition;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import static com.pavel_bojidar.vineweather.Constants.KEY_CLOUD;
import static com.pavel_bojidar.vineweather.Constants.KEY_CODE;
import static com.pavel_bojidar.vineweather.Constants.KEY_FEELSLIKE_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_FEELSLIKE_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_HUMIDITY;
import static com.pavel_bojidar.vineweather.Constants.KEY_ICON;
import static com.pavel_bojidar.vineweather.Constants.KEY_IS_DAY;
import static com.pavel_bojidar.vineweather.Constants.KEY_LAST_UPDATED;
import static com.pavel_bojidar.vineweather.Constants.KEY_LAST_UPDATED_EPOCH;
import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRECIP_IN;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRECIP_MM;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRESSURE_IN;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRESSURE_MB;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEMP_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEMP_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEXT;
import static com.pavel_bojidar.vineweather.Constants.KEY_VIS_KM;
import static com.pavel_bojidar.vineweather.Constants.KEY_VIS_MILES;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_DEGREE;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_DIR;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_KPH;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_MPH;
import static com.pavel_bojidar.vineweather.Constants.NODE_CONDITION;
import static com.pavel_bojidar.vineweather.Constants.NODE_CURRENT;
import static com.pavel_bojidar.vineweather.Constants.NODE_LOCATION;

public class GetCurrentWeather extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String strJSON = "";
        String validInput = null;
        try {
            validInput = URLEncoder.encode(params[0], "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        try {
            URL url = new URL(
                    "http://api.apixu.com/v1/current.json?key=" + Constants.API_KEY + "&q=" + validInput);
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

        }
        return strJSON;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String locationCallbackName = null;
        CurrentWeather currentWeather = new CurrentWeather();
        try {
            JSONObject jo = new JSONObject(result);
            locationCallbackName = jo.getJSONObject(NODE_LOCATION).getString(KEY_NAME);
            if (jo.has(NODE_CURRENT)) {
                JSONObject currentJo = jo.getJSONObject(NODE_CURRENT);
                JSONObject conditionJo = currentJo.getJSONObject(NODE_CONDITION);

                currentWeather.setLastUpdated(currentJo.getString(KEY_LAST_UPDATED));
                currentWeather.setLastUpdateEpoch(currentJo.getInt(KEY_LAST_UPDATED_EPOCH));
                currentWeather.setTempC(currentJo.getDouble(KEY_TEMP_C));
                currentWeather.setTempF(currentJo.getDouble(KEY_TEMP_F));
                currentWeather.setIs_day(currentJo.getInt(KEY_IS_DAY));
                currentWeather.setWindKph(currentJo.getDouble(KEY_WIND_KPH));
                currentWeather.setWindMph(currentJo.getDouble(KEY_WIND_MPH));
                currentWeather.setWindDegree(currentJo.getInt(KEY_WIND_DEGREE));
                currentWeather.setWindDir(currentJo.getString(KEY_WIND_DIR));
                currentWeather.setPressureMb(currentJo.getDouble(KEY_PRESSURE_MB));
                currentWeather.setPressureIn(currentJo.getDouble(KEY_PRESSURE_IN));
                currentWeather.setPrecipMm(currentJo.getDouble(KEY_PRECIP_MM));
                currentWeather.setPrecipIn(currentJo.getDouble(KEY_PRECIP_IN));
                currentWeather.setHumidity(currentJo.getInt(KEY_HUMIDITY));
                currentWeather.setCloud(currentJo.getInt(KEY_CLOUD));
                currentWeather.setFeelslikeC(currentJo.getDouble(KEY_FEELSLIKE_C));
                currentWeather.setFeelslikeF(currentJo.getDouble(KEY_FEELSLIKE_F));
                currentWeather.setVisability_km(currentJo.getDouble(KEY_VIS_KM));
                currentWeather.setVisability_mi(currentJo.getDouble(KEY_VIS_MILES));

                Condition condition = new Condition();
                condition.setText(conditionJo.getString(KEY_TEXT));
                condition.setIcon(conditionJo.getString(KEY_ICON));
                condition.setCode(conditionJo.getInt(KEY_CODE));
                currentWeather.setCondition(condition);
            }
        } catch (JSONException e) {
            WeatherActivity.isConnected = false;
        }
        AppManager.getInstance().getCurrentLocation().setName(locationCallbackName);
        AppManager.getInstance().getCurrentLocation().setCurrentWeather(currentWeather);
    }
}
