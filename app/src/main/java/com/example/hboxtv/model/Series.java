
package com.example.hboxtv.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Series {

    @SerializedName("name")
    @Expose
    private String seriesName;

    @SerializedName("series_id")
    @Expose
    private String seriesId;

    @SerializedName("cover")
    @Expose
    private String cover;

    public Series(String series_name, String series_id, String cover) {
        this.seriesName = series_name;
        this.seriesId = series_id;
        this.cover = cover;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
