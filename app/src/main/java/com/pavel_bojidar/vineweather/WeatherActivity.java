package com.pavel_bojidar.vineweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter;
import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter.OnFavouriteSelected;
import com.pavel_bojidar.vineweather.fragment.FragmentDay;
import com.pavel_bojidar.vineweather.fragment.FragmentNow;
import com.pavel_bojidar.vineweather.fragment.FragmentWeek;
import com.pavel_bojidar.vineweather.model.Location;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;
import com.pavel_bojidar.vineweather.task.GetCurrentWeather;
import com.pavel_bojidar.vineweather.task.GetForecast;
import com.pavel_bojidar.vineweather.task.LoadCitiesFromFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WeatherActivity extends AppCompatActivity implements OnFavouriteSelected {

    DrawerLayout drawer;
    TabLayout tabLayout;
    ViewPager viewPager;
    RecyclerView favoriteLocationRecyclerView;
    SharedPreferences preferences; //shared preferences contains currentLocation/recents(json) name and ID
    String currentLocationName;
    int currentLocationId;
    ProgressBar loadingView;
    EditText searchField;
    final boolean[] weatherTasksCompleted = {false, false};
    TextView noLocationSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        loadCitiesFromAssetsFile();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//        preferences.edit().putInt(Constants.KEY_LOCATION_ID, 727011).commit();
//        preferences.edit().putString(Constants.KEY_LOCATION_NAME, "Sofia").commit();
        currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, null);
        currentLocationId = preferences.getInt(Constants.KEY_LOCATION_ID, -1);
        initViews();

        if (currentLocationId == -1) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            noLocationSelected.setVisibility(View.VISIBLE);
            setTitle(null);
            searchField.requestFocus();
            searchField.setHint("Select a city");
            //todo request location from user
        } else {
            startWeatherTasks();
        }
    }

    private void startWeatherTasks() {
        loadingView.setVisibility(View.VISIBLE);

        new GetCurrentWeather(new WeakReference<Activity>(this)) {
            @Override
            protected void onPostExecute(Location location) {
                super.onPostExecute(location);
                weatherTasksCompleted[0] = true;
                onLocationUpdated(location);
            }
        }.execute(currentLocationId);

        new GetForecast(new WeakReference<Activity>(this)) {
            @Override
            protected void onPostExecute(Location location) {
                super.onPostExecute(location);
                weatherTasksCompleted[1] = true;
                onLocationUpdated(location);
            }
        }.execute(currentLocationId);
    }

    private void loadCitiesFromAssetsFile() {
        AssetManager am = getAssets();
        try {
            InputStream is = am.open("city_list.json");
            new LoadCitiesFromFile().execute(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLocationUpdated(Location location) {
        if (!weatherTasksCompleted[0] || !weatherTasksCompleted[1]) {
            return;
        }
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        setTitle(location.getName());
        if (viewPager.getAdapter() == null) {
            setViewPagerAdapter();
        }
        AppManager.getInstance().onLocationUpdated(this, location);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        loadingView = (ProgressBar) findViewById(R.id.loading_view);

        favoriteLocationRecyclerView = (RecyclerView) findViewById(R.id.favorite_location_list);
        setFavoritesList();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Now"));
        tabLayout.addTab(tabLayout.newTab().setText("24H"));
        tabLayout.addTab(tabLayout.newTab().setText("5Days"));
        viewPager = (ViewPager) findViewById(R.id.pager);

        noLocationSelected = (TextView) findViewById(R.id.no_location);

        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setViewPagerAdapter() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FragmentNow();
                    case 1:
                        return new FragmentDay();
                    case 2:
                        return new FragmentWeek();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    private Location simulateData() {
        Location sofia = new Location();
        sofia.setName("Sofia");
        ArrayList<Forecast> forecasts = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            forecasts.add(new Forecast(i));
        }
        sofia.setForecasts(forecasts);
        return sofia;
    }

    private void setFavoritesList() {
        //static temporary data for demo
        String[] cityNames = {"Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin",
                "Veliko Tarnovo", "Pazardjik", "London", "Leicester", "Liverpool", "New York", "Tokyo",
                "Bern", "Rome", "Barcelona", "Paris", "Amsterdam", "Copenhagen", "Ruse"};

        List<Location> favoritesList = new ArrayList<>();
        for (int i = 0; i < cityNames.length; i++) {
            favoritesList.add(new Location());
            favoritesList.get(i).setName(cityNames[i]);
        }
        favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Set<String> favoritesSet = preferences.getStringSet(Constants.KEY_FAVORITE_LOCATIONS, null);
        if (favoritesSet != null) {
            favoriteLocationRecyclerView.setAdapter(new FavoritesListAdapter(new ArrayList<>(favoritesSet), this));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFavouriteSelected(String selectedLocation) {
        drawer.closeDrawer(GravityCompat.START);

        new AsyncTask<String, Void, Location>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingView.setVisibility(View.VISIBLE);
                loadingView.bringToFront();
            }

            @Override
            protected Location doInBackground(String... params) {
                //todo load JSON, parse, return
                try {
                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return simulateData();
            }

            @Override
            protected void onPostExecute(Location location) {
                super.onPostExecute(location);
                onLocationUpdated(location);
            }
        }.execute(selectedLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem search = menu.add("Search");
        search.setIcon(R.drawable.ic_search_black_24dp);
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        search.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (searchField.getVisibility() != View.VISIBLE) {
                    searchField.setVisibility(View.VISIBLE);
                    search.setIcon(R.drawable.ic_close_black_24dp);
                    searchField.requestFocus();
                } else {
                    if (searchField.getText().length() > 0) {
                        searchField.setText(null);
                    } else {
                        search.setIcon(R.drawable.ic_search_black_24dp);
                        searchField.setVisibility(View.GONE);
                        hideKeyboard();
                    }
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    private void filterResults(String search) {
//        ArrayList<String> match = new ArrayList<>();
//        for(String string : strings){
//            if(string.contains(search)) {
//                match.add(string);
//            }
//        }
//        log(String.valueOf(match));
//
//


}
