package com.shareup.service;

import android.content.Context;

import com.shareup.model.Profile;
import com.shareup.service.BASE.BaseService;

import java.util.function.Consumer;

public class ProfileService extends BaseService {
    public ProfileService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "profile/";
    }

    public void getProfile(String userId, Consumer<Profile> callback) {
        String route = userId + "/";
        get(route, Profile.class, ResponseType.SINGLE, response -> callback.accept((Profile) response));
    }

    public void updateProfile(Profile profile, Consumer<Profile> callback) {
        String route = "";
        put(route, profile.toMap(), Profile.class, ResponseType.MESSAGE, response -> callback.accept((Profile) response));
    }
}
