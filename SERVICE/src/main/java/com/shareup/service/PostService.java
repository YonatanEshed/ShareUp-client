package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.Comment;
import com.shareup.model.EmptyResponse;
import com.shareup.model.Post;
import com.shareup.model.Profile;
import com.shareup.service.BASE.BaseService;

import java.io.File;
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

    public void uploadPost(Post post, File image, Consumer<ApiResponse<Post>> callback) {
        String route = "";
        post(route, post.toMap(), image, Post.class, response -> callback.accept((ApiResponse<Post>) response));
    }

    public void updatePost(String postId, Post post, Consumer<ApiResponse<Post>> callback) {
        String route = postId + "/";
        put(route, post.toMap(), Post.class, response -> callback.accept((ApiResponse<Post>) response));
    }

    public void deletePost(String postId, Consumer<ApiResponse<Post>> callback) {
        String route = postId + "/";
        delete(route, Post.class, response -> callback.accept((ApiResponse<Post>) response));
    }

    public void getUserPosts(String userId, Consumer<ApiResponse<ArrayList<Post>>> callback) {
        String route = "user/" + userId + "/";
        get(route, true, Post.class, response -> callback.accept((ApiResponse<ArrayList<Post>>) response));
    }

    public void getPostsFeed(Consumer<ApiResponse<ArrayList<Post>>> callback) {
        String route = "feed/";
        get(route, true, Post.class, response -> callback.accept((ApiResponse<ArrayList<Post>>) response));
    }

    public void getPostsFollowingFeed(Consumer<ApiResponse<ArrayList<Post>>> callback) {
        String route = "feed/following/";
        get(route, true, Post.class, response -> callback.accept((ApiResponse<ArrayList<Post>>) response));
    }

    public void likePost(String postId, Consumer<ApiResponse<Boolean>> callback) {
        String route = postId + "/like/";
        post(route, null, EmptyResponse.class, response -> callback.accept((ApiResponse<Boolean>) response));
    }

    public void unlikePost(String postId, Consumer<ApiResponse<Boolean>> callback) {
        String route = postId + "/like/";
        delete(route, EmptyResponse.class, response -> callback.accept((ApiResponse<Boolean>) response));
    }

    public void getPostLikes(String postId, Consumer<ApiResponse<ArrayList<Profile>>> callback) {
        String route = postId + "/likes/";
        get(route, true, Profile.class, response -> callback.accept((ApiResponse<ArrayList<Profile>>) response));
    }

    public void getComments(String postId, Consumer<ApiResponse<ArrayList<Comment>>> callback) {
        String route = postId + "/comment/";
        get(route, true, Comment.class, response -> callback.accept((ApiResponse<ArrayList<Comment>>) response));
    }

    public void addComment(String postId, Map<String, Object> commentData, Consumer<ApiResponse<Comment>> callback) {
        String route = postId + "/comment/";
        post(route, commentData, Comment.class, response -> callback.accept((ApiResponse<Comment>) response));
    }

    public void deleteComment(String postId, String commentId, Consumer<ApiResponse<Comment>> callback) {
        String route = postId + "/comment/" + commentId + "/";
        delete(route, Comment.class, response -> callback.accept((ApiResponse<Comment>) response));
    }
}
