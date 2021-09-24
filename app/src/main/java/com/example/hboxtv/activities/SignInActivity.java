package com.example.hboxtv.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.SignInModel;
import com.example.hboxtv.model.SignInResponse;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 102;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private String deviceName;
    private EditText textInputEmail;
    private EditText textInputPassword;
    private String email;
    private String password;
    private String UID;
    private ProgressBar progressBar;

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        getSignUpResponse();
//        checkPhoneStatePermission();
        getUid();
        deviceName = getDeviceName();
        Log.d(TAG, "onCreate: deviceName: "+ deviceName);
        initViews();

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add animation
                view.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.button_click));

                if (confirmInput()){
                    email = textInputEmail.getText().toString();
                    password = textInputPassword.getText().toString();

                    Log.d(TAG, "onClick: email: "+ email);
                    Log.d(TAG, "onClick: password: "+ password);

                    loginUser(email, password, UID, deviceName);
                }
            }
        });

        findViewById(R.id.tv_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add animation
                v.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.button_click));

                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
//                finish();
            }
        });
    }

    private boolean confirmInput() {
        if (!validateEmail() | !validatePassword()) {
            return false;
        } else
            return true;
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!isEmailValid(emailInput)) {
            textInputEmail.setError("Enter a valid email");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void loginUser(String email, String password, String uid, String deviceName) {
        Log.d(TAG, "loginUser: email: " + email);
        Log.d(TAG, "loginUser: password: " + password);
        Log.d(TAG, "loginUser: uid: " + uid);
        Log.d(TAG, "loginUser: deviceName: " + deviceName);
        progressBar.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getInstance().getMyApi();
        SignInModel model = new SignInModel(email, password, uid, deviceName);

        Log.d(TAG, "registerNewUser: model: "+ model);
        Call<SignInResponse> call = apiInterface.loginUser(model);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String GUID = response.body().getGuid();
                        String UID = response.body().getUid();
                        String expiryDate = response.body().getExpDate();
                        String username = response.body().getUsername();
                        String password = response.body().getPassword();
                        String server = response.body().getServer();

                        // saving guid to shared prefs
//                        if (!GUID.isEmpty()) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("uid", UID);
                            editor.putString("guid", GUID);
                            editor.putString("expiry_date", expiryDate);
                            editor.putString("server", server);
                            editor.putString("user_name", username);
                            editor.putString("password", password);
                            editor.putBoolean("isLogin", true);
                            editor.apply();
//                        }

                        Log.d(TAG, "onResponse: code: " + response.body().getCode());
                        Log.d(TAG, "onResponse: message: " + response.body().getMessage());
                        Log.d(TAG, "onResponse: uid: "+ UID);
                        Log.d(TAG, "onResponse: guid: "+ GUID);
                        progressBar.setVisibility(View.GONE);
                        showSignInDialog(response.body().getMessage());
                    }
                } else {
                    Log.d(TAG, "onResponse: response failed!");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, "Make sure email and password are correct.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getCause());
                Toast.makeText(SignInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSignInDialog(String message) {
        new AlertDialog.Builder(SignInActivity.this).setTitle("Login Successful")
                .setMessage(message)
                .setCancelable(false)
                /*.setNegativeButton(getResources().getString(R.string.cancelString), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })*/
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initViews() {
        textInputEmail = findViewById(R.id.text_input_email1);
        textInputPassword = findViewById(R.id.text_input_password1);
        progressBar = findViewById(R.id.progress_bar);
    }

    private String getDeviceName() {
        return Build.MODEL;
    }

    private void getUid() {
        try {
            /*TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null)
                UID = telephonyManager.getDeviceId();*/
//            UID = UUID.randomUUID().toString();
            UID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "getUid: " + UID);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /*private void checkPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            getUid();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission granted
                getUid();
            } else {
                // permission denied
                Log.d(TAG, "onRequestPermissionsResult: permission denied!");
            }
        }
    }*/

    /*private void getSignUpResponse() {
        Intent intent = getIntent();
        if (intent != null) {
            String code = intent.getStringExtra("sign_up_response_code");
            String message = intent.getStringExtra("sign_up_response_message");
            showSignUpDialog(message);
        }
    }*/
}