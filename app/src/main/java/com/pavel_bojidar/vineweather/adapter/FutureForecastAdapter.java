package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.FutureForecastAdapter.WeekForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;

import java.util.List;

/**
 * Created by Pavel Pavlov on 3/18/2017.
 */

public class FutureForecastAdapter extends RecyclerView.Adapter<WeekForecastViewHolder> {

    private Forecast forecast;

    public FutureForecastAdapter(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    public WeekForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeekForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_week_forecast, null));
    }

    @Override
    public void onBindViewHolder(WeekForecastViewHolder holder, int position) {
        DayForecast currentDay = forecast.getDayForecasts().get(position);
        if(position%2==0){
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }
//        String units = AppManager.getInstance().getUnits();
//        if (units.equals(Constants.KEY_CELSIUS)) {
//            holder.tempMin.setText(Constants.TEMP_MIN + Helper.decimalFormat(dayForecast.getMinTemperature()) + Constants.CELSIUS_SYMBOL);
//            holder.tempMax.setText(Constants.TEMP_MAX + Helper.decimalFormat(dayForecast.getMaxTemperature()) + Constants.CELSIUS_SYMBOL);
//        } else {
//            holder.tempMin.setText(Constants.TEMP_MIN + Helper.decimalFormat(Helper.kelvinToFahrenheit(dayForecast.getMinTemperature())) + Constants.FAHRENHEIT_SYMBOL);
//            holder.tempMax.setText(Constants.TEMP_MAX + Helper.decimalFormat(Helper.kelvinToFahrenheit(dayForecast.getMaxTemperature())) + Constants.FAHRENHEIT_SYMBOL);
//        }
//        holder.date.setText(Helper.getWeekDay(Helper.getUnixDate(dayForecast.getForecasts().get(0).getUnixTimestamp())));
//        holder.condition.setText(dayForecast.getMidCondition());
//        switch (dayForecast.getMidCondition()) {
//            case "Rain":
//                holder.conditionImage.setBackgroundResource(R.drawable.drizzle);
//                break;
//            case "Clouds":
//                holder.conditionImage.setBackgroundResource(R.drawable.cloudy);
//                break;
//            case "Clear":
//                holder.conditionImage.setBackgroundResource(R.drawable.clear);
//                break;
//            case "Snow":
//                holder.conditionImage.setBackgroundResource(R.drawable.snow);
//                break;
//            case "Fog":
//                holder.conditionImage.setBackgroundResource(R.drawable.fog);
//                break;
//            case "Mist":
//                holder.conditionImage.setBackgroundResource(R.drawable.mist);
//        }
    }

    @Override
    public int getItemCount() {
        return forecast.getDayForecasts().size();
    }

    public class WeekForecastViewHolder extends RecyclerView.ViewHolder {

        TextView date, tempMin, tempMax, condition;
        ImageView conditionImage;

        public WeekForecastViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_week);
            tempMin = (TextView) itemView.findViewById(R.id.temperature_week_min);
            tempMax = (TextView) itemView.findViewById(R.id.temperature_week_max);
            condition = (TextView) itemView.findViewById(R.id.condition_week);
            conditionImage = (ImageView) itemView.findViewById(R.id.condition_image_week);
        }
    }
}
