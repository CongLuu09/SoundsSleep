package com.kenhtao.site.soundssleep.models;

import com.google.gson.annotations.SerializedName;

public class MixSoundDto {
    @SerializedName("idSound")
    private long idSound;

    @SerializedName("soundName")
    private String soundName;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("image")
    private String image;

    @SerializedName("volume")
    private float volume;


    public long getIdSound() { return idSound; }
    public String getSoundName() { return soundName; }
    public String getFileName() { return fileName; }
    public String getImage() { return image; }
    public float getVolume() { return volume; }


    public void setIdSound(long idSound) { this.idSound = idSound; }
    public void setSoundName(String soundName) { this.soundName = soundName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setImage(String image) { this.image = image; }
    public void setVolume(float volume) { this.volume = volume; }
}
