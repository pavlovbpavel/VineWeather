package com.pavel_bojidar.vineweather.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.popupwindow.CitySearchPopupWindow;
import com.pavel_bojidar.vineweather.task.GetLocations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;

/**
 * Created by Pavel Pavlov on 4/30/2017.
 */

public class SearchHelper {

    static Timer inputDelay;

    public static void performSearch(final Context context, final CitySearchPopupWindow searchPopupWindow, final OnItemClickListener listener, String searchString){
        new GetLocations() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    //callback is the returned result from the API call
                    JSONArray callback = new JSONArray(result);
                    ArrayList<String> cityNames = new ArrayList<>();
                    for (int i = 0; i < callback.length(); i++) {
                        JSONObject currentItem = callback.getJSONObject(i);
                        cityNames.add(currentItem.getString(KEY_NAME));
                    }

                    searchPopupWindow.updateSuggestionsList(cityNames);
                    if (!cityNames.isEmpty()) {

                        searchPopupWindow.show();
                    } else {
                        searchPopupWindow.dismiss();
                    }
                    if (searchPopupWindow.isShowing()) {
                        searchPopupWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        searchPopupWindow.getListView().setOnItemClickListener(listener);
                    }
                } catch (JSONException e) {

                }
            }
        }.execute(searchString);
    }

    public static void initSearchField(final EditText searchField, final MenuItem searchIcon, final Search listener){

    }

    public interface Search{
        void onSearch(Editable s);
        void dismissDialog();
    }
}
