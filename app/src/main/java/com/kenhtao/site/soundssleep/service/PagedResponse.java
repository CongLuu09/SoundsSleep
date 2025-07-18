package com.kenhtao.site.soundssleep.service;

import java.util.List;

public class PagedResponse<T> {
    private int current_page;
    private List<T> data;

    public int getCurrentPage() { return current_page; }
    public void setCurrentPage(int current_page) { this.current_page = current_page; }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
}