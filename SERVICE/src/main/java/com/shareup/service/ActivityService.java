package com.shareup.service;

import android.app.Application;
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

    public ActivityService(Application application) {
        super(application);
        this.SERVICE_ROUTE = "notifications/";
    }

    public void getUserActivity(String userId, Consumer<ApiResponse<ArrayList<Activity>>> callback) {
        String route = userId + "/activity/";
        get(route, true, Activity.class, response -> callback.accept((ApiResponse<ArrayList<Activity>>) response));
    }
}
