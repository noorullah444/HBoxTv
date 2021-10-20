
package com.example.hboxtv.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package{

    @SerializedName("packageID")
    @Expose
    private String packageID;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("deviceID")
    @Expose
    private String deviceID;

    private Boolean isSelected;

    public Package(String packageID, String packageName, String deviceID, Boolean isSelected) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.deviceID = deviceID;
        this.isSelected = isSelected;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
