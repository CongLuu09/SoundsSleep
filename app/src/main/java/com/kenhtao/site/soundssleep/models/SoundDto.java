package com.kenhtao.site.soundssleep.models;

import com.google.gson.annotations.SerializedName;

public class SoundDto {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("link_music")
    private String linkMusic;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("volume")
    private String volume;

    @SerializedName("category")
    private String category;


    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getLink_Music() { return linkMusic; }
    public String getAvatar() { return avatar; }
    public String getVolume() { return volume; }
    public String getCategory() { return category; }


    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLink_Music(String linkMusic) { this.linkMusic = linkMusic; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setVolume(String volume) { this.volume = volume; }
    public void setCategory(String category) { this.category = category; }
}
