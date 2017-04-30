package com.pavel_bojidar.vineweather;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.pavel_bojidar.vineweather.adapter.RecentListAdapter;
import com.pavel_bojidar.vineweather.adapter.RecentListAdapter.RecentSelectedListener;
import com.pavel_bojidar.vineweather.fragment.FragmentForecast;
import com.pavel_bojidar.vineweather.fragment.FragmentToday;
import com.pavel_bojidar.vineweather.fragment.FragmentTomorrow;
import com.pavel_bojidar.vineweather.helper.Helper;
import com.pavel_bojidar.vineweather.model.maindata.CurrentWeather;
import com.pavel_bojidar.vineweather.model.maindata.Forecast;
import com.pavel_bojidar.vineweather.receiver.ForecastResponseReceiver;
import com.pavel_bojidar.vineweather.receiver.ForecastResponseReceiver.ForecastUpdate;
import com.pavel_bojidar.vineweather.singleton.AppManager;
import com.pavel_bojidar.vineweather.task.GetForecastService;

import java.util.ArrayList;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static com.pavel_bojidar.vineweather.BroadcastActions.ACTION_DOWNLOAD_FINISHED;
import static com.pavel_bojidar.vineweather.Constants.INTERNET_CONNECTION;
import static com.pavel_bojidar.vineweather.Constants.KEY_LOCATION_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_RECENT_PLACES;
import static com.pavel_bojidar.vineweather.Constants.SERVER_CONNECTION_FAILURE;

public class WeatherActivity extends BaseActivity implements RecentSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    protected RecyclerView recentLocationRecyclerView;
    private SharedPreferences preferences;
    private String currentLocationName;
    private ProgressBar loadingView;
    private RelativeLayout headerContainer;
    private ArrayList<String> recentList = new ArrayList<>();
    private ImageView navDrawerImage;
    private TextView navDrawerDegree, navDrawerCondition, noLocationSelected, navDrawerLocation;
    private Button celsiusButton, fahrenheitButton;
    public static boolean isImperialUnits;
    private int currentTabColor = R.color.todayAppBarColor;
    private int currentTabColorDark = R.color.todayAppBarColorDark;
    private GoogleApiClient mGoogleApiClient;
    private SwipeRefreshLayout swipeRefresh;
    protected boolean fromLocation, isDay;
    private RecentListAdapter recentAdapter;
    private Gson gson = new Gson();
    public static boolean isConnected = true;
    protected String conditionToday, conditionTomorrow;
    int[] conditionTodayColorSet, conditionTomorrowColorSet;
    private GradientDrawable gradient;

    private ForecastResponseReceiver responseReceiver = new ForecastResponseReceiver(new ForecastUpdate() {
        Bundle bundle;

        @Override
        public void onLocationUpdate(String currentLocationName, CurrentWeather currentWeather, Forecast forecast) {
            AppManager.getInstance().getCurrentLocation().setForecast(forecast);
            AppManager.getInstance().getCurrentLocation().setCurrentWeather(currentWeather);
            bundle = new Bundle();
            bundle.putSerializable("forecast", forecast);
            bundle.putSerializable("currentWeather", currentWeather);
            bundle.putString("locationName", currentLocationName);
            onLocationUpdated();
        }
    });

    @Override
    protected int getContentView() {
        return R.layout.activity_navigation_drawer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        isImperialUnits = preferences.getBoolean(Constants.UNITS_IMPERIAL, false); // default metric

        Gson gson = new Gson();
        String json = preferences.getString(KEY_RECENT_PLACES, null);
        if (json != null) {
            recentList = gson.fromJson(json, ArrayList.class);
        }

        recentAdapter = new RecentListAdapter(recentList, this);
        recentLocationRecyclerView.setAdapter(recentAdapter);

        buildGoogleApiClient();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentLocationName == null) {
            mGoogleApiClient.connect();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, new IntentFilter(ACTION_DOWNLOAD_FINISHED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        preferences.edit().putBoolean(Constants.UNITS_IMPERIAL, isImperialUnits).apply();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startWeatherTask() {
        if (isNetworkAvailable()) {
            if (noLocationSelected != null) {
                noLocationSelected.setVisibility(View.GONE);
            }
            loadingView.setVisibility(View.VISIBLE);
            Intent intent = new Intent(WeatherActivity.this, GetForecastService.class);
            intent.putExtra(GetForecastService.EXTRA_LOCATION_NAME, currentLocationName);
            startService(intent);
        } else {
            showAlertDialog(INTERNET_CONNECTION);
        }
    }

    @Override
    public OnItemClickListener getSearchPopupListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentLocationName = (String) searchPopupWindow.getListView().getItemAtPosition(position);
                searchPopupWindow.dismiss();
                addToRecentList(currentLocationName);
                preferences.edit().putString(Constants.KEY_LOCATION_NAME, currentLocationName).apply();
                hideKeyboard();
                searchField.setText(null);
                searchField.setHint(Helper.filterCityName(currentLocationName));
                searchField.clearFocus();
                getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
                searchMenu.setIcon(R.drawable.ic_search_black_24dp);
                startWeatherTask();
            }
        };
    }

    @Override
    protected void onSearchCanceled() {
        if (currentLocationName != null && !currentLocationName.isEmpty()) {
            searchField.setHint(Helper.filterCityName(currentLocationName));
        }
    }

    public void onLocationUpdated() {
        if (!isConnected) {
            showAlertDialog(SERVER_CONNECTION_FAILURE);
            return;
        }
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();

        isDay = currentWeather.getIsDay() == 1;
        conditionToday = currentWeather.getCondition().getText();
        conditionTomorrow = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDayDetails().getCondition().getText();
        conditionTodayColorSet = Helper.chooseConditionColorSet(conditionToday, isDay);
        conditionTomorrowColorSet = Helper.chooseConditionColorSet(conditionTomorrow, true);

        if (viewPager.getCurrentItem() == 0) {
            if (conditionTodayColorSet != null) {
                animateColorChange(appBar, currentTabColor, conditionTodayColorSet[0]);
                animateStatusBarColorChange(currentTabColorDark, conditionTodayColorSet[1]);
                currentTabColorDark = conditionTodayColorSet[1];
                currentTabColor = conditionTodayColorSet[0];
                gradient = Helper.chooseHeaderColorSet(this, conditionTodayColorSet);
                headerContainer.setBackground(gradient);
            }
        } else if (viewPager.getCurrentItem() == 1) {
            if (conditionTomorrowColorSet != null) {
                animateColorChange(appBar, currentTabColor, conditionTomorrowColorSet[0]);
                animateStatusBarColorChange(currentTabColorDark, conditionTomorrowColorSet[1]);
                currentTabColorDark = conditionTomorrowColorSet[1];
                currentTabColor = conditionTomorrowColorSet[0];
                gradient = Helper.chooseHeaderColorSet(this, conditionTomorrowColorSet);
                headerContainer.setBackground(gradient);
            }
        }

        swipeRefresh.setColorSchemeResources((currentTabColor));

        navDrawerImage.setImageDrawable(Helper.chooseConditionIcon(this, currentWeather.getIsDay() == 1, false, currentWeather.getCondition().getText()));
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

        if (fromLocation) {
            fromLocation = false;
            currentLocationName = AppManager.getInstance().getCurrentLocation().getName();
            searchField.setHint(currentLocationName);
            addToRecentList(currentLocationName);
        }
        navDrawerLocation.setText(Helper.filterCityName(currentLocationName));

        int conditionNameLength = currentWeather.getCondition().getText().length();
        if (conditionNameLength >= 25 && conditionNameLength < 30) {
            navDrawerCondition.setTextSize(14);
        } else if (conditionNameLength >= 35) {
            navDrawerCondition.setTextSize(10);
        } else {
            navDrawerCondition.setTextSize(16);
        }

        int locationNameLength = Helper.filterCityName(currentLocationName).length();
        if (locationNameLength >= 14 && locationNameLength <= 18) {
            navDrawerLocation.setTextSize(18);
        } else if (locationNameLength > 18) {
            navDrawerLocation.setTextSize(14);
        } else {
            navDrawerLocation.setTextSize(24);
        }

        swipeRefresh.setRefreshing(false);
    }

    public void onUnitSwapped() {
        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();
        if (currentWeather != null) {
            if (!isImperialUnits) {
                navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));
            } else {
                navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempF()).concat(Constants.CELSIUS_SYMBOL));
            }
        }
        AppManager.getInstance().onUnitSwapped(this);
    }

    protected void initViews() {
        super.initViews();
        noLocationSelected = (TextView) findViewById(R.id.no_location);
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
                startWeatherTask();
            }
        });

        navDrawerImage = (ImageView) hView.findViewById(R.id.nav_drawer_image);
        navDrawerDegree = (TextView) hView.findViewById(R.id.nav_drawer_degree);
        navDrawerCondition = (TextView) hView.findViewById(R.id.nav_drawer_condition);
        navDrawerLocation = (TextView) hView.findViewById(R.id.nav_drawer_location);
        headerContainer = (RelativeLayout) hView.findViewById(R.id.header_container_layout);


        setSupportActionBar(toolbar);

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
                searchMenu.setIcon(R.drawable.ic_search_black_24dp);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadingView = (ProgressBar) findViewById(R.id.loading_view);

        recentLocationRecyclerView = (RecyclerView) findViewById(R.id.favorite_location_list);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        appBar = (AppBarLayout) findViewById(R.id.app_bar);

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        animateColorChange(appBar, currentTabColor, conditionTodayColorSet[0]);
                        animateStatusBarColorChange(currentTabColorDark, conditionTodayColorSet[1]);
                        currentTabColorDark = conditionTodayColorSet[1];
                        currentTabColor = conditionTodayColorSet[0];
                        gradient = Helper.chooseHeaderColorSet(WeatherActivity.this, conditionTodayColorSet);
                        headerContainer.setBackground(gradient);
                        break;
                    case 1:
                        animateColorChange(appBar, currentTabColor, conditionTomorrowColorSet[0]);
                        animateStatusBarColorChange(currentTabColorDark, conditionTomorrowColorSet[1]);
                        currentTabColorDark = conditionTomorrowColorSet[1];
                        currentTabColor = conditionTomorrowColorSet[0];
                        gradient = Helper.chooseHeaderColorSet(WeatherActivity.this, conditionTomorrowColorSet);
                        headerContainer.setBackground(gradient);
                        break;
                    case 2:
                        animateColorChange(appBar, currentTabColor, R.color.forecastAppBarColor);
                        animateStatusBarColorChange(currentTabColorDark, R.color.forecastAppBarColorDark);
                        currentTabColorDark = R.color.forecastAppBarColorDark;
                        currentTabColor = R.color.forecastAppBarColor;
                        gradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{Color.parseColor("#006064"), Color.parseColor("#004749")});
                        gradient.setCornerRadius(0f);
                        headerContainer.setBackground(gradient);
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

        recentLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void addToRecentList(String cityName) {
        if (!recentList.contains(cityName)) {
            if (recentList.size() >= 10) {
                recentList.remove(recentList.get(9));
            }
        } else {
            recentList.remove(cityName);
        }
        recentList.add(0, cityName);

        preferences.edit().putString(KEY_RECENT_PLACES, gson.toJson(recentList)).apply();
        recentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRecentSelected(String selectedLocation) {
        drawer.closeDrawer(GravityCompat.START);
        if (selectedLocation.equalsIgnoreCase(currentLocationName))
            return;

        currentLocationName = selectedLocation;
        preferences.edit().putString(Constants.KEY_LOCATION_NAME, currentLocationName).apply();
        addToRecentList(currentLocationName);

        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingView.setVisibility(View.VISIBLE);
                loadingView.bringToFront();
            }

            @Override
            protected Void doInBackground(String... params) {
                startWeatherTask();
                return null;
            }

            @Override
            protected void onPostExecute(Void location) {
                super.onPostExecute(location);
                searchField.setHint(Helper.filterCityName(currentLocationName));
            }
        }.execute(selectedLocation);
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
        } else { //has permission
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                fromLocation = true;
                currentLocationName = String.valueOf(mLastLocation.getLatitude()).concat(" ").concat(String.valueOf(mLastLocation.getLongitude()));
                startWeatherTask();
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
                    fromLocation = true;
                    startWeatherTask();
                }
            } else {
                if (preferences.getString(Constants.KEY_LOCATION_NAME, null) != null) {
                    currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, null);
                    startWeatherTask();
                    searchField.setHint(Helper.filterCityName(currentLocationName));
                } else {
                    Toast.makeText(this, "Permission not granted, please choose a location.", Toast.LENGTH_SHORT).show();
                    viewPager.setVisibility(View.GONE);
                    loadingView.setVisibility(View.GONE);
                    noLocationSelected.setVisibility(View.VISIBLE);
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
        startWeatherTask();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to Google Location Services.", Toast.LENGTH_SHORT).show();
        currentLocationName = preferences.getString(KEY_LOCATION_NAME, null);
        startWeatherTask();
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
        int colorFrom = ContextCompat.getColor(getApplicationContext(), from);
        int colorTo = ContextCompat.getColor(getApplicationContext(), to);
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
        int colorFrom = ContextCompat.getColor(getApplicationContext(), from);
        int colorTo = ContextCompat.getColor(getApplicationContext(), to);
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

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (searchPopupWindow != null && searchPopupWindow.isShowing()) {
            searchPopupWindow.dismiss();
            return;
        }
        super.onBackPressed();
    }
}
