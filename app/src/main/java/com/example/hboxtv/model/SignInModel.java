package com.example.hboxtv.model;

public class SignInModel {
    final String email;
    final String apssword;
    final String uid;
    final String device_name;

    public SignInModel(String email, String password, String uid, String deviceName) {
        this.email = email;
        this.apssword = password;
        this.uid = uid;
        this.device_name = deviceName;
    }
}
