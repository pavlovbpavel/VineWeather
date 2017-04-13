package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyPrecipAdapter.HourlyPrecipViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.List;

/**
 * Created by Pavel Pavlov on 4/13/2017.
 */

public class HourlyPrecipAdapter extends RecyclerView.Adapter<HourlyPrecipViewHolder> {

    private List<HourForecast> hourlyPrecip;

    public HourlyPrecipAdapter(List<HourForecast> hourlyPrecip) {
        this.hourlyPrecip = hourlyPrecip;
    }

    @Override
    public HourlyPrecipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HourlyPrecipViewHolder(parent.inflate(parent.getContext(), R.layout.row_precipitation_chance, null));
    }

    @Override
    public void onBindViewHolder(HourlyPrecipViewHolder holder, int position) {
        HourForecast currentHour = hourlyPrecip.get(position);
        holder.volume.setText(String.valueOf(currentHour.getPrecipMm() == 0 ? "-" : currentHour.getPrecipMm()));
        holder.hour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
    }

    @Override
    public int getItemCount() {
        return hourlyPrecip.size();
    }

    class HourlyPrecipViewHolder extends RecyclerView.ViewHolder {

        TextView volume, hour;
        ImageView drop;

        public HourlyPrecipViewHolder(View itemView) {
            super(itemView);
            volume = (TextView) itemView.findViewById(R.id.row_precip_volume);
            hour = (TextView) itemView.findViewById(R.id.row_precip_hour);
            drop = (ImageView) itemView.findViewById(R.id.row_drop_image);
        }
    }
}
