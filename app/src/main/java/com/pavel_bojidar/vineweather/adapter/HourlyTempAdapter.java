package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyTempAdapter.HourlyForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 4/7/2017.
 */

public class HourlyTempAdapter extends RecyclerView.Adapter<HourlyForecastViewHolder> {

    private ArrayList<HourForecast> forecast;
    private int fragmentNumber;

    public HourlyTempAdapter(ArrayList<HourForecast> forecast, int fragmentNumber) {
        this.forecast = forecast;
        this.fragmentNumber = fragmentNumber;
    }

    @Override
    public HourlyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HourlyForecastViewHolder holder = null;
        switch (fragmentNumber) {
            case 0:
                holder = new HourlyForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_today_hourly_temp, null));
                break;
            case 1:
                holder = new HourlyForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_tomorrow_hourly_temp, null));
                break;
            case 2:
                holder = new HourlyForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_forecast_hourly_temp, null));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(HourlyForecastViewHolder holder, int position) {
        HourForecast currentHour = forecast.get(position);
        holder.degrees.setText(Helper.decimalFormat(currentHour.getTempC()).concat(Constants.CELSIUS_SYMBOL));
        holder.hour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
        holder.icon.setImageDrawable(Helper.chooseWeatherConditionIcon(holder.itemView.getContext(), currentHour.getIsDay() == 1, currentHour.getCondition().getIcon()));
    }

    @Override
    public int getItemCount() {
        return forecast.size();
    }

    class HourlyForecastViewHolder extends RecyclerView.ViewHolder {

        TextView degrees, hour;
        ImageView icon;

        HourlyForecastViewHolder(View itemView) {
            super(itemView);
            degrees = (TextView) itemView.findViewById(R.id.row_forecast_details_degrees);
            hour = (TextView) itemView.findViewById(R.id.row_forecast_details_hour);
            icon = (ImageView) itemView.findViewById(R.id.row_forecast_details_icon);
        }
    }
}
