package com.shareup.service;

import android.content.Context;

import com.shareup.model.Activity;
import com.shareup.model.ApiResponse;
import com.shareup.model.Post;
import com.shareup.service.BASE.BaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ActivityService extends BaseService {

    public ActivityService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "activity/";
    }

    public void getUserActivity(String userId, Consumer<ApiResponse<ArrayList<Activity>>> callback) {
        String route = userId + "/";
        get(route, true, Activity.class, response -> callback.accept((ApiResponse<ArrayList<Activity>>) response));
    }

    public void setFcmToken(String token, Consumer<ApiResponse<Void>> callback) {
        String route = "fcmToken/";

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        post(route, data, Post.class, response -> callback.accept((ApiResponse<Void>) response));
    }

    public void clearFcmToken(Consumer<ApiResponse<Void>> callback) {
        String route = "fcmToken/";
        delete(route, Post.class, response -> callback.accept((ApiResponse<Void>) response));
    }
}
