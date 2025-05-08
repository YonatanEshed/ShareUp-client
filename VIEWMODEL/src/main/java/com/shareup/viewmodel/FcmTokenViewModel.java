package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.service.ActivityService;
import com.shareup.viewmodel.BASE.BaseViewModel;


public class FcmTokenViewModel extends BaseViewModel<Void> {
    private final ActivityService activityService;

    public FcmTokenViewModel(Application application) {
        super();
        this.activityService = new ActivityService(application);
    }

    public void setFcmToken(String token) {
        executeApiCall(ApiMethod.DELETE ,callback -> activityService.setFcmToken(token, callback::onResult));
    }

    public void clearFcmToken() {
        executeApiCall(ApiMethod.DELETE ,callback -> activityService.clearFcmToken(callback::onResult));
    }
}
