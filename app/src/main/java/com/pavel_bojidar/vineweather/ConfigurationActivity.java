package com.pavel_bojidar.vineweather;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.receiver.ForecastResponseReceiver;
import com.pavel_bojidar.vineweather.receiver.ForecastResponseReceiver.ForecastUpdate;
import com.pavel_bojidar.vineweather.task.GetForecastService;
import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pavel_bojidar.vineweather.BroadcastActions.ACTION_DOWNLOAD_FINISHED;

public class ConfigurationActivity extends BaseActivity {

    @Override
    public OnItemClickListener getSearchPopupListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentLocationName = (String) searchPopupWindow.getListView().getItemAtPosition(position);
                searchPopupWindow.dismiss();
                searchMenu.setIcon(R.drawable.ic_search_black_24dp);
                Intent intent = new Intent(ConfigurationActivity.this, GetForecastService.class);
                intent.putExtra(GetForecastService.EXTRA_LOCATION_NAME, currentLocationName);
                startService(intent);
            }
        };
    }

    private int appWidgetId;
    private ForecastResponseReceiver responseReceiver = new ForecastResponseReceiver(new ForecastUpdate() {
        @Override
        public void onLocationUpdate(String currentLocationName, CurrentWeather currentWeather, Forecast forecast) {
            Log.e("confinguration", currentLocationName);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ConfigurationActivity.this);
            RemoteViews views = new RemoteViews(ConfigurationActivity.this.getPackageName(), R.layout.ma_pa_widget);

            views.setTextViewText(R.id.location_widget, currentLocationName);
            views.setTextViewText(R.id.degree_widget, Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));
            views.setTextViewText(R.id.condition_widget, currentWeather.getCondition().getText());

            appWidgetManager.updateAppWidget(appWidgetId, views);

            HashMap<String, ArrayList<Integer>> list = MaPaWidgetProvider.getWidgetLocations();
            ArrayList<Integer> ids;
            if(list.containsKey(currentLocationName)){
                ids = list.get(currentLocationName);
                ids.add(appWidgetId);
            } else {
                ids = new ArrayList<>();
                ids.add(appWidgetId);
                list.put(currentLocationName, ids);
            }

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    });

    @Override
    protected int getContentView() {
        return R.layout.activity_configuration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        findViewById(R.id.launch_app).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigurationActivity.this, GetForecastService.class);
                intent.putExtra(GetForecastService.EXTRA_LOCATION_NAME, searchField.getText().toString());
                startService(intent);
            }
        });

        showKeyboard(this.getCurrentFocus());

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter(ACTION_DOWNLOAD_FINISHED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
    }
}
