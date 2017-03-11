package com.pavel_bojidar.vineweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter;
import com.pavel_bojidar.vineweather.adapter.FavoritesListAdapter.OnFavouriteSelected;
import com.pavel_bojidar.vineweather.fragment.FragmentDay;
import com.pavel_bojidar.vineweather.fragment.FragmentNow;
import com.pavel_bojidar.vineweather.fragment.FragmentWeek;
import com.pavel_bojidar.vineweather.model.Location;
import com.pavel_bojidar.vineweather.model.Location.Forecast;
import com.pavel_bojidar.vineweather.singleton.AppManager;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements OnFavouriteSelected {

    DrawerLayout drawer;
    TabLayout tabLayout;
    ViewPager viewPager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        //app_bar_navigation_drawer - toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Set a Toolbar to act as the ActionBar for this Activity window
        setSupportActionBar(toolbar);

        //activity_navigation_drawer - drawer layout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //activity_navigation_drawer - navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //initialize favorite location list
        setFavoritesList();
        recyclerView = (RecyclerView) findViewById(R.id.favorite_location_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FavoritesListAdapter(AppManager.getOurInstance().getFavoriteList(), this));

        //initialize the tab layout and vp
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Now"));
        tabLayout.addTab(tabLayout.newTab().setText("24H"));
        tabLayout.addTab(tabLayout.newTab().setText("7Days"));
        viewPager = (ViewPager) findViewById(R.id.pager);

//        if(AppManager.getOurInstance().getCurrentLocation() == null){
//            tabLayout.setVisibility(View.GONE);
//            viewPager.setVisibility(View.GONE);
//            //todo task
//        }

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

            ////when a new page is selected(position is the index of the page)
            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

    private void setFavoritesList() {
        String[] cityNames = {"Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin",
                "Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin",
                "Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin",
                "Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin",
                "Sofia", "Pernik", "Plovdiv", "Varna", "Bourgas", "Ihtiman", "Vidin"};
        List<Location> favoritesList = new ArrayList<>();
        for (int i = 0; i < cityNames.length; i++){
            favoritesList.add(new Location());
            favoritesList.get(i).setLocation(cityNames[i]);
        }
        AppManager.getOurInstance().setFavoriteList(favoritesList);
    }

    //when back button is pressed
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFavouriteSelected(Location selectedLocation) {
        AppManager.getOurInstance().setCurrentLocation(selectedLocation);
        selectedLocation.getForecasts().clear();

        if(selectedLocation.getLocation().equals("Sofia")){
            selectedLocation.getForecasts().add(new Forecast(15));
        } else if (selectedLocation.getLocation().equals("Plovdiv")){
            selectedLocation.getForecasts().add(new Forecast(10));
        } else {
            selectedLocation.getForecasts().add(new Forecast(5));
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BroadcastActions.ACTION_LOCATION_CHANGED));
        drawer.closeDrawer(GravityCompat.START);
    }
}
