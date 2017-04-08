package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.Constants;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.HourForecast;

import java.util.List;

/**
 * Created by ASUS on 8.4.2017 г..
 */

public class HourlyWindAdapter extends RecyclerView.Adapter<HourlyWindAdapter.WindViewHoler> {

    private List<HourForecast> hourForecast;
    private ViewGroup parent;

    public HourlyWindAdapter(List<HourForecast> hourForecast) {
        this.hourForecast = hourForecast;
    }

    @Override
    public WindViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        return new WindViewHoler(parent.inflate(parent.getContext(), R.layout.row_wind, null));
    }

    @Override
    public void onBindViewHolder(WindViewHoler holder, int position) {

        HourForecast currentHour = hourForecast.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.highlightedRow);
        }

        holder.windProc.setText(Helper.decimalFormat(currentHour.getWindKph()) + Constants.KM_H);
        holder.windHour.setText(Helper.getUnixAmPmHour(currentHour.getTimeEpoch()));
        holder.windIcon.setImageDrawable(Helper.chooseIcon(parent.getContext(), currentHour.getIsDay() == 1, currentHour.getCondition().getIcon()));

    }

    @Override
    public int getItemCount() {
        return hourForecast.size();
    }

    public class WindViewHoler extends RecyclerView.ViewHolder {

        ImageView windIcon;
        TextView windProc;
        TextView windHour;

        public WindViewHoler(View row) {
            super(row);
            windIcon = (ImageView) row.findViewById(R.id.row_wind_icon);
            windProc = (TextView) row.findViewById(R.id.row_wind_procent);
            windHour = (TextView) row.findViewById(R.id.row_wind_hour);
        }
    }
}
