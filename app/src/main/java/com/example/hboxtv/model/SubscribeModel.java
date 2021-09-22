package com.example.hboxtv.model;

public class SubscribeModel {
    final String customerguid;
    final String device_uuid;
    final String amount;
    final String item_name;

    public SubscribeModel(String guid, String uid, String amount, String packageName) {
        this.customerguid = guid;
        this.device_uuid = uid;
        this.amount = amount;
        this.item_name = packageName;
    }
}
