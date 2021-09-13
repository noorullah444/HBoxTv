package com.example.hboxtv.model;

public class SignUpModel {
    final String email;
    final String apssword;
    final String uid;
    final String ipadress;

    public SignUpModel(String email, String password, String uid, String ipAddress) {
        this.email = email;
        this.apssword = password;
        this.uid = uid;
        this.ipadress = ipAddress;
    }
}