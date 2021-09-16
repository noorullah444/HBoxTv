
package com.example.hboxtv.model;

public class CategoryByDeviceModel {
    private String deviceuid;
    private String customerguid;
    private String category_type;

    public CategoryByDeviceModel(String uid, String guid, String categoryType) {
        this.deviceuid = uid;
        this.customerguid = guid;
        this.category_type = categoryType;
    }
}
