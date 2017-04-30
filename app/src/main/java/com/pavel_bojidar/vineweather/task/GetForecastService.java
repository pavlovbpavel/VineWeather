package com.pavel_bojidar.vineweather.task;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.pavel_bojidar.vineweather.ConfigurationActivity;
import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.Condition;
import com.pavel_bojidar.vineweather.model.DayDetails;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.pavel_bojidar.vineweather.BroadcastActions.ACTION_DOWNLOAD_FINISHED;
import static com.pavel_bojidar.vineweather.Constants.API_KEY;
import static com.pavel_bojidar.vineweather.Constants.KEY_AVGTEMP_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_AVGTEMP_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_AVGVIS_KM;
import static com.pavel_bojidar.vineweather.Constants.KEY_AVGVIS_MILES;
import static com.pavel_bojidar.vineweather.Constants.KEY_AVG_HUMIDITY;
import static com.pavel_bojidar.vineweather.Constants.KEY_CLOUD;
import static com.pavel_bojidar.vineweather.Constants.KEY_CODE;
import static com.pavel_bojidar.vineweather.Constants.KEY_DATE;
import static com.pavel_bojidar.vineweather.Constants.KEY_DATE_EPOCH;
import static com.pavel_bojidar.vineweather.Constants.KEY_DEWPOINT_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_DEWPOINT_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_FEELSLIKE_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_FEELSLIKE_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_HEATINDEX_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_HEATINDEX_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_HUMIDITY;
import static com.pavel_bojidar.vineweather.Constants.KEY_ICON;
import static com.pavel_bojidar.vineweather.Constants.KEY_IS_DAY;
import static com.pavel_bojidar.vineweather.Constants.KEY_LAST_UPDATED;
import static com.pavel_bojidar.vineweather.Constants.KEY_LAST_UPDATED_EPOCH;
import static com.pavel_bojidar.vineweather.Constants.KEY_MAXTEMP_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_MAXTEMP_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_MAXWIND_KPH;
import static com.pavel_bojidar.vineweather.Constants.KEY_MAXWIND_MPH;
import static com.pavel_bojidar.vineweather.Constants.KEY_MINTEMP_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_MINTEMP_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_MOONRISE;
import static com.pavel_bojidar.vineweather.Constants.KEY_MOONSET;
import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRECIP_IN;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRECIP_MM;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRESSURE_IN;
import static com.pavel_bojidar.vineweather.Constants.KEY_PRESSURE_MB;
import static com.pavel_bojidar.vineweather.Constants.KEY_SUNRISE;
import static com.pavel_bojidar.vineweather.Constants.KEY_SUNSET;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEMP_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEMP_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_TEXT;
import static com.pavel_bojidar.vineweather.Constants.KEY_TIME;
import static com.pavel_bojidar.vineweather.Constants.KEY_TIME_EPOCH;
import static com.pavel_bojidar.vineweather.Constants.KEY_TOTALPRECIP_IN;
import static com.pavel_bojidar.vineweather.Constants.KEY_TOTALPRECIP_MM;
import static com.pavel_bojidar.vineweather.Constants.KEY_VIS_KM;
import static com.pavel_bojidar.vineweather.Constants.KEY_VIS_MILES;
import static com.pavel_bojidar.vineweather.Constants.KEY_WILL_IT_RAIN;
import static com.pavel_bojidar.vineweather.Constants.KEY_WILL_IT_SNOW;
import static com.pavel_bojidar.vineweather.Constants.KEY_WINDCHILL_C;
import static com.pavel_bojidar.vineweather.Constants.KEY_WINDCHILL_F;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_DEGREE;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_DIR;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_KPH;
import static com.pavel_bojidar.vineweather.Constants.KEY_WIND_MPH;
import static com.pavel_bojidar.vineweather.Constants.NODE_ASTRO;
import static com.pavel_bojidar.vineweather.Constants.NODE_CONDITION;
import static com.pavel_bojidar.vineweather.Constants.NODE_CURRENT;
import static com.pavel_bojidar.vineweather.Constants.NODE_DAY;
import static com.pavel_bojidar.vineweather.Constants.NODE_FORECAST;
import static com.pavel_bojidar.vineweather.Constants.NODE_FORECASTDAY;
import static com.pavel_bojidar.vineweather.Constants.NODE_HOUR;
import static com.pavel_bojidar.vineweather.Constants.NODE_LOCATION;

/**
 * Created by Pavel Pavlov on 4/29/2017.
 */

public class GetForecastService extends IntentService {

    public static final String EXTRA_LOCATION_NAME = "params";

    public GetForecastService() {
        super("GetForecastService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("service", "onHandleIntent");
        StringBuilder result = new StringBuilder();
        String validInput = null;
        String locationName = intent.getStringExtra(EXTRA_LOCATION_NAME);
        try {
            validInput = URLEncoder.encode(locationName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Forecast forecast = null;
        CurrentWeather currentWeather = null;
        try {
            URL url = new URL(
                    "http://api.apixu.com/v1/forecast.json?key=" + API_KEY + "&q=" + validInput + "&days=10");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (http.getResponseCode() > 400) {
                WeatherActivity.isConnected = false;
            } else {
                WeatherActivity.isConnected = true;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            }

            String locationCallbackName = null;
            currentWeather = new CurrentWeather();
            forecast = new Forecast();
            ArrayList<DayForecast> dayForecasts = new ArrayList<>();
            try {
                JSONObject jo = new JSONObject(String.valueOf(result));
                JSONArray daysJsonArray = jo.getJSONObject(NODE_FORECAST).getJSONArray(NODE_FORECASTDAY);
                locationCallbackName = jo.getJSONObject(NODE_LOCATION).getString(KEY_NAME);

                //current weather data
                if (jo.has(NODE_CURRENT)) {
                    JSONObject currentJo = jo.getJSONObject(NODE_CURRENT);
                    JSONObject conditionJo = currentJo.getJSONObject(NODE_CONDITION);

                    currentWeather.setLastUpdated(currentJo.getString(KEY_LAST_UPDATED));
                    currentWeather.setLastUpdateEpoch(currentJo.getInt(KEY_LAST_UPDATED_EPOCH));
                    currentWeather.setTempC(currentJo.getDouble(KEY_TEMP_C));
                    currentWeather.setTempF(currentJo.getDouble(KEY_TEMP_F));
                    currentWeather.setIsDay(currentJo.getInt(KEY_IS_DAY));
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
                    currentWeather.setVisabilityKm(currentJo.getDouble(KEY_VIS_KM));
                    currentWeather.setVisabilityMi(currentJo.getDouble(KEY_VIS_MILES));

                    Condition condition = new Condition();
                    condition.setText(conditionJo.getString(KEY_TEXT));
                    condition.setIcon(conditionJo.getString(KEY_ICON));
                    condition.setCode(conditionJo.getInt(KEY_CODE));
                    currentWeather.setCondition(condition);
                }

                //forecast data
                for (int i = 0; i < daysJsonArray.length(); i++) {
                    JSONObject currentDayJo = daysJsonArray.getJSONObject(i);
                    DayForecast currentDayForecast = new DayForecast();

                    //date data
                    currentDayForecast.setDate(currentDayJo.getString(KEY_DATE));
                    currentDayForecast.setDateEpoch(currentDayJo.getInt(KEY_DATE_EPOCH));

                    //main day data
                    DayDetails currentDayDetails = new DayDetails();
                    JSONObject dayDataJo = currentDayJo.getJSONObject(NODE_DAY);
                    JSONObject conditionJo = dayDataJo.getJSONObject(NODE_CONDITION);
                    currentDayDetails.setMaxtempC(dayDataJo.getDouble(KEY_MAXTEMP_C));
                    currentDayDetails.setMaxtempF(dayDataJo.getDouble(KEY_MAXTEMP_F));
                    currentDayDetails.setMintempC(dayDataJo.getDouble(KEY_MINTEMP_C));
                    currentDayDetails.setMintempF(dayDataJo.getDouble(KEY_MINTEMP_F));
                    currentDayDetails.setAvgtempC(dayDataJo.getDouble(KEY_AVGTEMP_C));
                    currentDayDetails.setAvgtempF(dayDataJo.getDouble(KEY_AVGTEMP_F));
                    currentDayDetails.setMaxwindKph(dayDataJo.getDouble(KEY_MAXWIND_KPH));
                    currentDayDetails.setMaxwindMph(dayDataJo.getDouble(KEY_MAXWIND_MPH));
                    currentDayDetails.setAvgHumidity(dayDataJo.getDouble(KEY_AVG_HUMIDITY));
                    currentDayDetails.setTotalprecipMm(dayDataJo.getDouble(KEY_TOTALPRECIP_MM));
                    currentDayDetails.setTotalprecipIn(dayDataJo.getDouble(KEY_TOTALPRECIP_IN));
                    currentDayDetails.setAvgVisibilityKm(dayDataJo.getDouble(KEY_AVGVIS_KM));
                    currentDayDetails.setAvgVisibilityMiles(dayDataJo.getDouble(KEY_AVGVIS_MILES));
                    Condition condition = new Condition();
                    condition.setText(conditionJo.getString(KEY_TEXT));
                    condition.setIcon(conditionJo.getString(KEY_ICON));
                    condition.setCode(conditionJo.getInt(KEY_CODE));
                    currentDayDetails.setCondition(condition);

                    //main astro data
                    JSONObject astroJo = currentDayJo.getJSONObject(NODE_ASTRO);
                    currentDayForecast.setSunrise(astroJo.getString(KEY_SUNRISE));
                    currentDayForecast.setSunset(astroJo.getString(KEY_SUNSET));
                    currentDayForecast.setMoonrise(astroJo.getString(KEY_MOONRISE));
                    currentDayForecast.setMoonset(astroJo.getString(KEY_MOONSET));

                    //hourly data
                    JSONArray hourlyForecast = currentDayJo.getJSONArray(NODE_HOUR);
                    ArrayList<HourForecast> hourForecasts = new ArrayList<>();
                    for (int j = 0; j < hourlyForecast.length(); j++) {
                        JSONObject currentHourJO = hourlyForecast.getJSONObject(j);
                        JSONObject conditionHourJO = currentHourJO.getJSONObject(NODE_CONDITION);
                        HourForecast currentHourForecast = new HourForecast();
                        //main hour data
                        currentHourForecast.setTimeEpoch(currentHourJO.getInt(KEY_TIME_EPOCH));
                        currentHourForecast.setTime(currentHourJO.getString(KEY_TIME));
                        currentHourForecast.setTempC(currentHourJO.getDouble(KEY_TEMP_C));
                        currentHourForecast.setTempF(currentHourJO.getDouble(KEY_TEMP_F));
                        currentHourForecast.setIsDay(currentHourJO.getInt(KEY_IS_DAY));
                        currentHourForecast.setWindKph(currentHourJO.getDouble(KEY_WIND_KPH));
                        currentHourForecast.setWindMph(currentHourJO.getDouble(KEY_WIND_MPH));
                        currentHourForecast.setWindDegree(currentHourJO.getInt(KEY_WIND_DEGREE));
                        currentHourForecast.setWindDir(currentHourJO.getString(KEY_WIND_DIR));
                        currentHourForecast.setPressureMb(currentHourJO.getDouble(KEY_PRESSURE_MB));
                        currentHourForecast.setPressureIn(currentHourJO.getDouble(KEY_PRESSURE_IN));
                        currentHourForecast.setPrecipMm(currentHourJO.getDouble(KEY_PRECIP_MM));
                        currentHourForecast.setPrecipIn(currentHourJO.getDouble(KEY_PRECIP_IN));
                        currentHourForecast.setHumidity(currentHourJO.getInt(KEY_HUMIDITY));
                        currentHourForecast.setCloud(currentHourJO.getInt(KEY_CLOUD));
                        currentHourForecast.setFeelslikeC(currentHourJO.getDouble(KEY_FEELSLIKE_C));
                        currentHourForecast.setFeelslikeF(currentHourJO.getDouble(KEY_FEELSLIKE_F));
                        currentHourForecast.setWindchillC(currentHourJO.getDouble(KEY_WINDCHILL_C));
                        currentHourForecast.setWindchillF(currentHourJO.getDouble(KEY_WINDCHILL_F));
                        currentHourForecast.setHeatindexC(currentHourJO.getDouble(KEY_HEATINDEX_C));
                        currentHourForecast.setHeatindexF(currentHourJO.getDouble(KEY_HEATINDEX_F));
                        currentHourForecast.setDewpointC(currentHourJO.getDouble(KEY_DEWPOINT_C));
                        currentHourForecast.setDewpointF(currentHourJO.getDouble(KEY_DEWPOINT_F));
                        currentHourForecast.setWillItRain(currentHourJO.getInt(KEY_WILL_IT_RAIN));
                        currentHourForecast.setWillItSnow(currentHourJO.getInt(KEY_WILL_IT_SNOW));
                        currentHourForecast.setVisibilityKm(currentHourJO.getDouble(KEY_VIS_KM));
                        currentHourForecast.setVisibilityMiles(currentHourJO.getDouble(KEY_VIS_MILES));
                        //condition for this hour
                        Condition conditionHour = new Condition();
                        conditionHour.setText(conditionHourJO.getString(KEY_TEXT));
                        conditionHour.setIcon(conditionHourJO.getString(KEY_ICON));
                        conditionHour.setCode(conditionHourJO.getInt(KEY_CODE));
                        currentHourForecast.setCondition(conditionHour);
                        hourForecasts.add(currentHourForecast);
                    }
                    currentDayForecast.setHourForecasts(hourForecasts);
                    currentDayForecast.setDayDetails(currentDayDetails);
                    dayForecasts.add(currentDayForecast);
                }
                forecast.setDayForecasts(dayForecasts);
            } catch (JSONException e) {
                WeatherActivity.isConnected = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Bundle forecastBundle = new Bundle();
        forecastBundle.putString("locationName", locationName);
        forecastBundle.putSerializable("forecastResult", forecast);
        forecastBundle.putSerializable("currentWeatherResult", currentWeather);

        if(MaPaWidgetProvider.getWidgetLocations().containsKey(locationName)){
            for(int widgetId : MaPaWidgetProvider.getWidgetLocations().get(locationName)){
                Log.e("service", "updating widget");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ma_pa_widget);

                views.setTextViewText(R.id.location_widget, locationName);
                views.setTextViewText(R.id.degree_widget, Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));
                views.setTextViewText(R.id.condition_widget, currentWeather.getCondition().getText());

                appWidgetManager.updateAppWidget(widgetId, views);
            }
        }

//        Intent widgetUpdateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, MaPaWidgetProvider.class)));
//        widgetUpdateIntent.putExtras(forecastBundle);
//        sendBroadcast(widgetUpdateIntent);


        Intent intentResult = new Intent(ACTION_DOWNLOAD_FINISHED);
        intentResult.putExtras(forecastBundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentResult);
    }

}
