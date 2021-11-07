package com.example.hboxtv.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.R;
import com.example.hboxtv.adapters.CategoryAdapter;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceModel;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplayActivity extends AppCompatActivity {
    private static final String TAG = LiveTvActivity.class.getSimpleName();
    private static final String JSON_URL = "http://54.36.204.161/iptvapi/objects/categorybydevice.php";
    private ApiInterface apiInterface;
    ShimmerFrameLayout container;
//    List<Category> categoryList = new ArrayList<>();
    CategoryByDeviceResponse categoryByDeviceResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();
        categoryByDeviceResponse = new CategoryByDeviceResponse();

        // to make status bar transparent
        //            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        // for full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setClickListeners();

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReplayActivity.this);
        String UID = preferences.getString("uid", "");
        String GUID = preferences.getString("guid", "");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (UID != null && GUID != null) {
//                getCategories(UID, GUID);
                if (HomeActivity.replayList != null && HomeActivity.replayList.size() != 0) {
                    categoryByDeviceResponse.setResponse(HomeActivity.replayList);
                    populateRecyclerView(categoryByDeviceResponse);
                } else
                    getCategoriesByVolley(UID, GUID);
            }
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }

    private void getCategoriesByVolley(String uid, String guid) {

        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", uid);
            jsonBody.put("customerguid", guid);
            jsonBody.put("category_type", "4");

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Log.d(TAG, "onResponse: " + response.toString());

                        try {
                            //getting the whole json object from the response
                            JSONArray heroArray = response.getJSONArray("response");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject heroObject = heroArray.getJSONObject(i);

                                Category category = new Category(heroObject.getString("category_id"), heroObject.getString("category_name"));

                                //adding the list
                                HomeActivity.replayList.add(category);
                                categoryByDeviceResponse.setResponse(HomeActivity.replayList);
                            }
                            populateRecyclerView(categoryByDeviceResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.toString());
//                    Toast.makeText(ReplayActivity.this, "Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
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

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ReplayActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });
    }

    private void getCategories(String uid, String guid) {
        apiInterface = ApiClient.getInstance().getMyApi();

        Log.d(TAG, "getCategories: uid: " + uid);
        Log.d(TAG, "getCategories: guid: " + guid);
        CategoryByDeviceModel model = new CategoryByDeviceModel(uid, guid, "4");
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                container.stopShimmer();
                container.setVisibility(View.GONE);
            }
        });
    }
}