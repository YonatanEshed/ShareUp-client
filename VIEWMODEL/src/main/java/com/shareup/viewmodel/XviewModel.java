package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.service.BASE.BaseService;
import com.shareup.service.Xservice;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class XviewModel extends BaseViewModel {
    private final Xservice xService;

    public XviewModel(Application application) {
        this.xService = new Xservice(application);
    }

    public void fetchData(String postId) {
        executeApiCall(callback -> xService.fetchData(postId, callback::onResult));
    }
}
