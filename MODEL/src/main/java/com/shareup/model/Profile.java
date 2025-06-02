package com.shareup.model;

import com.shareup.model.BASE.BaseEntity;

import java.util.HashMap;
import java.util.Map;

public class Profile extends BaseEntity {
    private String username;
    private String bio;
    private String profilePicture;
    private int followingsCount;
    private int followersCount;
    private boolean isFollowed;

    public Profile() {
        super();
        username = "";
        bio = "";
        followingsCount = 0;
        followersCount = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getFollowingCount() {
        return followingsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("bio", bio);
        return map;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", followingsCount=" + followingsCount +
                ", followersCount=" + followersCount +
                '}';
    }
}
