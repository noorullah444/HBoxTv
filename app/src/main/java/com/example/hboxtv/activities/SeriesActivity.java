package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.R;
import com.example.hboxtv.adapters.CategoryAdapter;
import com.example.hboxtv.adapters.SeriesAdapter;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.example.hboxtv.model.Series;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeriesActivity extends AppCompatActivity implements SeriesAdapter.OnSeriesClickListener {
    private static final String TAG = SeriesActivity.class.getSimpleName();
    private String UID;
    private String GUID;
    private String categoryId;
    List<Series> seriesList = new ArrayList<>();
    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            // for full screen activity
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setOnClickListeners();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SeriesActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");

        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra("category_id");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getSeriesList(categoryId);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });

    }

    private void setOnClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SeriesActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });
    }

    private void getSeriesList(String categoryId) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/seriesbycategory.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);
            jsonBody.put("categoryID", categoryId);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "123onResponse: " + response.toString());

                    if (response != null) {
                        try {
                            //getting the whole json object from the response
                            JSONArray jsonArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                Series series = new Series(jsonObject1.getString("name"), jsonObject1.getString("series_id"), jsonObject1.getString("cover"));

                                //adding the list
                                seriesList.add(series);
                            }
                            populateRecyclerView(seriesList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

    private void populateRecyclerView(List<Series> seriesList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        SeriesAdapter adapter = new SeriesAdapter(this, seriesList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                container.stopShimmer();
                container.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void OnSeriesClick(String seriesId) {
//        Toast.makeText(this, "Series Id: " + seriesId, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SeriesActivity.this, SeriesDetailsActivity.class);
        intent.putExtra("series_id", seriesId);
        startActivity(intent);
    }
}