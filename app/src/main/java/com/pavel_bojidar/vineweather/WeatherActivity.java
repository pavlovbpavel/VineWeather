package com.pavel_bojidar.vineweather;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
import android.support.v4.content.LocalBroadcastManager;
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
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.PopupWindow;
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
import com.pavel_bojidar.vineweather.popupwindow.CitySearchPopupWindow;
import com.pavel_bojidar.vineweather.receiver.NetworkReceiver;
import com.pavel_bojidar.vineweather.receiver.NetworkReceiver.ConnectivityChanged;
import com.pavel_bojidar.vineweather.singleton.AppManager;
import com.pavel_bojidar.vineweather.task.GetCurrentWeather;
import com.pavel_bojidar.vineweather.task.GetForecast;
import com.pavel_bojidar.vineweather.task.GetLocations;
import com.pavel_bojidar.vineweather.widget.MaPaWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static com.pavel_bojidar.vineweather.Constants.INTERNET_CONNECTION;
import static com.pavel_bojidar.vineweather.Constants.KEY_LOCATION_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;
import static com.pavel_bojidar.vineweather.Constants.KEY_RECENT_PLACES;
import static com.pavel_bojidar.vineweather.Constants.SERVER_CONNECTION_FAILURE;

public class WeatherActivity extends AppCompatActivity implements RecentSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout drawer;
    private AppBarLayout appBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    protected RecyclerView recentLocationRecyclerView;
    private SharedPreferences preferences;
    private String currentLocationName;
    private ProgressBar loadingView;
    private EditText searchField;
    private RelativeLayout headerContainer;
    final boolean[] weatherTasksCompleted = {false, false};
    private CitySearchPopupWindow searchPopupWindow;
    private Toolbar toolbar;
    private ArrayList<String> recentList = new ArrayList<>();
    private MenuItem search;
    private Timer inputDelay;
    private ImageView navDrawerImage;
    private TextView navDrawerDegree, navDrawerCondition, noLocationSelected, navDrawerLocation;
    private Button celsiusButton, fahrenheitButton;
    private AlertDialog alertDialog;
    public static boolean isImperialUnits;
    private int currentTabColor = R.color.todayAppBarColor;
    private int currentTabColorDark = R.color.todayAppBarColorDark;
    private GoogleApiClient mGoogleApiClient;
    private SwipeRefreshLayout swipeRefresh;
    public static String widgetLocation;
    protected boolean fromLocation, isDay;
    private RecentListAdapter recentAdapter;
    private Gson gson = new Gson();
    public static boolean isConnected = true;
    protected String conditionToday, conditionTomorrow;
    int[] conditionTodayColorSet, conditionTomorrowColorSet;
    private GradientDrawable gradient;

    private NetworkReceiver networkReceiver = new NetworkReceiver(new ConnectivityChanged() {
        @Override
        public void onConnected() {
            if (alertDialog != null) {
                alertDialog.hide();
            }
            startWeatherTasks();
            LocalBroadcastManager.getInstance(WeatherActivity.this).unregisterReceiver(networkReceiver);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        isImperialUnits = preferences.getBoolean(Constants.UNITS_IMPERIAL, false); // default metric

        Gson gson = new Gson();
        String json = preferences.getString(KEY_RECENT_PLACES, null);
        if (json != null) {
            recentList = gson.fromJson(json, ArrayList.class);
        }

        initViews();

        buildGoogleApiClient();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentLocationName == null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        preferences.edit().putBoolean(Constants.UNITS_IMPERIAL, isImperialUnits).apply();
        requestWidgetUpdate();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkReceiver);
    }

    private void requestWidgetUpdate() {
        Intent intent = new Intent(this, MaPaWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, MaPaWidgetProvider.allWidgetIds);
        sendBroadcast(intent);
    }

    private void startWeatherTasks() {
        if (isNetworkAvailable()) {
            if (noLocationSelected != null) {
                noLocationSelected.setVisibility(View.GONE);
            }
            loadingView.setVisibility(View.VISIBLE);
            new GetCurrentWeather() {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    weatherTasksCompleted[0] = true;
                    onLocationUpdated();
                }
            }.execute(currentLocationName);

            new GetForecast() {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    weatherTasksCompleted[1] = true;
                    onLocationUpdated();
                }
            }.execute(currentLocationName);
        } else {
            showAlertDialog(INTERNET_CONNECTION);
        }
    }

    public void onLocationUpdated() {
        if (!weatherTasksCompleted[0] || !weatherTasksCompleted[1]) {
            return;
        }
        if (!isConnected) {
            showAlertDialog(SERVER_CONNECTION_FAILURE);
            return;
        }
        weatherTasksCompleted[0] = false;
        weatherTasksCompleted[1] = false;
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();

        isDay = AppManager.getInstance().getCurrentLocation().getCurrentWeather().getIs_day() == 1;
        conditionToday = AppManager.getInstance().getCurrentLocation().getCurrentWeather().getCondition().getText();
        conditionTomorrow = AppManager.getInstance().getCurrentLocation().getForecast().getDayForecasts().get(1).getDay().getCondition().getText();
        conditionTodayColorSet = Helper.chooseConditionColorSet(conditionToday, isDay);
        conditionTomorrowColorSet = Helper.chooseConditionColorSet(conditionTomorrow, true);

        if (viewPager.getCurrentItem() == 0) {
            if (conditionTodayColorSet != null) {
                animateColorChange(appBar, currentTabColor, conditionTodayColorSet[0]);
                animateStatusBarColorChange(currentTabColorDark, conditionTodayColorSet[1]);
                currentTabColorDark = conditionTodayColorSet[1];
                currentTabColor = conditionTodayColorSet[0];
                gradient = Helper.chooseHeaderColorSet(this, conditionTodayColorSet);
                headerContainer.setBackgroundDrawable(gradient);
            }
        } else if (viewPager.getCurrentItem() == 1) {
            if (conditionTomorrowColorSet != null) {
                animateColorChange(appBar, currentTabColor, conditionTomorrowColorSet[0]);
                animateStatusBarColorChange(currentTabColorDark, conditionTomorrowColorSet[1]);
                currentTabColorDark = conditionTomorrowColorSet[1];
                currentTabColor = conditionTomorrowColorSet[0];
                gradient = Helper.chooseHeaderColorSet(this, conditionTomorrowColorSet);
                headerContainer.setBackgroundDrawable(gradient);
            }
        }

        swipeRefresh.setColorSchemeResources((currentTabColor));

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

        if (fromLocation) {
            fromLocation = false;
            currentLocationName = AppManager.getInstance().getCurrentLocation().getName();
            searchField.setHint(currentLocationName);
            widgetLocation = currentLocationName;
            addToRecentList(currentLocationName);
        }
        navDrawerLocation.setText(Helper.filterCityName(currentLocationName));

        if (currentWeather.getCondition().getText().length() >= 25 && currentWeather.getCondition().getText().length() < 30) {
            navDrawerCondition.setTextSize(14);
        } else if (currentWeather.getCondition().getText().length() >= 35) {
            navDrawerCondition.setTextSize(10);
        } else {
            navDrawerCondition.setTextSize(16);
        }

        if (Helper.filterCityName(currentLocationName).length() >= 14 && Helper.filterCityName(currentLocationName).length() <= 18) {
            navDrawerLocation.setTextSize(18);
        } else if (Helper.filterCityName(currentLocationName).length() > 18) {
            navDrawerLocation.setTextSize(14);
        } else {
            navDrawerLocation.setTextSize(24);
        }

        swipeRefresh.setRefreshing(false);
        MaPaWidgetProvider.startService(this);
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
        requestWidgetUpdate();
    }

    public void showAlertDialog(String event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (event.equals(INTERNET_CONNECTION)) {
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
            LocalBroadcastManager.getInstance(this).registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (event.equals(SERVER_CONNECTION_FAILURE)) {
            builder.setTitle("Error");
            builder.setMessage("Sorry, something went wrong. A team of highly trained monkeys has been dispatched to deal with this situation");
            builder.setCancelable(false);
            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                preferences.edit().putBoolean(Constants.UNITS_IMPERIAL, false).apply();
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
                preferences.edit().putBoolean(Constants.UNITS_IMPERIAL, true).apply();
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
        navDrawerLocation = (TextView) hView.findViewById(R.id.nav_drawer_location);
        headerContainer = (RelativeLayout) hView.findViewById(R.id.header_container_layout);


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
                        animateColorChange(appBar, currentTabColor, conditionTodayColorSet[0]);
                        animateStatusBarColorChange(currentTabColorDark, conditionTodayColorSet[1]);
                        currentTabColorDark = conditionTodayColorSet[1];
                        currentTabColor = conditionTodayColorSet[0];
                        gradient = Helper.chooseHeaderColorSet(WeatherActivity.this, conditionTodayColorSet);
                        headerContainer.setBackgroundDrawable(gradient);
                        break;
                    case 1:
                        animateColorChange(appBar, currentTabColor, conditionTomorrowColorSet[0]);
                        animateStatusBarColorChange(currentTabColorDark, conditionTomorrowColorSet[1]);
                        currentTabColorDark = conditionTomorrowColorSet[1];
                        currentTabColor = conditionTomorrowColorSet[0];
                        gradient = Helper.chooseHeaderColorSet(WeatherActivity.this, conditionTomorrowColorSet);
                        headerContainer.setBackgroundDrawable(gradient);
                        break;
                    case 2:
                        animateColorChange(appBar, currentTabColor, R.color.forecastAppBarColor);
                        animateStatusBarColorChange(currentTabColorDark, R.color.forecastAppBarColorDark);
                        currentTabColorDark = R.color.forecastAppBarColorDark;
                        currentTabColor = R.color.forecastAppBarColor;
                        gradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{Color.parseColor("#006064"), Color.parseColor("#004749")});
                        gradient.setCornerRadius(0f);
                        headerContainer.setBackgroundDrawable(gradient);
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
        recentAdapter = new RecentListAdapter(recentList, this);
        recentLocationRecyclerView.setAdapter(recentAdapter);
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
                            searchPopupWindow.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
                            searchPopupWindow.setForceIgnoreOutsideTouch(true);
                        }
                        searchPopupWindow.updateSuggestionsList(cityNames);
                        if (!cityNames.isEmpty()) {

                            searchPopupWindow.show();
                        } else {
                            searchPopupWindow.dismiss();
                        }
                        if (searchPopupWindow.isShowing()) {
                            searchPopupWindow.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                            showKeyboard();
                            searchPopupWindow.getListView().setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String chosenCity = (String) searchPopupWindow.getListView().getItemAtPosition(position);
                                    searchPopupWindow.dismiss();
                                    currentLocationName = chosenCity;
                                    addToRecentList(currentLocationName);
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
                        showAlertDialog(SERVER_CONNECTION_FAILURE);
                    }
                }
            }.execute(s.toString());
        } else {
            showAlertDialog(INTERNET_CONNECTION);
        }
    }

    private void addToRecentList(String cityName) {
        if (!recentList.contains(cityName)) {
            if (recentList.size() >= 10) {
                recentList.remove(recentList.get(9));
                recentList.add(0, cityName);
            } else {
                recentList.add(0, cityName);
            }
        } else {
            recentList.remove(cityName);
            recentList.add(0, cityName);
        }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                    fromLocation = true;
                    startWeatherTasks();
                }
            } else {
                if (preferences.getString(Constants.KEY_LOCATION_NAME, null) != null) {
                    currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, null);
                    startWeatherTasks();
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

    public void showKeyboard() {
        showKeyboard(getCurrentFocus());
    }

    public void showKeyboard(View focusedView) {
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
