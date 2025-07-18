package com.kenhtao.site.soundssleep.models;

public class SettingItem {
    private final String title;
    private final String subtitle;

    public SettingItem(String title) {
        this.title = title;
        this.subtitle = null;
    }

    public SettingItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasSubtitle() {
        return subtitle != null && !subtitle.trim().isEmpty();
    }
}
