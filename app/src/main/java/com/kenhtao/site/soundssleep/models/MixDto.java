package com.kenhtao.site.soundssleep.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MixDto implements Serializable {

    private long id;

    @SerializedName("categoryId")
    private long categoryId;

    @SerializedName("title")
    private String title;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("createdAt")
    private long createdAt;

    @SerializedName("soundIds")
    private List<Long> soundIds;

    @SerializedName("sounds")
    private List<MixSoundDto> sounds;


    public long getId() {
        return id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<Long> getSoundIds() {
        return soundIds;
    }

    public List<MixSoundDto> getSounds() {
        return sounds;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setSoundIds(List<Long> soundIds) {
        this.soundIds = soundIds;
    }

    public void setSounds(List<MixSoundDto> sounds) {
        this.sounds = sounds;
    }


    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }
}
