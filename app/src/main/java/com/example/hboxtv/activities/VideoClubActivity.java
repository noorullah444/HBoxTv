package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ProgressBar;

import com.example.hboxtv.R;
import com.example.hboxtv.adapters.CategoryAdapter;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.CategoryByDeviceModel;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoClubActivity extends AppCompatActivity {
    private static final String TAG = LiveTvActivity.class.getSimpleName();
    private ApiInterface apiInterface;
    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_club);
        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();
        // to make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            // for full screen activity
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setClickListeners();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VideoClubActivity.this);
        String UID = preferences.getString("uid", "");
        String GUID = preferences.getString("guid", "");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
                getCategories(UID, GUID);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(VideoClubActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });
    }

    private void getCategories(String uid, String guid) {
        apiInterface = ApiClient.getInstance().getMyApi();

        Log.d(TAG, "getCategories: uid: " + uid);
        Log.d(TAG, "getCategories: guid: " + guid);
        CategoryByDeviceModel model = new CategoryByDeviceModel(uid, guid, "2");
        Gson gson = new Gson();
        String json = gson.toJson(model);
        Log.d(TAG, "getCategories: json: " + json);

        Call<CategoryByDeviceResponse> call = apiInterface.getCategories(model);

        call.enqueue(new Callback<CategoryByDeviceResponse>() {
            @Override
            public void onResponse(Call<CategoryByDeviceResponse> call, Response<CategoryByDeviceResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            populateRecyclerView(response.body());
                            Log.d(TAG, "onResponse: response body: " + response.body().toString());
                            Log.d(TAG, "onResponse: code = " + response.code());
                            Log.d(TAG, "onResponse: message = " + response.message());
                        } else
                            Log.d(TAG, "onResponse: body is null");
                    } else
                        Log.d(TAG, "onResponse: failed!");
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: exception: " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<CategoryByDeviceResponse> call, Throwable t) {
                //handle error or failure cases here
                Log.d(TAG, "onFailure: " + t.toString());
                Log.d(TAG, "onFailure: " + t.getMessage());
                Log.d(TAG, "onFailure: " + t.getCause());
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRecyclerView(CategoryByDeviceResponse categories) {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        recyclerView.setAdapter(adapter);
        container.stopShimmer();
        container.setVisibility(View.GONE);
    }
}