package com.shareup.model;

import com.shareup.model.BASE.BaseEntity;

import java.util.Date;

public class Activity extends BaseEntity {
    private String userId;
    private String senderId;
    private String message;
    private int type;
    private Date createdAt;

    public Activity() {
        this.userId = "";
        this.senderId = "";
        this.type = 0;
        this.createdAt = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
