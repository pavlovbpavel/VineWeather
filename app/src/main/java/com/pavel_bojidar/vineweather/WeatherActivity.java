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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter;
import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter.OnFavouriteSelected;
import com.pavel_bojidar.vineweather.fragment.FragmentDay;
import com.pavel_bojidar.vineweather.fragment.FragmentNow;
import com.pavel_bojidar.vineweather.fragment.FragmentWeek;
import com.pavel_bojidar.vineweather.model.Location.CityInfo;
import com.pavel_bojidar.vineweather.popupwindow.CitySearchPopupWindow;
import com.pavel_bojidar.vineweather.singleton.AppManager;
import com.pavel_bojidar.vineweather.task.GetCurrentWeather;
import com.pavel_bojidar.vineweather.task.GetForecast;
import com.pavel_bojidar.vineweather.task.LoadCitiesFromFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    CitySearchPopupWindow searchPopupWindow;
    Toolbar toolbar;
    ArrayList<CityInfo> recentList = new ArrayList<>();
    MenuItem search;
    Timer inputDelay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        loadCitiesFromAssetsFile();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        preferences.edit().putInt(Constants.KEY_LOCATION_ID, 727011).commit();
        preferences.edit().putString(Constants.KEY_LOCATION_NAME, "Sofia").commit();
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
            searchField.setVisibility(View.GONE);
            setTitle(currentLocationName);
        }
    }

    private void startWeatherTasks() {
        loadingView.setVisibility(View.VISIBLE);

        new GetCurrentWeather(new WeakReference<Activity>(this)) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                weatherTasksCompleted[0] = true;
                onLocationUpdated();
            }
        }.execute(currentLocationId);

        new GetForecast(new WeakReference<Activity>(this)) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                weatherTasksCompleted[1] = true;
                onLocationUpdated();
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

    public void onLocationUpdated() {
        if (!weatherTasksCompleted[0] || !weatherTasksCompleted[1]) {
            return;
        }
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        searchField.setVisibility(View.GONE);
        setTitle(AppManager.getInstance().getCurrentLocation().getName());
        if (viewPager.getAdapter() == null) {
            setViewPagerAdapter();
        }
        AppManager.getInstance().onLocationUpdated(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() > 1) {
                    if (inputDelay != null) {
                        inputDelay.cancel();
                    }
                    inputDelay = new Timer();
                    inputDelay.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    performSearch(s);
                                }
                            });
                        }
                    }, 500);
                } else {
                    if (searchPopupWindow != null) {
                        searchPopupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

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

    private void performSearch(Editable s) {
        ArrayList<CityInfo> match = new ArrayList<>();
        for (String entry : new ArrayList<>(AppManager.getInstance().getAllCities().keySet())) {
            if (entry.toLowerCase().contains(s.toString().toLowerCase())) {
                match.add(new CityInfo(entry, AppManager.getInstance().getAllCities().get(entry)));
            }
        }

        if (searchPopupWindow == null) {
            searchPopupWindow = new CitySearchPopupWindow(WeatherActivity.this, match);
            searchPopupWindow.setAnchorView(toolbar);
        }
        searchPopupWindow.updateSuggestionsList(match);
        if (!match.isEmpty()) {
            searchPopupWindow.show();
        } else {
            searchPopupWindow.dismiss();
        }
        if (searchPopupWindow.isShowing()) {
            searchPopupWindow.getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CityInfo currentItem = (CityInfo) searchPopupWindow.getListView().getItemAtPosition(position);
                    searchPopupWindow.dismiss();
                    addToRecentList(currentItem.getName(), currentItem.getId());
                    currentLocationId = currentItem.getId();
                    currentLocationName = currentItem.getName();
                    hideKeyboard();
                    searchField.setText(null);
                    searchField.setVisibility(View.GONE);
                    setTitle(currentLocationName);
                    search.setIcon(R.drawable.ic_search_black_24dp);
                    startWeatherTasks();
                }
            });
        }
    }

    private void addToRecentList(String cityName, int cityId) {

        for (int i = 0; i < recentList.size(); i++) {
            if (recentList.get(i).getId() == cityId) {
                return;
            }
        }
        recentList.add(new CityInfo(cityName, cityId));

        favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            favoriteLocationRecyclerView.setAdapter(new FavoritesListAdapter(recentList, this));
        }
    }

    @Override
    public void onFavouriteSelected(CityInfo selectedLocation) {
        drawer.closeDrawer(GravityCompat.START);
        currentLocationId = selectedLocation.getId();

        new AsyncTask<CityInfo, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingView.setVisibility(View.VISIBLE);
                loadingView.bringToFront();
            }

            @Override
            protected Void doInBackground(CityInfo... params) {
                startWeatherTasks();
                return null;
            }

            @Override
            protected void onPostExecute(Void location) {
                super.onPostExecute(location);

            }
        }.execute(selectedLocation);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        search = menu.add("Search");
        if (!searchField.hasFocus()) {
            search.setIcon(R.drawable.ic_search_black_24dp);
        } else {
            search.setIcon(R.drawable.ic_close_black_24dp);
        }
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        search.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (searchField.getVisibility() != View.VISIBLE) {
                    searchField.setVisibility(View.VISIBLE);
                    search.setIcon(R.drawable.ic_close_black_24dp);
                    searchField.requestFocus();
                    showKeyboard();
                } else {
                    if (searchField.getText().length() > 0) {
                        searchField.setText(null);
                    } else {
                        search.setIcon(R.drawable.ic_search_black_24dp);
                        searchField.setVisibility(View.GONE);
                        setTitle(currentLocationName);
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

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
