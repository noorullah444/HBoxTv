package com.example.hboxtv.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hboxtv.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactUsActivity extends AppCompatActivity {
    private static final String TAG = ContactUsActivity.class.getSimpleName();
    private String GUID;
    private EditText editTextSubject;
    private EditText editTextMessage;
    private String subject;
    private String message;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        // for full screen activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // get value from shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this);
        GUID = preferences.getString("guid", "");

        initViews();
        setClickListeners();
    }

    private void initViews() {
        editTextSubject = findViewById(R.id.edit_text_subject);
        editTextMessage = findViewById(R.id.edit_text_message);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setClickListeners() {
        findViewById(R.id.btn_back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ContactUsActivity.this, R.anim.button_click));
                onBackPressed();
            }
        });

        findViewById(R.id.button_send_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(ContactUsActivity.this, R.anim.button_click));
                if (confirmInput()) {
                    progressBar.setVisibility(View.VISIBLE);
                    subject = editTextSubject.getText().toString();
                    message = editTextMessage.getText().toString();

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        //Background work here
                        sendFeedback(subject, message);
                        handler.post(() -> {
                            //UI Thread work here
                        });
                    });
                }
            }
        });
    }

    private void sendFeedback(String subject, String message) {
        final String JSON_URL = "http://54.36.204.161/iptvapi/objects/contactus.php";
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("customerguid", GUID);
            jsonBody.put("subject", subject);
            jsonBody.put("message", message);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, JSON_URL, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            String code = response.getString("code");
                            String msg = response.getString("message");
                            progressBar.setVisibility(View.GONE);
                            showDialog(msg);
                            Log.d(TAG, "onResponse: code:" + code + " message: " + msg);

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
                    Toast.makeText(ContactUsActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
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

    private boolean validateSubject() {
        String subject = editTextSubject.getText().toString().trim();

        if (subject.isEmpty()) {
            editTextSubject.setError("Field can't be empty");
            return false;
        } else {
            editTextSubject.setError(null);
            return true;
        }
    }

    private boolean validateMessage() {
        String message = editTextMessage.getText().toString().trim();

        if (message.isEmpty()) {
            editTextMessage.setError("Field can't be empty");
            return false;
        } else {
            editTextMessage.setError(null);
            return true;
        }
    }

    private boolean confirmInput() {
        if (!validateSubject() | !validateMessage()) {
            return false;
        } else
            return true;
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(ContactUsActivity.this).setTitle("Send Successful!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}