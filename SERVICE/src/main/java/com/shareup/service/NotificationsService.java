package com.shareup.service;

import android.app.Application;

import com.shareup.model.ApiResponse;
import com.shareup.model.Post;
import com.shareup.service.BASE.BaseService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationsService extends BaseService {
    public NotificationsService(Application application) {
        super(application);
        this.SERVICE_ROUTE = "notifications/";
    }

    public void setFcmToken(String token, Consumer<ApiResponse<Void>> callback) {
        String route = "fcmToken/";

        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", token);

        post(route, data, Post.class, response -> callback.accept((ApiResponse<Void>) response));
    }

    public void clearFcmToken(Consumer<ApiResponse<Void>> callback) {
        String route = "fcmToken/";
        delete(route, Post.class, response -> callback.accept((ApiResponse<Void>) response));
    }
}
