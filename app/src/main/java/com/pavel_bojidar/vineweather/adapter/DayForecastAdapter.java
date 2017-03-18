package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.DayForecastAdapter.DayForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.Location.Forecast;

import java.util.List;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class DayForecastAdapter extends RecyclerView.Adapter<DayForecastViewHolder> {

    private List<Forecast> dailyForecast;

    public DayForecastAdapter(List<Forecast> dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    @Override
    public DayForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_day_forecast, null));
    }

    @Override
    public void onBindViewHolder(DayForecastViewHolder holder, int position) {

        Forecast forecast = dailyForecast.get(position);
        holder.date.setText(Helper.getUnixDate(forecast.getUnixTimestamp()));
        holder.temp.setText(String.valueOf((int) forecast.getTemperature() - Constants.COEF_FOR_CONVERT_CELSIUS) + "c");
        holder.hour.setText(Helper.getUnixHour(forecast.getUnixTimestamp()));
        holder.condition.setText(forecast.getWeatherCondition());

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo show detailed forecast
            }
        });

    }

    @Override
    public int getItemCount() {
        return dailyForecast.size();
    }

    public class DayForecastViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView hour;
        TextView temp;
        TextView condition;
        ImageView imageView;


        public DayForecastViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            hour = (TextView) itemView.findViewById(R.id.forecast_hour);
            temp = (TextView) itemView.findViewById(R.id.temp);
            condition = (TextView) itemView.findViewById(R.id.condition);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
