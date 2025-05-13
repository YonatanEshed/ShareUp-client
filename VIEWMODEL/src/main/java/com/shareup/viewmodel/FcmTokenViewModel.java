package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;

import com.shareup.service.NotificationsService;
import com.shareup.viewmodel.BASE.BaseViewModel;


public class FcmTokenViewModel extends BaseViewModel<Void> {
    private final NotificationsService notificationsService;

    public FcmTokenViewModel(Application application) {
        super();
        this.notificationsService = new NotificationsService(application);
    }

    public void setFcmToken(String token) {
        executeApiCall(ApiMethod.POST ,callback -> notificationsService.setFcmToken(token, callback::onResult));
    }

    public void clearFcmToken() {
        executeApiCall(ApiMethod.DELETE ,callback -> notificationsService.clearFcmToken(callback::onResult));
    }
}
