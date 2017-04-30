package com.pavel_bojidar.vineweather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.pavel_bojidar.vineweather.popupwindow.CitySearchPopupWindow;
import com.pavel_bojidar.vineweather.receiver.NetworkReceiver;
import com.pavel_bojidar.vineweather.receiver.NetworkReceiver.ConnectivityChanged;
import com.pavel_bojidar.vineweather.task.GetLocations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.pavel_bojidar.vineweather.Constants.INTERNET_CONNECTION;
import static com.pavel_bojidar.vineweather.Constants.KEY_NAME;
import static com.pavel_bojidar.vineweather.Constants.SERVER_CONNECTION_FAILURE;

/**
 * Created by Pavel Pavlov on 4/30/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected MenuItem searchMenu;
    protected Toolbar toolbar;
    protected Timer inputDelay;
    protected EditText searchField;
    protected CitySearchPopupWindow searchPopupWindow;
    protected AlertDialog alertDialog;

    protected NetworkReceiver networkReceiver = new NetworkReceiver(new ConnectivityChanged() {
        @Override
        public void onConnected() {
            if (alertDialog != null) {
                alertDialog.hide();
            }
            LocalBroadcastManager.getInstance(BaseActivity.this).unregisterReceiver(networkReceiver);
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        initViews();
    }

    protected abstract int getContentView();

    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchField = (EditText) findViewById(R.id.search_field);
    }

    private void initSearchField() {
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchField.hasFocus()) {
                    searchMenu.setIcon(R.drawable.ic_close_black_24dp);
                }
            }
        });
        searchField.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchMenu.setIcon(R.drawable.ic_close_black_24dp);
                } else {
                    searchField.clearFocus();
                    searchMenu.setIcon(R.drawable.ic_search_black_24dp);
                }
            }
        });
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
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
                    if (searchPopupWindow != null && searchPopupWindow.isShowing()) {
                        searchPopupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchMenu = menu.add("Search");
        if (!searchField.hasFocus()) {
            searchMenu.setIcon(R.drawable.ic_search_black_24dp);
        } else {
            searchMenu.setIcon(R.drawable.ic_close_black_24dp);
        }
        searchMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!searchField.hasFocus()) {
                    searchField.requestFocus();
                    searchMenu.setIcon(R.drawable.ic_close_black_24dp);
                    showKeyboard(searchField);
                } else {
                    if (searchField.getText().length() > 0) {
                        searchField.setText(null);
                    } else {
                        searchMenu.setIcon(R.drawable.ic_search_black_24dp);
                        searchField.clearFocus();
                        onSearchCanceled();
                        hideKeyboard();
                    }
                }
                return true;
            }
        });
        initSearchField();
        return super.onCreateOptionsMenu(menu);
    }

    protected void onSearchCanceled() {
    }

    protected void performSearch(Editable s) {
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
                            searchPopupWindow = new CitySearchPopupWindow(BaseActivity.this);
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
                            searchPopupWindow.getListView().setOnItemClickListener(getSearchPopupListener());
                        }

                    } catch (JSONException e) {

                    }
                }
            }.execute(s.toString());
        } else {
            showAlertDialog(INTERNET_CONNECTION);
        }
    }

    abstract public OnItemClickListener getSearchPopupListener();

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void showKeyboard(View focusedView) {
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showAlertDialog(String event) {
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
}
