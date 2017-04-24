package com.pavel_bojidar.vineweather;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pavel_bojidar.vineweather.NetworkReceiver.ConnectivityChanged;
import com.pavel_bojidar.vineweather.adapter.RecentListAdapter;
import com.pavel_bojidar.vineweather.adapter.RecentListAdapter.RecentSelectedListener;
import com.pavel_bojidar.vineweather.fragment.FragmentForecast;
import com.pavel_bojidar.vineweather.fragment.FragmentToday;
import com.pavel_bojidar.vineweather.fragment.FragmentTomorrow;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.SearchCity;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.popupwindow.CitySearchPopupWindow;
import com.pavel_bojidar.vineweather.singleton.AppManager;
import com.pavel_bojidar.vineweather.task.GetCurrentWeather;
import com.pavel_bojidar.vineweather.task.GetForecast;
import com.pavel_bojidar.vineweather.task.GetLocations;
import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static com.pavel_bojidar.vineweather.Constants.KEY_LOCATION_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;


public class WeatherActivity extends AppCompatActivity implements RecentSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerView recentLocationRecyclerView;
    private SharedPreferences preferences;
    private String currentLocationName;
    private ProgressBar loadingView;
    private EditText searchField;
    final boolean[] weatherTasksCompleted = {false, false};
    private CitySearchPopupWindow searchPopupWindow;
    private Toolbar toolbar;
    private ArrayList<String> recentList = new ArrayList<>();
    private MenuItem search;
    private Timer inputDelay;
    private ImageView navDrawerImage;
    private TextView navDrawerDegree, navDrawerCondition;
    private Button celsiusButton, fahrenheitButton;
    private AlertDialog alertDialog;
    private ArrayList<SearchCity> searchCities = new ArrayList<>();
    public static boolean isImperialUnits;
    private int currentTabColor = R.color.todayAppBarColor;
    private int currentTabColorDark = R.color.todayAppBarColorDark;
    private GoogleApiClient mGoogleApiClient;
    private boolean isInitialRun = true;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        isImperialUnits = preferences.getString(Constants.KEY_UNIT_TYPE, "metric").equals("imperial");

        initViews();

        buildGoogleApiClient();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        MaPaWidgetProvider.startService(this);
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(WeatherActivity.this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentLocationName == null) {
            mGoogleApiClient.connect();
        } else {
            startWeatherTasks();
            searchField.setHint(Helper.filterCityName(currentLocationName));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        preferences = getSharedPreferences("Recent List", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("searchPlace", null);
        if (json != null) {
            Type type = new TypeToken<List<SearchCity>>() {
            }.getType();
            List<SearchCity> cities = gson.fromJson(json, type);
            Collections.sort(cities, new Comparator<SearchCity>() {
                @Override
                public int compare(SearchCity o1, SearchCity o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            for (int i = 0; i < cities.size(); i++) {
                addToRecentList(cities.get(i).getName());
            }
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void startWeatherTasks() {
        if (isNetworkAvailable()) {
            loadingView.setVisibility(View.VISIBLE);
            new GetCurrentWeather(new WeakReference<Activity>(this)) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    weatherTasksCompleted[0] = true;
                    onLocationUpdated();
                }
            }.execute(currentLocationName);

            new GetForecast(new WeakReference<Activity>(this)) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    weatherTasksCompleted[1] = true;
                    onLocationUpdated();
                }
            }.execute(currentLocationName);
        } else {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
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

    public void onLocationUpdated() {
        if (!weatherTasksCompleted[0] || !weatherTasksCompleted[1]) {
            return;
        }
        weatherTasksCompleted[0] = false;
        weatherTasksCompleted[1] = false;
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();
        navDrawerImage.setImageDrawable(Helper.chooseConditionIcon(this, currentWeather.getIs_day() == 1, false,
                currentWeather.getCondition().getText()));
        navDrawerCondition.setText(currentWeather.getCondition().getText());
        if (!isImperialUnits) {
            navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));
        } else {
            navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempF()).concat(Constants.CELSIUS_SYMBOL));
        }

        if (viewPager.getAdapter() == null) {
            setViewPagerAdapter();
        }

        AppManager.getInstance().onLocationUpdated(this);
        if (isInitialRun) {
            currentLocationName = AppManager.getInstance().getCurrentLocation().getName();
            searchField.setHint(currentLocationName);
            isInitialRun = false;
            removeFromRecentList(currentLocationName);
        }
        swipeRefresh.setRefreshing(false);
    }


    public void onUnitSwapped() {
        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();
        if (!isImperialUnits) {
            navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));
        } else {
            navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempF()).concat(Constants.CELSIUS_SYMBOL));
        }
        AppManager.getInstance().onUnitSwapped(this);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        celsiusButton = (Button) findViewById(R.id.nav_drawer_celsius_button);
        fahrenheitButton = (Button) findViewById(R.id.nav_drawer_fahrenheit_button);

        if (isImperialUnits) {
            fahrenheitButton.getBackground().clearColorFilter();
            celsiusButton.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        } else {
            celsiusButton.getBackground().clearColorFilter();
            fahrenheitButton.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        }

        celsiusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isImperialUnits = false;
                preferences.edit().putString(Constants.KEY_UNIT_TYPE, "metric").apply();
                celsiusButton.getBackground().clearColorFilter();
                fahrenheitButton.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
                drawer.closeDrawer(GravityCompat.START);
                onUnitSwapped();
            }
        });

        fahrenheitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isImperialUnits = true;
                preferences.edit().putString(Constants.KEY_UNIT_TYPE, "imperial").apply();
                fahrenheitButton.getBackground().clearColorFilter();
                celsiusButton.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
                drawer.closeDrawer(GravityCompat.START);
                onUnitSwapped();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                startWeatherTasks();
            }
        });

        navDrawerImage = (ImageView) hView.findViewById(R.id.nav_drawer_image);
        navDrawerDegree = (TextView) hView.findViewById(R.id.nav_drawer_degree);
        navDrawerCondition = (TextView) hView.findViewById(R.id.nav_drawer_condition);

        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchField.hasFocus()) {
                    search.setIcon(R.drawable.ic_close_black_24dp);
                }
            }
        });
        searchField.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    search.setIcon(R.drawable.ic_close_black_24dp);
                } else {
                    searchField.clearFocus();
                    search.setIcon(R.drawable.ic_search_black_24dp);
                }
            }
        });
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                //api returns result after 3 input symbols
                if (s.length() > 2) {
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
                                    Log.e("perform search", "before search");
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
                searchField.clearFocus();
                if (currentLocationName != null && !currentLocationName.isEmpty()) {
                    searchField.setText(null);
                    searchField.setHint(Helper.filterCityName(currentLocationName));
                } else {
                    searchField.setText(null);
                    searchField.setHint("Select a city");
                }
                search.setIcon(R.drawable.ic_search_black_24dp);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        loadingView = (ProgressBar) findViewById(R.id.loading_view);

        recentLocationRecyclerView = (RecyclerView) findViewById(R.id.favorite_location_list);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        appBar = (AppBarLayout) findViewById(R.id.app_bar);

        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        animateColorChange(appBar, currentTabColor, R.color.todayAppBarColor);
                        animateStatusBarColorChange(currentTabColorDark, R.color.todayAppBarColorDark);
                        currentTabColorDark = R.color.todayAppBarColorDark;
                        currentTabColor = R.color.todayAppBarColor;
                        break;
                    case 1:
                        animateColorChange(appBar, currentTabColor, R.color.tomorrowAppBarColor);
                        animateStatusBarColorChange(currentTabColorDark, R.color.tomorrowAppBarColorDark);
                        currentTabColorDark = R.color.tomorrowAppBarColorDark;
                        currentTabColor = R.color.tomorrowAppBarColor;
                        break;
                    case 2:
                        animateColorChange(appBar, currentTabColor, R.color.forecastAppBarColor);
                        animateStatusBarColorChange(currentTabColorDark, R.color.forecastAppBarColorDark);
                        currentTabColorDark = R.color.forecastAppBarColorDark;
                        currentTabColor = R.color.forecastAppBarColor;
                        break;
                }
                swipeRefresh.setColorSchemeResources((currentTabColor));
                viewPager.setCurrentItem(tab.getPosition(), true);
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
                if (state == SCROLL_STATE_DRAGGING) {
                    swipeRefresh.setEnabled(false);
                } else {
                    swipeRefresh.setEnabled(true);
                }
            }
        });
    }

    private void setViewPagerAdapter() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] tabTitles = new String[]{"Today", "Tomorrow", "10 Days"};

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FragmentToday();
                    case 1:
                        return new FragmentTomorrow();
                    case 2:
                        return new FragmentForecast();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles[position];
            }
        });
    }

    private void performSearch(Editable s) {
        if (isNetworkAvailable()) {
            new GetLocations() {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    try {
                        //callback is the returned result from the API call
                        JSONArray callback = new JSONArray(result);
                        ArrayList<String> cityNames = new ArrayList<>();
                        for (int i = 0; i < callback.length(); i++) {
                            JSONObject currentItem = callback.getJSONObject(i);
                            cityNames.add(currentItem.getString(KEY_NAME));
                        }

                        if (searchPopupWindow == null) {
                            searchPopupWindow = new CitySearchPopupWindow(WeatherActivity.this, cityNames);
                            searchPopupWindow.setAnchorView(toolbar);
                        }
                        searchPopupWindow.updateSuggestionsList(cityNames);
                        if (!cityNames.isEmpty()) {
                            searchPopupWindow.show();
                        } else {
                            searchPopupWindow.dismiss();
                        }
                        if (searchPopupWindow.isShowing()) {
                            searchPopupWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            searchPopupWindow.setInputMethodMode(CitySearchPopupWindow.INPUT_METHOD_NEEDED);
                            searchPopupWindow.setForceIgnoreOutsideTouch(true);
                            showKeyboard();
                            searchPopupWindow.getListView().setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String chosenCity = (String) searchPopupWindow.getListView().getItemAtPosition(position);
                                    searchPopupWindow.dismiss();
                                    if (currentLocationName != null && !currentLocationName.isEmpty()) {
                                        addToRecentList(currentLocationName);
                                    }
                                    currentLocationName = chosenCity;
                                    preferences.edit().putString(Constants.KEY_LOCATION_NAME, currentLocationName).apply();
                                    hideKeyboard();
                                    searchField.setText(null);
                                    searchField.setHint(Helper.filterCityName(chosenCity));
                                    searchField.clearFocus();
                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                    search.setIcon(R.drawable.ic_search_black_24dp);
                                    startWeatherTasks();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(s.toString());
        } else {
            showAlertDialog();
        }
    }

    private void addToRecentList(String cityName) {

        for (int i = 0; i < recentList.size(); i++) {
            if (recentList.get(i).equalsIgnoreCase(cityName)) {
                return;
            }
        }
        if (recentList.size() == 10) {
            recentList.remove(recentList.get(9));
            recentList.add(0, cityName);
        } else {
            recentList.add(0, cityName);
        }

        recentLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            recentLocationRecyclerView.setAdapter(new RecentListAdapter(recentList, this));
        }

        preferences = getSharedPreferences("Recent List", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        SearchCity city = new SearchCity(cityName, new Date());
        searchCities.add(city);
        Gson gson = new Gson();
        String jsonCities = gson.toJson(searchCities);
        edit.putString("searchPlace", jsonCities);
        edit.apply();
    }

    private void removeFromRecentList(String cityName) {
        if (recentList.contains(cityName)) {
            recentList.remove(cityName);
        }
        recentLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            recentLocationRecyclerView.setAdapter(new RecentListAdapter(recentList, this));
        }
    }

    private void reorderRecentList(String selectedLocationName) {
        recentList.remove(selectedLocationName);
        recentLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            recentLocationRecyclerView.setAdapter(new RecentListAdapter(recentList, this));
        }
    }

    @Override
    public void onRecentSelected(String selectedLocation) {
        drawer.closeDrawer(GravityCompat.START);
        if (!selectedLocation.equalsIgnoreCase(currentLocationName)) {
            if (recentList.contains(selectedLocation)) {
                reorderRecentList(selectedLocation);
            }
            addToRecentList(currentLocationName);
            currentLocationName = selectedLocation;
            new AsyncTask<String, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loadingView.setVisibility(View.VISIBLE);
                    loadingView.bringToFront();
                }

                @Override
                protected Void doInBackground(String... params) {
                    startWeatherTasks();
                    return null;
                }

                @Override
                protected void onPostExecute(Void location) {
                    super.onPostExecute(location);
                    searchField.setHint(Helper.filterCityName(currentLocationName));
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
                        if (currentLocationName != null && !currentLocationName.isEmpty()) {
                            searchField.setHint(Helper.filterCityName(currentLocationName));
                        }
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
        showKeyboard(getCurrentFocus());
    }

    public void showKeyboard(View focusedView) {
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (searchPopupWindow.isShowing()) {
            searchPopupWindow.dismiss();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                currentLocationName = String.valueOf(mLastLocation.getLatitude()).concat(" ").concat(String.valueOf(mLastLocation.getLongitude()));
                startWeatherTasks();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    currentLocationName = String.valueOf(mLastLocation.getLatitude()).concat(" ").concat(String.valueOf(mLastLocation.getLongitude()));
                    startWeatherTasks();
                }
            } else {
                if (preferences.getString(Constants.KEY_LOCATION_NAME, null) != null) {
                    currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, null);
                    startWeatherTasks();
                } else {
                    Toast.makeText(this, "Permission not granted, please choose a location.", Toast.LENGTH_SHORT).show();
                    viewPager.setVisibility(View.GONE);
                    loadingView.setVisibility(View.GONE);
                    setTitle(null);
                    searchField.setHint("Select a city");
                    searchField.clearFocus();
                    searchField.requestFocus();
                    searchField.performClick();
                    showKeyboard(searchField);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection to Google Location Services timed out.", Toast.LENGTH_SHORT).show();
        currentLocationName = preferences.getString(KEY_LOCATION_NAME, null);
        startWeatherTasks();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to Google Location Services.", Toast.LENGTH_SHORT).show();
        currentLocationName = preferences.getString(KEY_LOCATION_NAME, null);
        startWeatherTasks();
    }

    private void changeStatusBarColor(int resID) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(resID);
        }
    }

    private void animateColorChange(final View view, int from, int to) {
        int colorFrom = getResources().getColor(from);
        int colorTo = getResources().getColor(to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    private void animateStatusBarColorChange(int from, int to) {
        int colorFrom = getResources().getColor(from);
        int colorTo = getResources().getColor(to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                changeStatusBarColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
