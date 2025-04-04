package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.Post;
import com.shareup.service.PostService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class PostViewModel extends BaseViewModel<Post> {
    private final PostService postService;

    public PostViewModel(Application application) {
        super();
        this.postService = new PostService(application);
    }

    public void getUserPosts(String userId) {
        executeListApiCall(callback -> postService.getUserPosts(userId, callback::onResult));
    }
}
