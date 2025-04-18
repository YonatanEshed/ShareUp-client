package com.shareup.model;

import com.shareup.model.BASE.BaseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment extends BaseEntity {
    private String postId;
    private Profile user;
    private String content;
    private Date createdAt;

    public Comment() {
        super();
        user = new Profile();
        postId = "";
        content = "";
        createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", userId='" + user + '\'' +
                ", text='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        return map;
    }
}
