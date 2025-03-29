package com.shareup.service;

import android.content.Context;

import com.shareup.model.ApiResponse;
import com.shareup.model.AuthResponse;
import com.shareup.service.BASE.BaseService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AuthService extends BaseService {
    public AuthService(Context context) {
        super(context);
        this.SERVICE_ROUTE = "auth/";
    }

    public void register(String email, String username, String password, Consumer<ApiResponse<AuthResponse>> customer) {
        String route = "register/";
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("username", username);
        body.put("password", password);
        post(route, body, AuthResponse.class, response -> customer.accept((ApiResponse<AuthResponse>) response));
    }

    public void login(String email, String password, Consumer<ApiResponse<AuthResponse>> customer) {
        String route = "login/";
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        post(route, body, AuthResponse.class, response -> customer.accept((ApiResponse<AuthResponse>) response));
    }
}
