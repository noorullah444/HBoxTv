
package com.example.hboxtv.model.Epg;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpgResponse {

    @SerializedName("channelid")
    @Expose
    private String channelid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("num")
    @Expose
    private String num;
    @SerializedName("stream_type")
    @Expose
    private String streamType;
    @SerializedName("type_name")
    @Expose
    private String typeName;
    @SerializedName("stream_id")
    @Expose
    private String streamId;
    @SerializedName("stream_icon")
    @Expose
    private String streamIcon;
    @SerializedName("epg_channel_id")
    @Expose
    private String epgChannelId;
    @SerializedName("added")
    @Expose
    private String added;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("series_no")
    @Expose
    private String seriesNo;
    @SerializedName("live")
    @Expose
    private String live;
    @SerializedName("container_extension")
    @Expose
    private String containerExtension;
    @SerializedName("custom_sid")
    @Expose
    private String customSid;
    @SerializedName("tv_archive")
    @Expose
    private String tvArchive;
    @SerializedName("direct_source")
    @Expose
    private String directSource;
    @SerializedName("tv_archive_duration")
    @Expose
    private String tvArchiveDuration;
    @SerializedName("epg")
    @Expose
    private List<Epg> epg = null;

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getStreamIcon() {
        return streamIcon;
    }

    public void setStreamIcon(String streamIcon) {
        this.streamIcon = streamIcon;
    }

    public String getEpgChannelId() {
        return epgChannelId;
    }

    public void setEpgChannelId(String epgChannelId) {
        this.epgChannelId = epgChannelId;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSeriesNo() {
        return seriesNo;
    }

    public void setSeriesNo(String seriesNo) {
        this.seriesNo = seriesNo;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getContainerExtension() {
        return containerExtension;
    }

    public void setContainerExtension(String containerExtension) {
        this.containerExtension = containerExtension;
    }

    public String getCustomSid() {
        return customSid;
    }

    public void setCustomSid(String customSid) {
        this.customSid = customSid;
    }

    public String getTvArchive() {
        return tvArchive;
    }

    public void setTvArchive(String tvArchive) {
        this.tvArchive = tvArchive;
    }

    public String getDirectSource() {
        return directSource;
    }

    public void setDirectSource(String directSource) {
        this.directSource = directSource;
    }

    public String getTvArchiveDuration() {
        return tvArchiveDuration;
    }

    public void setTvArchiveDuration(String tvArchiveDuration) {
        this.tvArchiveDuration = tvArchiveDuration;
    }

    public List<Epg> getEpg() {
        return epg;
    }

    public void setEpg(List<Epg> epg) {
        this.epg = epg;
    }

}
