package com.example.hboxtv.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.hboxtv.email.JSSEProvider;
import com.example.hboxtv.model.Channel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
//                    sendEmail("Android Studio", "", "Test email from Android Studio");
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

    public void sendEmail(String body, String from, String subject) {
        Session session;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy 'at' HH:mm:ss a z");
            String currentDateandTime = dateFormat.format(new Date());

            final String host = "smtp.ipage.com";
            final String username = "donotreply-secureapps@mobipixels.com";
            final String password = "Secure#1234";

            Security.addProvider(new JSSEProvider());
            Properties property1 = new Properties();
            property1.put("mail.host", host);
            property1.put("mail.smtp.auth", "true");

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.socketFactory.port", "587");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "587");
            //  props.put("mail.smtp.socketFactory.fallback", "false");
            //  props.put("mail.smtp.user", username);
            //  creates a new session, no Authenticator (will connect() later)
            //  Session session = Session.getDefaultInstance(props, this);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Authenticator auth = new Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                };
                session = Session.getInstance(property1, auth);
            } else {
                session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });
            }

            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(username));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("nalbadar14@gmail.com"));

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);
            // Send message
            Transport.send(message);

        } catch (MessagingException e) {
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