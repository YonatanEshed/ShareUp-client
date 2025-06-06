package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.Profile;
import com.shareup.service.BASE.BaseService;

import java.io.File;
import java.util.ArrayList;
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

    public void followUser(String userId, Consumer<ApiResponse<Boolean>> callback) {
        String route = userId + "/follow/";
        post(route, null, Boolean.class, response -> callback.accept((ApiResponse<Boolean>) response));
    }

    public void unfollowUser(String userId, Consumer<ApiResponse<Boolean>> callback) {
        String route = userId + "/follow/";
        delete(route, Boolean.class, response -> callback.accept((ApiResponse<Boolean>) response));
    }

    public void getFollowers(String userId, Consumer<ApiResponse<ArrayList<Profile>>> callback) {
        String route = userId + "/followers/";
        get(route, true, Profile.class,response -> callback.accept((ApiResponse<ArrayList<Profile>>) response));
    }

    public void getFollowings(String userId, Consumer<ApiResponse<ArrayList<Profile>>> callback) {
        String route = userId + "/following/";
        get(route, true, Profile.class, response -> callback.accept((ApiResponse<ArrayList<Profile>>) response));
    }
}
