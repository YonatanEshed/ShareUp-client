package com.shareup.model;

import com.shareup.model.BASE.BaseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post extends BaseEntity {
    private String mediaURL;
    private String caption;
    private int likesCount;
    private Date createdAt;
    private Profile user;

    public Post() {
        super();
        user = new Profile();
        mediaURL = "";
        caption = "";
        likesCount = 0;
        createdAt = new Date();
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId='" + user.getId() + '\'' +
                "username='" + user.getUsername() + '\'' +
                ", mediaURL='" + mediaURL + '\'' +
                ", caption='" + caption + '\'' +
                ", likesCount=" + likesCount +
                ", createdAt=" + createdAt +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("caption", caption);
        return map;
    }
}
