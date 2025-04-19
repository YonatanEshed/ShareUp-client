package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.model.Profile;
import com.shareup.service.ProfileService;
import com.shareup.viewmodel.BASE.BaseViewModel;

import java.io.File;

public class ProfileViewModel extends BaseViewModel<Profile> {
    private final ProfileService profileService;

    public ProfileViewModel(Application application) {
        this.profileService = new ProfileService(application);
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
}
