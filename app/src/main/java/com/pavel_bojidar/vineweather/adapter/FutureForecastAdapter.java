package com.pavel_bojidar.vineweather.adapter;

import android.support.transition.AutoTransition;
import android.support.transition.ChangeBounds;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.WeatherActivity;
import com.pavel_bojidar.vineweather.adapter.FutureForecastAdapter.ForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.DayForecast;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;

/**
 * Created by Pavel Pavlov on 3/18/2017.
 */

public class FutureForecastAdapter extends RecyclerView.Adapter<ForecastViewHolder> {

    private Forecast forecast;
    private Transition transition;

    public FutureForecastAdapter(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        parent.setClipToPadding(false);
        return new ForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_forecast, null));
    }

    @Override
    public void onBindViewHolder(final ForecastViewHolder holder, int position) {
        DayForecast currentDay = forecast.getDayForecasts().get(position);
        HourlyTempAdapter adapter = new HourlyTempAdapter(currentDay.getHourForecasts(), 2);
        holder.hourlyForecast.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.hourlyForecast.setLayoutManager(layoutManager);

        holder.itemView.setBackgroundResource(R.color.row);

        holder.layout.setOnClickListener(new OnClickListener() {

            boolean visible;

            @Override
            public void onClick(View v) {
                if (!visible) {
                    transition = new ChangeBounds();
                    transition.setDuration(400);
                } else {
                    transition = new AutoTransition();
                    transition.setDuration(200);
                }
                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView.getParent(), transition);
                visible = !visible;
                holder.masterLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        if (position == 0) {
            holder.date.setText(R.string.TodayText);
        } else if (position == 1) {
            holder.date.setText("Tomorrow".concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        } else {
            holder.date.setText(Helper.getWeekDay(Helper.getUnixDate(currentDay.getDateEpoch())).concat(Helper.getUnixCustomDate(currentDay.getDateEpoch())));
        }
        holder.condition.setText(currentDay.getDay().getCondition().getText());
        holder.sun.setText(currentDay.getAstro().getSunrise().concat(", ").concat(currentDay.getAstro().getSunset()));
        holder.moon.setText(currentDay.getAstro().getMoonrise().concat(", ").concat(currentDay.getAstro().getMoonset()));
        if (!WeatherActivity.isFahrenheit) {
            holder.tempMax.setText(Helper.decimalFormat(currentDay.getDay().getMaxtempC()).concat(Constants.CELSIUS_SYMBOL));
            holder.tempMin.setText(Helper.decimalFormat(currentDay.getDay().getMintempC()).concat(Constants.CELSIUS_SYMBOL));
        } else {
            holder.tempMax.setText(Helper.decimalFormat(currentDay.getDay().getMaxtempF()).concat(Constants.CELSIUS_SYMBOL));
            holder.tempMin.setText(Helper.decimalFormat(currentDay.getDay().getMintempF()).concat(Constants.CELSIUS_SYMBOL));
        }
        holder.wind.setText(String.valueOf(Helper.decimalFormat(currentDay.getDay().getMaxwindKph())).concat(" " + Constants.KM_H));
        holder.humidity.setText(String.valueOf(Helper.decimalFormat(currentDay.getDay().getAvgHumidity()).concat(Constants.HUMIDITY_SYMBOL)));
        holder.conditionImage.setImageDrawable(Helper.chooseConditionIcon((holder.itemView.getContext()), true, true, currentDay.getDay().getCondition().getText()));
    }

    @Override
    public int getItemCount() {
        return forecast.getDayForecasts().size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {

        private ImageView conditionImage;
        private RecyclerView hourlyForecast;
        private RelativeLayout masterLayout;
        private RelativeLayout layout;
        private TextView date, tempMin, tempMax, condition, wind, humidity, sun, moon;

        ForecastViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date_week);
            sun = (TextView) itemView.findViewById(R.id.hidden_content_sun);
            condition = (TextView) itemView.findViewById(R.id.condition_week);
            wind = (TextView) itemView.findViewById(R.id.hidden_content_wind);
            moon = (TextView) itemView.findViewById(R.id.hidden_content_moon);
            tempMin = (TextView) itemView.findViewById(R.id.temperature_week_min);
            tempMax = (TextView) itemView.findViewById(R.id.temperature_week_max);
            layout = (RelativeLayout) itemView.findViewById(R.id.row_week_forecast);
            humidity = (TextView) itemView.findViewById(R.id.hidden_content_humidity);
            conditionImage = (ImageView) itemView.findViewById(R.id.condition_image_week);
            masterLayout = (RelativeLayout) itemView.findViewById(R.id.forecast_details_master_layout);
            hourlyForecast = (RecyclerView) itemView.findViewById(R.id.forecast_details_recycler_view);
        }
    }
}
