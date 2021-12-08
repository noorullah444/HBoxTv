package com.example.hboxtv.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.MyApplication;
import com.example.hboxtv.R;
import com.example.hboxtv.model.Category;
import com.sun.mail.imap.protocol.UID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String JSON_URL = "http://54.36.204.161/iptvapi/objects/categorybydevice.php";
    private ExecutorService executor;
    private ProgressDialog progressDialog;
    private String UID;
    private String GUID;

    public static List<Category> liveTVList, videoClubList, tvShowsList, replayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initLists();
//        initProgressDialog();
//        loadingCatalogue();
        setDateAndTime();
        setExpiryDate();
        setClickListeners();
    }

    private void loadingCatalogue() {
        progressDialog.show();
        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");

        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getCategoriesByVolley("1", liveTVList);
            }
        });

        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getCategoriesByVolley("2", videoClubList);
            }
        });

        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getCategoriesByVolley("3", tvShowsList);
            }
        });

        /*executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getCategoriesByVolley("4", replayList);
            }
        });*/
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Catalogue...");
        progressDialog.setCancelable(false);
    }

    private void setDateAndTime() {
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvDate = findViewById(R.id.tv_date);
        tvTime.setText(getFormattedTime());
        tvDate.setText(getFormattedDate());
    }

    public static CharSequence getFormattedDate() {
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String time = df.format(new Date());
        Log.d(TAG, "getFormattedTime: " + time);
        return time;
    }

    public static CharSequence getFormattedTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = df.format(new Date());
        Log.d(TAG, "getFormattedTime: " + time);
        return time;
    }

    private void setExpiryDate() {
        TextView tvExpiryDate = findViewById(R.id.tv_expiry_date);

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String expiryDate = preferences.getString("expiry_date", "");
        if (expiryDate != null)
            tvExpiryDate.setText("Expiration: " + expiryDate);
    }

    private void initLists() {
        liveTVList = new ArrayList<>();
        videoClubList = new ArrayList<>();
        tvShowsList = new ArrayList<>();
        replayList = new ArrayList<>();
    }

    private void setClickListeners() {
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                logOutUser();
            }
        });

        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.card_live_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));

                if (MyApplication.isNetworkNotAvailable(HomeActivity.this)) {
                    MyApplication.networkDialog(HomeActivity.this,
                            "No internet available. Please connect to the internet first.");
                    return;
                }
                startActivity(new Intent(HomeActivity.this, LiveTvActivity.class));
            }
        });

        findViewById(R.id.card_video_club).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));

                if (MyApplication.isNetworkNotAvailable(HomeActivity.this)) {
                    MyApplication.networkDialog(HomeActivity.this,
                            "No internet available. Please connect to the internet first.");
                    return;
                }
                startActivity(new Intent(HomeActivity.this, VideoClubActivity.class));
            }
        });

        findViewById(R.id.card_tv_shows).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));

                if (MyApplication.isNetworkNotAvailable(HomeActivity.this)) {
                    MyApplication.networkDialog(HomeActivity.this,
                            "No internet available. Please connect to the internet first.");
                    return;
                }
                startActivity(new Intent(HomeActivity.this, TvShowsActivity.class));
            }
        });

        findViewById(R.id.card_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));

                if (MyApplication.isNetworkNotAvailable(HomeActivity.this)) {
                    MyApplication.networkDialog(HomeActivity.this,
                            "No internet available. Please connect to the internet first.");
                    return;
                }
                startActivity(new Intent(HomeActivity.this, ReplayActivity.class));
            }
        });
    }

    private void logOutUser() {
        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        boolean isLogin = preferences.getBoolean("isLogin", false);
        if (isLogin) {
            editor.putBoolean("isLogin", false);
            editor.apply();
            showLogOutDialog();
        }
    }

    private void showLogOutDialog() {
        new AlertDialog.Builder(HomeActivity.this).setTitle("Logout")
                .setMessage("Are you sure to logout?")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                        finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void getCategoriesByVolley(String catId, List<Category> catList) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("category_type", catId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (catId.equals("1")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                            }
                        });
                    }

                    if (response != null) {
                        Log.d(TAG, "live onResponse: " + response.toString());
                        try {
                            //getting the whole json object from the response
                            JSONArray heroArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject heroObject = heroArray.getJSONObject(i);

                                Category category = new Category(heroObject.getString("category_id"), heroObject.getString("category_name"));

                                //adding the list
                                catList.add(category);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    });
                    Log.d(TAG, "live onErrorResponse: " + error.toString());
                }
            });

            jsonObject.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //adding the string request to request queue
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}