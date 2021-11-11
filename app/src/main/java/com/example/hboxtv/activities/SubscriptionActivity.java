package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.Channel;
import com.example.hboxtv.model.SignInModel;
import com.example.hboxtv.model.SignInResponse;
import com.example.hboxtv.model.SubscribeModel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
    private TextView tvExpiry;

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
        checkSubscription();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        tvExpiry = findViewById(R.id.tv_expiry_date);
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
                    subscribePackage("5", "1 month package");
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
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler();
                executor.execute(() -> {
                    // background work here
                    subscribePackage("10", "2 month package");
                    handler.post(() -> {
                        // ui work here
                    });
                });
            }
        });

        findViewById(R.id.card_four_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler();
                executor.execute(() -> {
                    // background work here
                    subscribePackage("20", "4 month package");
                    handler.post(() -> {
                        // ui work here
                    });
                });
            }
        });

        findViewById(R.id.card_twelve_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SubscriptionActivity.this, R.anim.button_click));
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler();
                executor.execute(() -> {
                    // background work here
                    subscribePackage("60", "12 month package");
                    handler.post(() -> {
                        // ui work here
                    });
                });
            }
        });
    }

    private void subscribePackage(String amount, String packageName) {
//        progressBar.setVisibility(View.VISIBLE);
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/Paypal.php";
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("customerguid", GUID);
            params.put("device_uuid", UID);
            params.put("amount", amount);
            params.put("item_name", packageName);

            StringRequest request = new StringRequest(Request.Method.POST, JSON_URL,
                    new com.android.volley.Response.Listener<String>() {
                        public void onResponse(String response) {
                            if (response != null) {
                                Log.d(TAG, "onResponse: paypal::"+ response);
                                Intent intent = new Intent(SubscriptionActivity.this, WebViewActivity.class);
                                intent.putExtra("subscription_url", response);
                                startActivity(intent);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                        }
                    }
            ) {
                public byte[] getBody() {
                    return new JSONObject(params).toString().getBytes();
                }
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSubscription() {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/checksubscription.php";
        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("deviceuid", UID);
            jsonBody.put("customerguid", GUID);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "123onResponse: " + response.toString());
                        try {
                                String message = response.getString("message");
                                Log.d(TAG, "onResponse: expiry message: "+ message);
                                tvExpiry.setText(message);
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
}