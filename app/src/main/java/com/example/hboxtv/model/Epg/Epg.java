package com.example.hboxtv.model.Epg;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Epg {

    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("stop")
    @Expose
    private String stop;
    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("credits")
    @Expose
    private String credits;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("episode_num")
    @Expose
    private String episodeNum;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

}
