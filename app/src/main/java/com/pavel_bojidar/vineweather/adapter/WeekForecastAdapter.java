package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.WeekForecastAdapter.WeekForecastViewHolder;
import com.pavel_bojidar.vineweather.model.Location.Forecast;

import java.util.List;

/**
 * Created by Pavel Pavlov on 3/18/2017.
 */

public class WeekForecastAdapter extends RecyclerView.Adapter<WeekForecastViewHolder> {

    private List<Forecast> weeklyForecast;

    public WeekForecastAdapter(List<Forecast> weeklyForecast) {
        this.weeklyForecast = weeklyForecast;
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeekForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_week_forecast, null));
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        Forecast forecast = weeklyForecast.get(position);
        holder.temperature.setText(String.valueOf(forecast.getTemperature()));
    }

    @Override
    public int getItemCount() {
        return weeklyForecast.size();
    }

    public class WeekForecastViewHolder extends RecyclerView.ViewHolder {

        TextView dayOfWeek, date, temperature;
        ImageView conditionImage;

        public WeekForecastViewHolder(View itemView) {
            super(itemView);
            dayOfWeek = (TextView) itemView.findViewById(R.id.day_of_week);
            date = (TextView) itemView.findViewById(R.id.date);
            temperature = (TextView) itemView.findViewById(R.id.temperature);
            conditionImage = (ImageView) itemView.findViewById(R.id.condition_image);
        }
    }
}
