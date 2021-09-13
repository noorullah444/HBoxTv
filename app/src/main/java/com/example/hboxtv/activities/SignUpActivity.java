package com.example.hboxtv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiClient;
import com.example.hboxtv.api.ApiInterface;
import com.example.hboxtv.model.SignUpModel;
import com.example.hboxtv.model.ApiResponse;
import com.google.android.material.textfield.TextInputLayout;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 101;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private String UID;
    private String ipAddress;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private String email;
    private String password;
    private String confirmPassword;

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        checkPhoneStatePermission();
        ipAddress = getLocalIpAddress();
        initViews();

        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmInput()){
                    email = textInputEmail.getEditText().getText().toString();
                    password = textInputPassword.getEditText().getText().toString();

                    Log.d(TAG, "onClick: email: "+ email);
                    Log.d(TAG, "onClick: password: "+ password);

                    registerNewUser(email, password, UID, ipAddress);
                }
            }
        });
    }

    private void registerNewUser(String email, String password, String uid, String ipAddress) {
        apiInterface = ApiClient.getInstance().getMyApi();
        SignUpModel model = new SignUpModel(email, password, uid, ipAddress);
        Log.d(TAG, "registerNewUser: model: "+ model);
        Call<ApiResponse> call = apiInterface.registerUser(model);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        /*ApiResponse signUpResponse = new ApiResponse();
                        signUpResponse.setCode(response.body().getCode());
                        signUpResponse.setMessage(response.body().getMessage());*/
                        Log.d(TAG, "onResponse: code: " + response.code());
                        Log.d(TAG, "onResponse: message: " + response.message());

                        showSignUpDialog(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSignUpDialog(String message) {
        new AlertDialog.Builder(SignUpActivity.this).setTitle("Registration Successful")
                .setMessage(message)
                /*.setNegativeButton(getResources().getString(R.string.cancelString), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })*/
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public boolean confirmInput() {
        if (!validateEmail() | !validateConfirmPassword() | !validatePassword()) {
            return false;
        } else
            return true;
    }

    private boolean validateConfirmPassword() {
        String confirmPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (confirmPasswordInput.isEmpty()) {
            textInputConfirmPassword.setError("Field can't be empty");
            return false;
        } else if (!confirmPasswordInput.matches(passwordInput)) {
            textInputConfirmPassword.setError("Password does not match");
            return false;
        } else {
            textInputConfirmPassword.setError(null);
            return true;
        }
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

    private void initViews() {
//        editTextEmail = findViewById(R.id.text_input_email);
//        editTextPassword = findViewById(R.id.text_input_password);
//        editTextConfirmPassword = findViewById(R.id.text_input_confirm_password);
        textInputEmail = findViewById(R.id.text_input_layout_email);
        textInputPassword = findViewById(R.id.text_input_layout_password);
        textInputConfirmPassword = findViewById(R.id.textInputLayout3);
    }

    private void checkPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            getUid();
        }
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

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        Log.d(TAG, "getLocalIpAddress: " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
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
}