package com.example.hboxtv.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.ApiResponse;
import com.example.hboxtv.model.SignInModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 102;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private String deviceName;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private String email;
    private String password;
    private String UID;

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//        getSignUpResponse();
        checkPhoneStatePermission();
        deviceName = getDeviceName();
        Log.d(TAG, "onCreate: deviceName: "+ deviceName);
        initViews();

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmInput()){
                    email = textInputEmail.getEditText().getText().toString();
                    password = textInputPassword.getEditText().getText().toString();

                    Log.d(TAG, "onClick: email: "+ email);
                    Log.d(TAG, "onClick: password: "+ password);

                    loginUser(email, password, UID, deviceName);
                }
            }
        });

        findViewById(R.id.tv_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
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
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

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
        apiInterface = ApiClient.getInstance().getMyApi();
        SignInModel model = new SignInModel(email, password, uid, deviceName);
        Log.d(TAG, "registerNewUser: model: "+ model);
        Call<ApiResponse> call = apiInterface.loginUser(model);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: code: " + response.code());
                        Log.d(TAG, "onResponse: message: " + response.message());

                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(SignInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        textInputEmail = findViewById(R.id.text_input_layout_email);
        textInputPassword = findViewById(R.id.text_input_layout_password);
    }

    private String getDeviceName() {
        return Build.MODEL;
    }

    private void getUid() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null)
                UID = telephonyManager.getDeviceId();
            Log.d(TAG, "getUid: " + UID);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void checkPhoneStatePermission() {
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
    }

    /*private void getSignUpResponse() {
        Intent intent = getIntent();
        if (intent != null) {
            String code = intent.getStringExtra("sign_up_response_code");
            String message = intent.getStringExtra("sign_up_response_message");
            showSignUpDialog(message);
        }
    }*/
}