package com.example.hboxtv.activities;

import static com.example.hboxtv.activities.HomeActivity.getFormattedDate;
import static com.example.hboxtv.activities.HomeActivity.getFormattedTime;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.BuildConfig;
import com.example.hboxtv.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // for full screen activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setDateAndTime();
        setClickListeners();
    }

    private void setDateAndTime() {
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvDate = findViewById(R.id.tv_date);
        tvTime.setText(getFormattedTime());
        tvDate.setText(getFormattedDate());
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        findViewById(R.id.card_subscription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                startActivity(new Intent(SettingsActivity.this, SubscriptionActivity.class));
            }
        });

        findViewById(R.id.card_package).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                startActivity(new Intent(SettingsActivity.this, PackageSelectionActivity.class));
            }
        });

        findViewById(R.id.card_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                startActivity(new Intent(SettingsActivity.this, ContactUsActivity.class));
            }
        });

        findViewById(R.id.card_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }
}