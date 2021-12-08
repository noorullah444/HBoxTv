package com.example.hboxtv.model.Epg;

public class EpgBody {
    final String deviceuid;
    final String customerguid;
    final String categoryID;

    public EpgBody(String uid, String guid, String categoryID) {
        this.deviceuid = uid;
        this.customerguid = guid;
        this.categoryID = categoryID;
    }
}