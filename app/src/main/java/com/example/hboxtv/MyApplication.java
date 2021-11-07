package com.example.hboxtv;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class MyApplication extends Application {

    public static boolean isNetworkNotAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void networkDialog(Context context, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("No Internet!");
        adb.setMessage(message);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }
}
