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

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private String UID;
    private String GUID;
    private String channelId;
    private ImageView movieCover;
    private TextView tvMovieName;
    private TextView tvGenre;
    private TextView tvCasting;
    private TextView tvDescription;
    private TextView tvDuration;
    private Button btnPlay;
    private RatingBar ratingBar;

    private String movieName;
    private String cover;
    private String genre;
    private String casting;
    private String description;
    private String rating;
    private String duration;
    private String STREAM_URL;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            // for full screen activity
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        initViews();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MovieDetailsActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");
        String server = preferences.getString("server", "");
        String userName = preferences.getString("user_name", "");
        String password = preferences.getString("password", "");

        Intent intent = getIntent();
        if (intent != null) {
            channelId = intent.getStringExtra("channel_id");
            String streamType = intent.getStringExtra("stream_type");
            String extension = intent.getStringExtra("extension");

            if (userName != null && password != null) {
                STREAM_URL = server + "/" + streamType + "/" + userName + "/" + password + "/" + channelId + "." + extension;
                Log.d(TAG, "OnChannelClick: url: " + STREAM_URL);
//                initializePlayer();
            }
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getMoviesDetails(channelId);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });

        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MovieDetailsActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(MovieDetailsActivity.this, R.anim.button_click));
                if (STREAM_URL != null) {
                    Intent gotoPlayer = new Intent(MovieDetailsActivity.this, MxPlayerActivity.class);
                    gotoPlayer.putExtra("stream_url", STREAM_URL);
                    gotoPlayer.putExtra("movie_name", movieName);
                    startActivity(gotoPlayer);
                }
            }
        });
    }

    private void initViews() {
        movieCover = findViewById(R.id.iv_movie_cover);
        tvMovieName = findViewById(R.id.tv_movie_name);
        tvGenre = findViewById(R.id.tv_genre);
        tvCasting = findViewById(R.id.tv_casting);
        tvDescription = findViewById(R.id.tv_description);
        tvDuration = findViewById(R.id.tv_duration);
        btnPlay = findViewById(R.id.btn_play_movie);
        ratingBar = findViewById(R.id.ratingBar);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void getMoviesDetails(String channelId) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/voddetailsbychannelid.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("channelid", channelId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "kaka: " + response.toString());
                            //getting the whole json object from the response
                            JSONArray jsonArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                movieName = jsonObject1.getString("name");
                                cover = jsonObject1.getString("movie_image");
                                genre = jsonObject1.getString("genre");
                                casting = jsonObject1.getString("cast");
                                description = jsonObject1.getString("description");
                                rating = jsonObject1.getString("rating");
                                duration = jsonObject1.getString("duration");
                            }
                            setData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.toString());
                    progressBar.setVisibility(View.GONE);
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

    private void setData() {
        tvMovieName.setText(movieName);
        tvGenre.setText(genre);
        tvCasting.setText(casting);
        tvDescription.setText(description);
        tvDuration.setText(duration+" hrs");
        ratingBar.setRating(Float.parseFloat(rating));

        Glide.with(MovieDetailsActivity.this)
                .load(cover)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(movieCover);
    }
}