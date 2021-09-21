package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.hboxtv.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setClickListeners();
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
                Toast.makeText(SettingsActivity.this, "Subscription Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_package).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                Toast.makeText(SettingsActivity.this, "Packages Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                Toast.makeText(SettingsActivity.this, "Contact Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.button_click));
                Toast.makeText(SettingsActivity.this, "Share Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}