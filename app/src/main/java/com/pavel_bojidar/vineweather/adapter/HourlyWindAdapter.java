package com.pavel_bojidar.vineweather.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyWindAdapter.WindViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.List;

/**
 * Created by ASUS on 8.4.2017 г..
 */

public class HourlyWindAdapter extends RecyclerView.Adapter<WindViewHolder> {

    private List<HourForecast> hourForecast;
    ViewGroup parent;

    public HourlyWindAdapter(List<HourForecast> hourForecast) {
        this.hourForecast = hourForecast;
    }

    @Override
    public WindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WindViewHolder wvh = new WindViewHolder(parent.inflate(parent.getContext(), R.layout.row_wind, null));
        this.parent = parent;
        return wvh;
    }

    @Override
    public void onBindViewHolder(WindViewHolder holder, int position) {
        HourForecast currentHour = hourForecast.get(position);
        holder.windProc.setText(Helper.decimalFormat(currentHour.getWindKph()));
        holder.windHour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
        holder.windIcon.setRotation(currentHour.getWindDegree());
        LayoutParams lp = (LayoutParams) holder.windFill.getLayoutParams();

        int windSpeed = pxToDp((int) currentHour.getWindKph());
        int maxWindSpeed = 120;
        if (windSpeed <= 35) {
            lp.height = windSpeed * 15;
        } else {
            lp.height = windSpeed;
        }
//        lp.height = windSpeed*calculateTomorrowPercentage();
        holder.windFill.setLayoutParams(lp);
        if (currentHour.getWindKph() >= 20) {
            int color = Color.argb(255, 25, 140, 120);
            holder.windFill.setBackgroundColor(color);
        }
        if (currentHour.getWindKph() >= maxWindSpeed) {
            int color = Color.argb(255, 255, 0, 0);
            holder.windFill.setBackgroundColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return hourForecast.size();
    }

    class WindViewHolder extends RecyclerView.ViewHolder {

        ImageView windIcon;
        TextView windProc;
        TextView windHour;
        LinearLayout windFill;

        public WindViewHolder(View row) {
            super(row);
            windIcon = (ImageView) row.findViewById(R.id.row_wind_icon);
            windProc = (TextView) row.findViewById(R.id.row_wind_percent);
            windHour = (TextView) row.findViewById(R.id.row_wind_hour);
            windFill = (LinearLayout) row.findViewById(R.id.wind_fill);
        }
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = parent.getContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int calculateTomorrowPercentage() {
        Forecast forecast = AppManager.getInstance().getCurrentLocation().getForecast();
        double maxWind = 120;
        double minWind = 50;
        for (int i = 0; i < forecast.getDayForecasts().get(1).getHourForecasts().size(); i++) {
            HourForecast currentHour = forecast.getDayForecasts().get(1).getHourForecasts().get(i);
            if (currentHour.getWindKph() > maxWind) {
                maxWind = currentHour.getWindKph();
            }
            if (currentHour.getWindKph() < minWind) {
                minWind = currentHour.getWindKph();
            }
        }
        return (int) ((minWind / maxWind) * 100);
    }
}
