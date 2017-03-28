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
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.List;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class DayForecastAdapter extends RecyclerView.Adapter<DayForecastViewHolder> {

    Forecast forecast;
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
        forecast = dailyForecast.get(position);
        if(position%2==0){
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }
        String units = AppManager.getInstance().getUnits();
        holder.date.setText(Helper.getUnixDate(forecast.getUnixTimestamp()));
        if (units.equals(Constants.KEY_CELSIUS)) {
            holder.temp.setText(Helper.decimalFormat(forecast.getTemperature() - Constants.COEF_FOR_CONVERT_CELSIUS) + Constants.CELSIUS_SYMBOL);
        } else {
            holder.temp.setText(Helper.decimalFormat(Helper.kelvinToFahrenheit(forecast.getTemperature())) + Constants.FAHRENHEIT_SYMBOL);
        }
        holder.hour.setText(Helper.getUnixHour(forecast.getUnixTimestamp()));

        holder.condition.setText(forecast.getWeatherCondition());
        switch (forecast.getWeatherCondition()) {
            case "Rain":
                holder.imageView.setBackgroundResource(R.drawable.drizzle);
                break;
            case "Clouds":
                holder.imageView.setBackgroundResource(R.drawable.cloudy);
                break;
            case "Clear":
                holder.imageView.setBackgroundResource(R.drawable.clear);
                break;
            case "Snow":
                holder.imageView.setBackgroundResource(R.drawable.snow);
                break;
            case "Fog":
                holder.imageView.setBackgroundResource(R.drawable.fog);
                break;
            case "Mist":
                holder.imageView.setBackgroundResource(R.drawable.mist);
        }

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
            date = (TextView) itemView.findViewById(R.id.date_daily);
            hour = (TextView) itemView.findViewById(R.id.forecast_hour);
            temp = (TextView) itemView.findViewById(R.id.teemperature_day);
            condition = (TextView) itemView.findViewById(R.id.condition_day);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
