package com.shareup.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class ShareUpApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("shareup-21b47")
                .setApplicationId("1:854857677319:android:76515d81fb5ab7f87048a2") // Correct App ID
                .setApiKey("AIzaSyCFW55XVjRY-GKI5Vi8FV6EptIHZo5O4TM")
                .setStorageBucket("shareup-21b47.firebasestorage.app")
                .build();

        FirebaseApp.initializeApp(this, options);
    }
}
