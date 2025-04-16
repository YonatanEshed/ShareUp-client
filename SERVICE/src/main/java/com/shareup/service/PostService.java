package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.Comment;
import com.shareup.model.Post;
import com.shareup.service.BASE.BaseService;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class PostService extends BaseService {
    public PostService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "posts/";
    }

    public void getPost(String postId, Consumer<ApiResponse<Post>> callback) {
        String route = postId + "/";
        get(route, Post.class, response -> callback.accept((ApiResponse<Post>) response));
    }

    public void getUserPosts(String userId, Consumer<ApiResponse<ArrayList<Post>>> callback) {
        String route = "user/" + userId + "/";
        get(route, ResponseType.LIST, Post.class, response -> callback.accept((ApiResponse<ArrayList<Post>>) response));
    }

    public void likePost(String postId, Consumer<ApiResponse<Void>> callback) {
        String route = postId + "/like/";
        post(route, null, Boolean.class, response -> callback.accept((ApiResponse<Void>) response));
    }

    public void unlikePost(String postId, Consumer<ApiResponse<Void>> callback) {
        String route = postId + "/like/";
        delete(route, Boolean.class, response -> callback.accept((ApiResponse<Void>) response));
    }
}
