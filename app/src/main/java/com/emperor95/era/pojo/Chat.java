package com.emperor95.era.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chat {
    private String msg;
    private String from;

    @ServerTimestamp
    private Date time;

    public Chat() {
    }

    public Chat(String message, String sender) {
        this.msg = message;
        this.from = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String sender) {
        this.from = sender;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date timestamp) {
        this.time = timestamp;
    }
}