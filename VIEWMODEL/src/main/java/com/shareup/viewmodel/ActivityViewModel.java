package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.Activity;
import com.shareup.service.ActivityService;
import com.shareup.viewmodel.BASE.BaseViewModel;


public class ActivityViewModel extends BaseViewModel<Activity> {
    private final ActivityService activityService;

    public ActivityViewModel(Application application) {
        super();
        this.activityService = new ActivityService(application);
    }

    public void getUserActivity(String userId) {
        executeListApiCall(callback -> activityService.getUserActivity(userId, callback::onResult));
    }
}
