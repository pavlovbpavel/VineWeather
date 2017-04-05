package com.pavel_bojidar.vineweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;
import com.pavel_bojidar.vineweather.adapter.RecentListAdapter.FavoriteLocationViewHolder;
import com.pavel_bojidar.vineweather.helper.Helper;

import java.util.ArrayList;

/**
 * Created by Pavel Pavlov on 3/11/2017.
 */

public class RecentListAdapter extends RecyclerView.Adapter<FavoriteLocationViewHolder> {

    private ArrayList<String> locations;

    private RecentSelectedListener listener;

    public RecentListAdapter(ArrayList<String> locations, RecentSelectedListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @Override
    public FavoriteLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavoriteLocationViewHolder(parent.inflate(parent.getContext(), R.layout.row_favorites, null));
    }

    @Override
    public void onBindViewHolder(FavoriteLocationViewHolder holder, final int position) {
        final String location = locations.get(position);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecentSelected(location);
            }
        });
        holder.name.setText(Helper.filterCityName(location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class FavoriteLocationViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public FavoriteLocationViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.location_name);
        }
    }

    public interface RecentSelectedListener {
        void onRecentSelected(String selectedLocationName);
    }
}
