package com.pavel_bojidar.vineweather.popupwindow;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Pavlov on 3/16/2017.
 */

public class CitySearchPopupWindow extends ListPopupWindow {

    private SearchAdapter searchAdapter;
    private Context context;
    private ArrayList<String> items = new ArrayList();

    public CitySearchPopupWindow(@NonNull Context context, ArrayList<String> cityList) {
        super(context);
        this.context = context;
        setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
        updateSuggestionsList(cityList);
    }

    public void updateSuggestionsList(ArrayList<String> cityList) {
        items.clear();
        for (String entry : cityList) {
            items.add(entry);
        }
        if (searchAdapter == null) {
            searchAdapter = new SearchAdapter(context, R.layout.row_autocomplete_search, items);
            setAdapter(searchAdapter);
        } else {
            searchAdapter.notifyDataSetChanged();
        }
    }

    private class SearchAdapter extends ArrayAdapter<String> {

        public SearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            SearchViewHolder holder;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_autocomplete_search, null);
                holder = new SearchViewHolder();
                holder.cityName = (TextView) convertView.findViewById(R.id.citySuggestion);
                convertView.setTag(holder);
            } else {
                holder = (SearchViewHolder) convertView.getTag();
            }
            holder.cityName.setText(getItem(position));

            return convertView;
        }

        private class SearchViewHolder {
            TextView cityName;
        }
    }
}
