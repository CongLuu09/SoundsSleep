package com.kenhtao.site.soundssleep.data;

import com.google.gson.annotations.SerializedName;
import com.kenhtao.site.soundssleep.models.SoundDto;

import java.util.List;

public class SoundData {
    @SerializedName("data")
    private List<SoundDto> sounds;

    public List<SoundDto> getSounds() { return sounds; }
}
