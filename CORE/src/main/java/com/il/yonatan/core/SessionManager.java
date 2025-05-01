package com.il.yonatan.core;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.shareup.model.SingleEvent;

public class SessionManager {
    private static SessionManager instance;
    private final MutableLiveData<SingleEvent> forceLogout = new MutableLiveData<>();


    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void triggerLogout() {
        forceLogout.setValue(new SingleEvent());
    }

    public LiveData<SingleEvent> getForceLogout() {
        return forceLogout;
    }
}