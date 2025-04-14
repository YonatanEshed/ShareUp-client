package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.AuthResponse;
import com.shareup.model.Profile;
import com.shareup.service.BASE.BaseService;

import java.io.File;
import java.util.function.Consumer;

public class ProfileService extends BaseService {
    public ProfileService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "profile/";
    }

    public void getProfile(String userId, Consumer<ApiResponse<Profile>> callback) {
        String route = userId + "/";
        get(route, Profile.class, response -> callback.accept((ApiResponse<Profile>) response));
    }

    public void updateProfile(Profile profile, Consumer<ApiResponse<Profile>> callback) {
        String route = "";
        put(route, profile.toMap(), Profile.class, response -> callback.accept((ApiResponse<Profile>) response));
    }

    public void updateProfileWithImage(Profile profile, File image, Consumer<ApiResponse<Profile>> callback) {
        String route = "";
        put(route, profile.toMap(), image, Profile.class, response -> callback.accept((ApiResponse<Profile>) response));
    }
}
