package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;

import com.shareup.service.ProfileService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class FollowViewModel extends BaseViewModel<Boolean> {
    private final ProfileService profileService;

    public FollowViewModel(Application application) {
        super();
        this.profileService = new ProfileService(application);
    }

    public void followUser(String userId) {
        executeApiCall(ApiMethod.POST, callback -> profileService.followUser(userId, callback::onResult));
    }

    public void unfollowUser(String userId) {
        executeApiCall(ApiMethod.DELETE, callback -> profileService.unfollowUser(userId, callback::onResult));
    }
}
