package com.pavel_bojidar.vineweather;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
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
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

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

import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;


public class WeatherActivity extends AppCompatActivity implements RecentSelectedListener {

    DrawerLayout drawer;
    AppBarLayout appBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    RecyclerView recentLocationRecyclerView;
    SharedPreferences preferences;
    String currentLocationName;
    ProgressBar loadingView;
    EditText searchField;
    final boolean[] weatherTasksCompleted = {false, false};
    CitySearchPopupWindow searchPopupWindow;
    Toolbar toolbar;
    ArrayList<String> recentList = new ArrayList<>();
    MenuItem search;
    Timer inputDelay;
    ImageView navDrawerImage;
    TextView navDrawerDegree, navDrawerCondition;
    Switch navDrawerSwitch;
    BroadcastReceiver br;
    AlertDialog alertDialog;
    ArrayList<SearchCity> searchCities = new ArrayList<>();
    public static boolean isCelsius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        preferences.edit().putString(Constants.KEY_LOCATION_NAME, "Sofia, Grad Sofiya, Bulgaria").apply();
        currentLocationName = preferences.getString(Constants.KEY_LOCATION_NAME, "Sofia, Grad Sofiya, Bulgaria");

        initViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentLocationName == null) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            setTitle(null);
            searchField.setHint("Select a city");
            //todo request location from user
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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
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
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

        CurrentWeather currentWeather = AppManager.getInstance().getCurrentLocation().getCurrentWeather();
        navDrawerImage.setImageDrawable(Helper.chooseWeatherConditionIcon(this ,currentWeather.getIs_day() == 1,
                currentWeather.getCondition().getIcon()));
        navDrawerCondition.setText(currentWeather.getCondition().getText());
        navDrawerDegree.setText(Helper.decimalFormat(currentWeather.getTempC()).concat(Constants.CELSIUS_SYMBOL));

        setTitle(AppManager.getInstance().getCurrentLocation().getName());
        if (viewPager.getAdapter() == null) {
            setViewPagerAdapter();
        }

        //todo set updated location as current in shared preferences

        Log.e("task completed", "on location updated");
        AppManager.getInstance().onLocationUpdated(this);
    }

    private void initViews() {
        //init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //init view from header navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        navDrawerImage = (ImageView) hView.findViewById(R.id.nav_drawer_image);
        navDrawerDegree = (TextView) hView.findViewById(R.id.nav_drawer_degree);
        navDrawerCondition = (TextView) hView.findViewById(R.id.nav_drawer_condition);

        setSupportActionBar(toolbar);

        //init search field and call performSearch logic
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

        //init navigation drawer and set toggle
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

        //init progress bar
        loadingView = (ProgressBar) findViewById(R.id.loading_view);

        //init recent locations recyclerView
        recentLocationRecyclerView = (RecyclerView) findViewById(R.id.favorite_location_list);

        //init tabLayout and viewPager
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));
        tabLayout.addTab(tabLayout.newTab().setText("10 Days"));
        viewPager = (ViewPager) findViewById(R.id.pager);

        appBar = (AppBarLayout) findViewById(R.id.app_bar);

        //todo change switch to set imperial/metric units
        navDrawerSwitch = (Switch) findViewById(R.id.nav_drawer_switch);
        navDrawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCelsius = isChecked;
            }
        });

        //connect tabLayout with viewPager
        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()){
                    case 0:
//                        animateColorChange(appBar, R.color.tomorrowAppBarColor, R.color.todayAppBarColor);
                        appBar.setBackgroundResource(R.color.todayAppBarColor);
                        changeStatusBarColor(R.color.todayAppBarColorDark);
                        break;
                    case 1:
//                        animateColorChange(appBar, R.color.todayAppBarColor, R.color.tomorrowAppBarColor);
                        appBar.setBackgroundResource(R.color.tomorrowAppBarColor);
                        changeStatusBarColor(R.color.tomorrowAppBarColorDark);
                        break;
                    case 2:
//                        animateColorChange(appBar, R.color.tomorrowAppBarColor, R.color.forecastAppBarColor);
                        appBar.setBackgroundResource(R.color.forecastAppBarColor);
                        changeStatusBarColor(R.color.forecastAppBarColorDark);
                        break;
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        //connect viewPager with tabLayout
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


    //set an adapter for the viewPager
    private void setViewPagerAdapter() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        });
    }

    //implement search logic
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
                        //put all city names in an ArrayList and feed it to the SearchPopupWindow Adapter
                        for (int i = 0; i < callback.length(); i++) {
                            JSONObject currentItem = callback.getJSONObject(i);
                            //todo format string utf-8
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
                            searchPopupWindow.getListView().setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String chosenCity = (String) searchPopupWindow.getListView().getItemAtPosition(position);
                                    searchPopupWindow.dismiss();
                                    addToRecentList(currentLocationName);
                                    currentLocationName = chosenCity;
                                    //todo put the new current location in sharedpreferences
                                    hideKeyboard();
                                    searchField.setText(null);
                                    searchField.setHint(Helper.filterCityName(chosenCity));
                                    getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                    search.setIcon(R.drawable.ic_search_black_24dp);
                                    viewPager.setCurrentItem(0, true);
                                    startWeatherTasks();
                                    Log.e("task", "on Search");
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

    //add to recent list
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

    //if an item is already in the list, but needs to be reordered
    private void reorderRecentList(String selectedLocationName) {
        recentList.remove(selectedLocationName);
        recentLocationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recentList != null) {
            recentLocationRecyclerView.setAdapter(new RecentListAdapter(recentList, this));
        }
    }

    //when an item from recent list is clicked
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
                        searchField.setHint(Helper.filterCityName(currentLocationName));
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

    private void changeStatusBarColor(int resID){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(resID));
        }
    }

    private void animateColorChange(final View view, int from, int to){
        int colorFrom = getResources().getColor(from);
        int colorTo = getResources().getColor(to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300); // milliseconds
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}
