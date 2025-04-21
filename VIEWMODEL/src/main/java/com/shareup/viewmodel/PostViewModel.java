package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.model.Post;
import com.shareup.service.PostService;
import com.shareup.viewmodel.BASE.BaseViewModel;

import java.io.File;

public class PostViewModel extends BaseViewModel<Post> {
    private final PostService postService;

    public PostViewModel(Application application) {
        super();
        this.postService = new PostService(application);
    }

    public void getPost(String postId) {
        executeApiCall(ApiMethod.GET, callback -> postService.getPost(postId, callback::onResult));
    }

    public void uploadPost(String caption, File postPicture) {
        Post post = new Post();
        post.setCaption(caption);
        executeApiCall(ApiMethod.POST, callback -> postService.uploadPost(post, postPicture, callback::onResult));
    }

    public void updatePost(String postId, String Caption) {
        Post post = new Post();
        post.setCaption(Caption);
        executeApiCall(ApiMethod.PUT, callback -> postService.updatePost(postId, post, callback::onResult));
    }

    public void getUserPosts(String userId) {
        executeListApiCall(callback -> postService.getUserPosts(userId, callback::onResult));
    }

    public void getPostsFeed() {
        executeListApiCall(callback -> postService.getPostsFeed(callback::onResult));
    }

    public void getPostsFollowingFeed() {
        executeListApiCall(callback -> postService.getPostsFollowingFeed(callback::onResult));
    }
}
