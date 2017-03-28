package com.pavel_bojidar.vineweather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pavel_bojidar.vineweather.NetworkReceiver.ConnectivityChanged;
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
    SharedPreferences preferences;
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
    Button celsius, fahrenheit;
    BroadcastReceiver br;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        loadCitiesFromAssetsFile();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        preferences.edit().putInt(Constants.KEY_LOCATION_ID, 727011).apply();
        preferences.edit().putString(Constants.KEY_LOCATION_NAME, "Sofia").apply();
        currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, "Sofia");
        currentLocationId = preferences.getInt(Constants.KEY_LOCATION_ID, 727011);

        initViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        celsius.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppManager.getInstance().getUnits().equals(Constants.KEY_FAHRENHEIT)) {
                    AppManager.getInstance().setUnits(Constants.KEY_CELSIUS);
                    startWeatherTasks();
                    Log.e("task", "onCelsiusSelected");
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        fahrenheit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppManager.getInstance().getUnits().equals(Constants.KEY_CELSIUS)) {
                    AppManager.getInstance().setUnits(Constants.KEY_FAHRENHEIT);
                    startWeatherTasks();
                    Log.e("task", "onFahrenheitSelected");
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentLocationId == -1) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            noLocationSelected.setVisibility(View.VISIBLE);
            setTitle(null);
            searchField.setHint("Select a city");
            //todo request location from user
        } else {
            startWeatherTasks();
            searchField.setHint(currentLocationName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
    }

    private void startWeatherTasks() {
        if (isNetworkAvailable()) {
            loadingView.setVisibility(View.VISIBLE);
            CityInfo currentCityInfo = new CityInfo(currentLocationName, currentLocationId);
            new GetCurrentWeather(new WeakReference<Activity>(this)) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    weatherTasksCompleted[0] = true;
                    onLocationUpdated();
                }
            }.execute(currentCityInfo);

            new GetForecast(new WeakReference<Activity>(this)) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    weatherTasksCompleted[1] = true;
                    onLocationUpdated();
                }
            }.execute(currentCityInfo);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No internet Connection");
            builder.setMessage("Please turn on internet connection to continue");
            builder.setCancelable(false);
            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
            registerReceiver(new NetworkReceiver(new ConnectivityChanged() {
                @Override
                public void onConnected() {
                    if (alertDialog != null) {
                        alertDialog.hide();
                    }
                    startWeatherTasks();
                }
            }), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public void loadCitiesFromAssetsFile() {
        AssetManager am = getAssets();
        try {
            InputStream is = am.open("city_list.txt");
            new LoadCitiesFromFile().execute(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLocationUpdated() {
        if (!weatherTasksCompleted[0] || !weatherTasksCompleted[1]) {
            return;
        }
        weatherTasksCompleted[0] = false;
        weatherTasksCompleted[1] = false;
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        setTitle(AppManager.getInstance().getCurrentLocation().getName());
        if (viewPager.getAdapter() == null) {
            setViewPagerAdapter();
        }
        preferences.edit().putInt(Constants.KEY_LOCATION_ID, AppManager.getInstance().getCurrentLocation().getId()).apply();
        preferences.edit().putString(Constants.KEY_LOCATION_NAME, AppManager.getInstance().getCurrentLocation().getName()).apply();

        Log.e("task completed", "on location updated");
        AppManager.getInstance().onLocationUpdated(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    search.setIcon(R.drawable.ic_close_black_24dp);
                }
            }
        });
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
                    }, 200);
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
        celsius = (Button) findViewById(R.id.celsius);
        fahrenheit = (Button) findViewById(R.id.fahrenheit);

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

    private void performSearch(Editable s) {

        ArrayList<CityInfo> match = new ArrayList<>();
        ArrayList<String> cityNames = new ArrayList<>(AppManager.getInstance().getAllCities().keySet());
        for (String entry : cityNames) {
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
                    addToRecentList(currentLocationName, currentLocationId);
                    currentLocationId = currentItem.getId();
                    currentLocationName = currentItem.getName();
                    preferences.edit().putInt(Constants.KEY_LOCATION_ID, currentLocationId).apply();
                    preferences.edit().putString(Constants.KEY_LOCATION_NAME, currentLocationName).apply();
                    hideKeyboard();
                    searchField.setText(null);
                    searchField.setHint(currentLocationName);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    search.setIcon(R.drawable.ic_search_black_24dp);
                    viewPager.setCurrentItem(0, true);
                    startWeatherTasks();
                    Log.e("task", "on Search");
                }
            });
        }

    }

    private void addToRecentList(String cityName, int cityId) {

        CityInfo oldLocation = new CityInfo(cityName, cityId);

        for (int i = 0; i < recentList.size(); i++) {
            if (recentList.get(i).getId() == cityId) {
                return;
            }
        }
        recentList.add(0, oldLocation);

        favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            favoriteLocationRecyclerView.setAdapter(new FavoritesListAdapter(recentList, this));
        }
    }

    private void reorderRecentList(CityInfo selectedLocation){
        recentList.remove(selectedLocation);
        recentList.add(0, selectedLocation);
        favoriteLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            favoriteLocationRecyclerView.setAdapter(new FavoritesListAdapter(recentList, this));
        }
    }

    @Override
    public void onFavouriteSelected(CityInfo selectedLocation) {
        drawer.closeDrawer(GravityCompat.START);
        if (selectedLocation.getId() != currentLocationId) {
            if(recentList.contains(selectedLocation)){
                reorderRecentList(selectedLocation);
            }
            addToRecentList(currentLocationName, currentLocationId);
            currentLocationId = selectedLocation.getId();
            currentLocationName = selectedLocation.getName();
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
                    searchField.setHint(currentLocationName);
                    viewPager.setCurrentItem(0, true);
                }
            }.execute(selectedLocation);
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
                if (!searchField.hasFocus()) {
                    searchField.requestFocus();
                    search.setIcon(R.drawable.ic_close_black_24dp);
                    showKeyboard();
                } else {
                    if (searchField.getText().length() > 0) {
                        searchField.setText(null);
                    } else {
                        search.setIcon(R.drawable.ic_search_black_24dp);
                        searchField.setHint(currentLocationName);
                        searchField.clearFocus();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
