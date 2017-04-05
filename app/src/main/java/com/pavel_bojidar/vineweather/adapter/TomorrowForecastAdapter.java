package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.TomorrowForecastAdapter.DayForecastViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.List;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class TomorrowForecastAdapter extends RecyclerView.Adapter<DayForecastViewHolder> {


    private List<HourForecast> tomorrowForecast;

    public TomorrowForecastAdapter(List<HourForecast> hourForecasts) {
        this.tomorrowForecast = hourForecasts;
    }

    @Override
    public DayForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayForecastViewHolder(parent.inflate(parent.getContext(), R.layout.row_tomorrow, null));
    }

    @Override
    public void onBindViewHolder(DayForecastViewHolder holder, int position) {
        HourForecast hourForecast = tomorrowForecast.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }
        holder.date.setText(Helper.getUnixDate(hourForecast.getTimeEpoch()));
        holder.temp.setText(Helper.decimalFormat(hourForecast.getTempC()) + Constants.CELSIUS_SYMBOL);
        holder.hour.setText(Helper.getUnixHour(hourForecast.getTimeEpoch()));
        holder.condition.setText(hourForecast.getCondition().getText());

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo show detailed dayForecast
            }
        });
    }

    @Override
    public int getItemCount() {
        return tomorrowForecast.size();
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
