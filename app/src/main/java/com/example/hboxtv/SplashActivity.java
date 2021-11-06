package com.example.hboxtv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.activities.HomeActivity;
import com.example.hboxtv.activities.SignInActivity;

public class SplashActivity extends AppCompatActivity {
    public static boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // get value from shared prefs
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                isLogin = preferences.getBoolean("isLogin", false);

                if (isLogin) {
                    // if user is already login in
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                }
                finish();
            }
        }, 1000);
    }

}