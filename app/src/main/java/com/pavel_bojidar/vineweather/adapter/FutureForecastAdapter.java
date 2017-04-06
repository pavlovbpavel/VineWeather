package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.FutureForecastAdapter.ForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;

/**
 * Created by Pavel Pavlov on 3/18/2017.
 */

public class FutureForecastAdapter extends RecyclerView.Adapter<ForecastViewHolder> {

    private Forecast forecast;
    ViewGroup parent;

    public FutureForecastAdapter(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_forecast, null));
        this.parent = parent;
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        DayForecast currentDay = forecast.getDayForecasts().get(position);
        int isDay = currentDay.getHourForecasts().get(position).getIsDay();
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }
        if(position == 0){
            holder.date.setText("Today");
        } else if(position == 1){
            holder.date.setText("Tomorrow".concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        } else {
            holder.date.setText(Helper.getWeekDay(Helper.getUnixDate(currentDay.getDateEpoch())).concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        }
        holder.tempMax.setText(Helper.decimalFormat(currentDay.getDay().getMaxtempC()) + Constants.CELSIUS_SYMBOL);
        holder.tempMin.setText(Helper.decimalFormat(currentDay.getDay().getMintempC()) + Constants.CELSIUS_SYMBOL);
        holder.condition.setText(currentDay.getDay().getCondition().getText());
        holder.conditionImage.setImageDrawable(Helper.chooseIcon(parent.getContext(), true, currentDay.getDay().getCondition().getIcon()));

    }

    @Override
    public int getItemCount() {
        return forecast.getDayForecasts().size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView date, tempMin, tempMax, condition;
        ImageView conditionImage;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_week);
            tempMin = (TextView) itemView.findViewById(R.id.temperature_week_min);
            tempMax = (TextView) itemView.findViewById(R.id.temperature_week_max);
            condition = (TextView) itemView.findViewById(R.id.condition_week);
            conditionImage = (ImageView) itemView.findViewById(R.id.condition_image_week);
        }
    }
}
