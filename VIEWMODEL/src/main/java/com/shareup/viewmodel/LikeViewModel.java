package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.service.PostService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class LikeViewModel extends BaseViewModel<Boolean> {
    private final PostService postService;

    public LikeViewModel(Application application) {
        super();
        this.postService = new PostService(application);
    }

    public void likePost(String postId) {
        executeApiCall(ApiMethod.ACTION, callback -> postService.likePost(postId, callback::onResult));
    }

    public void unlikePost(String postId) {
        executeApiCall(ApiMethod.ACTION, callback -> postService.unlikePost(postId, callback::onResult));
    }
}
