package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.Profile;
import com.shareup.service.BASE.BaseService;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SearchService extends BaseService {
    public SearchService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "search";
    }

    public void search(String query, Consumer<ApiResponse<ArrayList<Profile>>> callback) {
        String route = "?q=" + query;
        get(route, true, Profile.class, response -> callback.accept((ApiResponse<ArrayList<Profile>>) response));
    }
}
