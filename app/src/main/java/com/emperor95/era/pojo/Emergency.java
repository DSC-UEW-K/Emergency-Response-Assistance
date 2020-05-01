package com.emperor95.era.pojo;

public class Emergency {
    private String title;
    private String icon;

    public Emergency() {
    }

    public Emergency(String title, String image) {
        this.title = title;
        this.icon = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String image) {
        this.icon = image;
    }
}
