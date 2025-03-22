package com.shareup.model;

import com.shareup.model.BASE.BaseEntity;

public class AuthResponse extends BaseEntity {
    private String token;
    private String userId;

    public AuthResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + getServerMessage() + '\'' +
                '}';
    }
}
