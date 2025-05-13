package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.model.Profile;
import com.shareup.service.PostService;
import com.shareup.service.ProfileService;
import com.shareup.service.SearchService;
import com.shareup.viewmodel.BASE.BaseViewModel;

import java.io.File;

public class ProfileViewModel extends BaseViewModel<Profile> {
    private final ProfileService profileService;
    private final PostService postService;
    private final SearchService searchService;

    public ProfileViewModel(Application application) {
        this.profileService = new ProfileService(application);
        this.postService = new PostService(application);
        this.searchService = new SearchService(application);
    }

    public void getProfile(String userId) {
        executeApiCall(ApiMethod.GET, callback -> profileService.getProfile(userId, callback::onResult));
    }

    public void updateProfile(String username, String bio) {
        Profile profile = new Profile();
        profile.setUsername(username);
        profile.setBio(bio);
        executeApiCall(ApiMethod.PUT, callback -> profileService.updateProfile(profile, callback::onResult));
    }

    public void updateProfile(String username, String bio, File profilePicture) {
        Profile profile = new Profile();
        profile.setUsername(username);
        profile.setBio(bio);
        executeApiCall(ApiMethod.PUT, callback -> profileService.updateProfileWithImage(profile, profilePicture, callback::onResult));
    }

    public void searchUsers(String query) {
        executeListApiCall(callback -> searchService.search(query, callback::onResult));
    }

    public void getPostLikes(String postId) {
        executeListApiCall(callback -> postService.getPostLikes(postId, callback::onResult));
    }

    public void getUserFollowers(String userId) {
        executeListApiCall(callback -> profileService.getFollowers(userId, callback::onResult));
    }

    public void getUserFollowings(String userId) {
        executeListApiCall(callback -> profileService.getFollowings(userId, callback::onResult));
    }
}
