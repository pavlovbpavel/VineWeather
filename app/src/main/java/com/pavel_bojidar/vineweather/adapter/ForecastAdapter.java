package com.pavel_bojidar.vineweather.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.ForecastAdapter.ForecastViewHolder;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import java.util.List;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastViewHolder>{

    //todo List<Forecast> forecasts
    private List<Forecast> forecasts;

    public ForecastAdapter(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ForecastViewHolder(parent.inflate(parent.getContext(),R.layout.row_forecast, null));
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        //Forecast forecast = forecasts.get(position);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo show detailed forecast
            }
        });

    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder{

        TextView content;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.forecast_content);
        }
    }
}
