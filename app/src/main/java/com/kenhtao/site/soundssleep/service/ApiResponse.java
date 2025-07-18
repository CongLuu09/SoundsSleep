package com.kenhtao.site.soundssleep.service;

public class ApiResponse<T> {
    private boolean status;
    private T data;

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}

