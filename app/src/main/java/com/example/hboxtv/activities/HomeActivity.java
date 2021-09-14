package com.example.hboxtv.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiInterface;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // to make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }

        setClickListeners();

//        getHeroes();
    }

    private void setClickListeners() {
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "Logout Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "Settings Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_live_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "Live TV Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_tv_shows).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "TV Shows Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_video_club).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "Video Club Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_click));
                Toast.makeText(HomeActivity.this, "Replay Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void getHeroes() {
        //Creating a call instance using our RetrofitClient
        //here basically we are calling the method getHeroes() that we created inside
        //our API Interface
        apiInterface = ApiClient.getInstance().getMyApi();
        Call<List<Hero>> call = apiInterface.getHeroes();

        //to perform the API call we need to call the method enqueue()
        //We need to pass a Callback with enqueue method
        //And Inside the callback functions we will handle success or failure of
        //the result that we got after the API call
        call.enqueue(new Callback<List<Hero>>() {
            @Override
            public void onResponse(Call<List<Hero>> call, Response<List<Hero>> response) {

                //In this point we got our hero list
                //thats damn easy right ;)
                if (response.body() != null) {
                    List<Hero> heroList = response.body();
                    Log.d(TAG, "onResponse: response= " + heroList.toString());
                }

                //now we can do whatever we want with this list


            }

            @Override
            public void onFailure(Call<List<Hero>> call, Throwable t) {
                //handle error or failure cases here
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}