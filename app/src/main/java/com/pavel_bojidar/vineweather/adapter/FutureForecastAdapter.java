package com.pavel_bojidar.vineweather.adapter;

import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public ForecastViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_forecast, null));
        this.parent = parent;
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(final ForecastViewHolder holder, int position) {
        DayForecast currentDay = forecast.getDayForecasts().get(position);

        HourlyTempAdapter adapter = new HourlyTempAdapter(currentDay.getHourForecasts());
        holder.hourlyForecast.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.hourlyForecast.setLayoutManager(layoutManager);

        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }

        holder.layout.setOnClickListener(new OnClickListener() {

            boolean visible;

            @Override
            public void onClick(View v) {
                Transition transition = new AutoTransition();
                transition.setDuration(200);
                TransitionManager.beginDelayedTransition(parent, transition);
                visible = !visible;
                holder.masterLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        if (position == 0) {
            holder.date.setText("Today");
        } else if (position == 1) {
            holder.date.setText("Tomorrow".concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        } else {
            holder.date.setText(Helper.getWeekDay(Helper.getUnixDate(currentDay.getDateEpoch())).concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        }
        holder.tempMax.setText(Helper.decimalFormat(currentDay.getDay().getMaxtempC()) + Constants.CELSIUS_SYMBOL);
        holder.tempMin.setText(Helper.decimalFormat(currentDay.getDay().getMintempC()) + Constants.CELSIUS_SYMBOL);
        holder.condition.setText(currentDay.getDay().getCondition().getText());
        holder.conditionImage.setImageDrawable(Helper.chooseIcon(parent.getContext(), true, currentDay.getDay().getCondition().getIcon()));
        holder.wind.setText(String.valueOf(Helper.decimalFormat(currentDay.getDay().getMaxwindKph())).concat(" " + Constants.KM_H));
        holder.humidity.setText(String.valueOf(Helper.decimalFormat(currentDay.getDay().getAvgHumidity()).concat(Constants.HUMIDITY_SYMBOL)));
        holder.sun.setText(currentDay.getAstro().getSunrise().concat(", ").concat(currentDay.getAstro().getSunset()));
        holder.moon.setText(currentDay.getAstro().getMoonrise().concat(", ").concat(currentDay.getAstro().getMoonset()));
    }

    @Override
    public int getItemCount() {
        return forecast.getDayForecasts().size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView date, tempMin, tempMax, condition, wind, humidity, sun, moon;
        ImageView conditionImage;
        LinearLayout layout, masterLayout;
        RecyclerView hourlyForecast;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_week);
            tempMin = (TextView) itemView.findViewById(R.id.temperature_week_min);
            tempMax = (TextView) itemView.findViewById(R.id.temperature_week_max);
            condition = (TextView) itemView.findViewById(R.id.condition_week);
            conditionImage = (ImageView) itemView.findViewById(R.id.condition_image_week);
            layout = (LinearLayout) itemView.findViewById(R.id.row_week_forecast);
            masterLayout = (LinearLayout) itemView.findViewById(R.id.forecast_details_master_layout);
            wind = (TextView) itemView.findViewById(R.id.hidden_content_wind);
            humidity = (TextView) itemView.findViewById(R.id.hidden_content_humidity);
            sun = (TextView) itemView.findViewById(R.id.hidden_content_sun);
            moon = (TextView) itemView.findViewById(R.id.hidden_content_moon);
            hourlyForecast = (RecyclerView) itemView.findViewById(R.id.forecast_details_recycler_view);
        }
    }
}
