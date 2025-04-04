package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.Post;
import com.shareup.service.BASE.BaseService;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PostService extends BaseService {
    public PostService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "posts/";
    }

    public void getUserPosts(String userId, Consumer<ApiResponse<ArrayList<Post>>> callback) {
        String route = "user/" + userId + "/";
        get(route, ResponseType.LIST, Post.class, response -> callback.accept((ApiResponse<ArrayList<Post>>) response));
    }
}
