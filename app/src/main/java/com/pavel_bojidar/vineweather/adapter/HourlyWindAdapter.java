package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.adapter.HourlyWindAdapter.WindViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.List;

/**
 * Created by ASUS on 8.4.2017 г..
 */

public class HourlyWindAdapter extends RecyclerView.Adapter<WindViewHolder> {

    private List<HourForecast> hourForecast;
    private int maxWindSpeed;

    public HourlyWindAdapter(List<HourForecast> hourForecast) {
        this.hourForecast = hourForecast;
        this.maxWindSpeed = getMaxWindSpeed();
    }

    @Override
    public WindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WindViewHolder(parent.inflate(parent.getContext(), R.layout.row_wind, null));
    }

    @Override
    public void onBindViewHolder(WindViewHolder holder, int position) {
        HourForecast currentHour = hourForecast.get(position);
        if(WeatherActivity.isImperialUnits){
            holder.windSpeed.setText(Helper.decimalFormat(currentHour.getWindMph()));
        } else {
            holder.windSpeed.setText(Helper.decimalFormat(currentHour.getWindKph()));
        }
        holder.windHour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
        holder.windIcon.setRotation(currentHour.getWindDegree());
        LayoutParams lp = (LayoutParams) holder.windFill.getLayoutParams();

        lp.height = (int) (holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.wind_height) * (float) (currentHour.getWindKph() / maxWindSpeed));
        holder.windFill.setLayoutParams(lp);
        int windSpeed = (int)currentHour.getWindKph();
        if (windSpeed <= 5){
            holder.windFill.setBackgroundResource(R.color.windLow);
        }
        if (windSpeed > 5 && windSpeed <= 20){
            holder.windFill.setBackgroundResource(R.color.windAverage);
        }
        if (windSpeed > 20 && windSpeed <= 50){
            holder.windFill.setBackgroundResource(R.color.windHigh);
        }
        if (windSpeed > 50 && windSpeed <= 250){
            holder.windFill.setBackgroundResource(R.color.windExtreme);
        }
    }

    @Override
    public int getItemCount() {
        return hourForecast.size();
    }

    class WindViewHolder extends RecyclerView.ViewHolder {

        private ImageView windIcon;
        private TextView windSpeed, windHour;
        private LinearLayout windFill;

        WindViewHolder(View row) {
            super(row);
            windIcon = (ImageView) row.findViewById(R.id.row_wind_icon);
            windSpeed = (TextView) row.findViewById(R.id.row_wind_percent);
            windHour = (TextView) row.findViewById(R.id.row_wind_hour);
            windFill = (LinearLayout) row.findViewById(R.id.wind_fill);
        }
    }

    private int getMaxWindSpeed() {
        double maxWind = 0;
        for (int i = 0; i < hourForecast.size(); i++) {
            HourForecast currentHour = hourForecast.get(i);
            if (currentHour.getWindKph() > maxWind) {
                maxWind = currentHour.getWindKph();
            }
        }
        return (int) maxWind;
    }
}
