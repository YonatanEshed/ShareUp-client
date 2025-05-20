package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.model.ApiMethod;
import com.shareup.model.ApiResponse;
import com.shareup.model.AuthResponse;
import com.shareup.service.AuthService;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class AuthViewModel extends BaseViewModel<AuthResponse> {
    AuthService authService;

    public AuthViewModel(Application application) {
        super();
        authService = new AuthService(application);
    }

    public void register(String email, String username, String password) {
        executeApiCall(ApiMethod.POST, callback -> authService.register(email, username, password, callback::onResult));
    }

    public void login(String email, String password) {
        executeApiCall(ApiMethod.POST, callback -> authService.login(email, password, callback::onResult));
    }

    public boolean isLoggedIn() {
        return authService.getUserId() != null;
    }

    public void saveLogin(String token, String username) {
        authService.saveLogin(token, username);
    }
}
