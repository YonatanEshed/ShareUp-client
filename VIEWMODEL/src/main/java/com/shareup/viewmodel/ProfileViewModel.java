package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.Profile;
import com.shareup.service.ProfileService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class ProfileViewModel extends BaseViewModel<Profile> {
    private final ProfileService profileService;

    public ProfileViewModel(Application application) {
        this.profileService = new ProfileService(application);
    }

    public void getProfile(String userId) {
        executeApiCall(callback -> profileService.getProfile(userId, callback::onResult));
    }

    public void updateProfile(String username, String bio) {
        Profile profile = new Profile();
        profile.setUsername(username);
        profile.setBio(bio);
        executeApiCall(callback -> profileService.updateProfile(profile, callback::onResult));
    }
}
