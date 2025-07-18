package com.kenhtao.site.soundssleep.service;

import com.kenhtao.site.soundssleep.data.SoundData;

public class SoundResponse {
    private boolean status;
    private SoundData data;
    private String message;

    public boolean isStatus() { return status; }
    public SoundData getData() { return data; }
    public String getMessage() { return message; }
}