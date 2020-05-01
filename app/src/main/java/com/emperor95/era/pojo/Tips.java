package com.emperor95.era.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Tips {
    private String title;
    private String description;
    private String image;
    private String author;

    @ServerTimestamp
    private Date time;

    public Tips() {
    }

    public Tips(String title, String description, String image, String author) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
