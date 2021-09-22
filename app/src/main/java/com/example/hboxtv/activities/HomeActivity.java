package com.example.hboxtv.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.R;
import com.example.hboxtv.SplashActivity;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceModel;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    public static List<Category> liveTVList, videoClubList, tvShowsList, replayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLists();
        // to make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            // for full screen activity
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setExpiryDate();
        setClickListeners();
    }

    private void setExpiryDate() {
        TextView tvExpiryDate = findViewById(R.id.tv_expiry_date);

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String expiryDate = preferences.getString("expiry_date", "");
        if (expiryDate != null)
            tvExpiryDate.setText("Expiration: "+ expiryDate);
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
                startActivity(new Intent(HomeActivity.this, LiveTvActivity.class));
            }
        });

        findViewById(R.id.card_video_club).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                startActivity(new Intent(HomeActivity.this, VideoClubActivity.class));
            }
        });

        findViewById(R.id.card_tv_shows).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                startActivity(new Intent(HomeActivity.this, TvShowsActivity.class));
            }
        });

        findViewById(R.id.card_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
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
}