package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.SignInModel;
import com.example.hboxtv.model.SignInResponse;
import com.example.hboxtv.model.SubscribeModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {
    private static final String TAG = SubscriptionActivity.class.getSimpleName();
    private String UID;
    private String GUID;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        // for full screen activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SubscriptionActivity.this);
        UID = preferences.getString("uid", "");
        GUID = preferences.getString("guid", "");

        initViews();
        setClickListeners();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        findViewById(R.id.card_one_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler();
                executor.execute(() -> {
                    // background work here
                    subscribePackage2("5", "1 month package");
                    handler.post(() -> {
                        // ui work here
                    });
                });

            }
        });

        findViewById(R.id.card_two_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                Toast.makeText(SubscriptionActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_four_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                Toast.makeText(SubscriptionActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_twelve_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                Toast.makeText(SubscriptionActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribePackage(String amount, String packageName) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/Paypal.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("customerguid", GUID);
            jsonBody.put("device_uuid", UID);
            jsonBody.put("amount", amount);
            jsonBody.put("item_name", packageName);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
//                            JSONArray json = new JSONArray(response);
//                            String code = response.getString("code");
//                            String msg = response.getString("message");
                            progressBar.setVisibility(View.GONE);
//                            Log.d(TAG, "onResponse: code:" + code + " message: " + msg);
                            Log.d(TAG, "onResponse: response: "+ response.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: " + error.toString());
                    Toast.makeText(SubscriptionActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
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

    private void subscribePackage2(String amount, String packageName) {
//        progressBar.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getInstance().getMyApi();
        SubscribeModel model = new SubscribeModel(GUID, UID, amount, packageName);

        Gson gson = new Gson();
        String json = gson.toJson(model);

        Log.d(TAG, "registerNewUser: model: "+ json);
        Call<String> call = apiInterface.subscribe(model);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: response: " + response.body());
                    }
                } else {
                    Log.d(TAG, "onResponse: response failed!");
//                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(SubscriptionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}