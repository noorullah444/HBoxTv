
package com.example.hboxtv.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryByDeviceResponse {

    @SerializedName("response")
    @Expose
    private List<Category> category = null;

    public List<Category> getResponse() {
        return category;
    }

    public void setResponse(List<Category> category) {
        this.category = category;
    }

}
