package com.example.hboxtv.activities;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.hboxtv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeriesDetailsActivity extends AppCompatActivity {
    private static final String TAG = SeriesDetailsActivity.class.getSimpleName();
    private String UID;
    private String GUID;
    private String seriesId;
    private ProgressBar progressBar;

    private TextView tvTitle;
    private TextView tvGenre;
    private TextView tvCasting;
    private TextView tvPlot;
    private ImageView ivCover;
    private RatingBar ratingBar;
    private Button btnEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_details);
        //            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        // for full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        setClickListeners();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SeriesDetailsActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");

        Intent intent = getIntent();
        if (intent != null) {
            seriesId = intent.getStringExtra("series_id");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getSeriesDetails(seriesId);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(SeriesDetailsActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        findViewById(R.id.button_episodes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        tvTitle = findViewById(R.id.tv_title);
        tvGenre = findViewById(R.id.tv_genre);
        tvCasting = findViewById(R.id.tv_casting);
        tvPlot = findViewById(R.id.tv_plot);
        ivCover = findViewById(R.id.iv_series_cover);
        ratingBar = findViewById(R.id.rating_bar_series);
        btnEpisodes = findViewById(R.id.button_episodes);
    }

    private void getSeriesDetails(String seriesId) {
        progressBar.setVisibility(View.VISIBLE);
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/seriedetailsbyserieid.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("serieid", seriesId);

            Log.d(TAG, "getSeriesDetails: uid: " + UID);
            Log.d(TAG, "getSeriesDetails: guid: " + GUID);
            Log.d(TAG, "getSeriesDetails: seriesId: "+ seriesId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "123onResponse: " + response.toString());
                        try {
                            //getting the whole json object from the response
                            JSONArray jsonArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String name = jsonObject1.getString("name");
                                String genre = jsonObject1.getString("genre");
                                String casting = jsonObject1.getString("cast");
                                String plot = jsonObject1.getString("plot");
                                String rating = jsonObject1.getString("rating_5based");
                                String cover = jsonObject1.getString("cover");

                                Log.d(TAG, "onResponse1: name: " + name);
                                Log.d(TAG, "onResponse1: genre: " + genre);
                                Log.d(TAG, "onResponse1: casting: " + casting);
                                Log.d(TAG, "onResponse1: plot: " + plot);
                                Log.d(TAG, "onResponse1: rating: " + rating);
                                Log.d(TAG, "onResponse1: cover: " + cover);

                                populateViews(name, genre, casting, plot, rating, cover);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: " + error.toString());
//                    Toast.makeText(LiveTvActivity.this, "Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
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

    private void populateViews(String name, String genre, String casting, String plot, String rating, String cover) {
        tvTitle.setText(name);
        tvGenre.setText(genre);
        tvCasting.setText(casting);
        tvPlot.setText(plot);
        ratingBar.setRating(Float.parseFloat(rating));
        Glide.with(this)
                .load(cover)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivCover);
    }
}