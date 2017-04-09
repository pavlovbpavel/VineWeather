package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.HourlyWindAdapter.WindViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.List;

/**
 * Created by ASUS on 8.4.2017 г..
 */

public class HourlyWindAdapter extends RecyclerView.Adapter<WindViewHolder> {

    private List<HourForecast> hourForecast;

    public HourlyWindAdapter(List<HourForecast> hourForecast) {
        this.hourForecast = hourForecast;
    }

    @Override
    public WindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WindViewHolder(parent.inflate(parent.getContext(), R.layout.row_wind, null));
    }

    @Override
    public void onBindViewHolder(WindViewHolder holder, int position) {
        HourForecast currentHour = hourForecast.get(position);
        holder.windProc.setText(Helper.decimalFormat(currentHour.getWindKph()));
        holder.windHour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
        holder.windIcon.setRotation(currentHour.getWindDegree());
    }

    @Override
    public int getItemCount() {
        return hourForecast.size();
    }

    public class WindViewHolder extends RecyclerView.ViewHolder {

        ImageView windIcon;
        TextView windProc;
        TextView windHour;

        public WindViewHolder(View row) {
            super(row);
            windIcon = (ImageView) row.findViewById(R.id.row_wind_icon);
            windProc = (TextView) row.findViewById(R.id.row_wind_procent);
            windHour = (TextView) row.findViewById(R.id.row_wind_hour);
        }
    }
}
