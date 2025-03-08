package com.shareup.service.BASE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseService {
    private static final String BASE_ROUTE = "api/";
    protected static String SERVICE_ROUTE = "";

    private static final String PREFS_NAME = "AppPrefs";
    private static final String TOKEN_KEY = "jwt_token";

    protected static FirebaseFunctions functions;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public BaseService(Context context) {
        functions = getFunctionsApp(context);
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    private static FirebaseFunctions getFunctionsApp(Context context) {
        if (functions == null) {
            try {
                functions = FirebaseFunctions.getInstance();
            } catch (Exception e) {
                FirebaseInstance instance = FirebaseInstance.instance(context);
                functions = FirebaseFunctions.getInstance(FirebaseInstance.app);
            }
        }
        return functions;
    }

    // Retrieves JWT token from SharedPreferences
    private String getJwtToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    // Saves JWT token
    public void saveJwtToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    // Clears JWT token (useful for logout)
    public void clearJwtToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    // Generic API request handler (supports both single object & list)
    protected <T> void makeApiRequest(
            String method,
            String route,
            Map<String, Object> body,
            Class<T> modelClass,
            boolean isList,
            Consumer<Object> callback
    ) {
        String token = getJwtToken();
        if (token == null) {
            Log.e("BaseService", "JWT Token is missing");
            callback.accept(null);
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("method", method);
        data.put("route", BASE_ROUTE + SERVICE_ROUTE + route);
        data.put("body", body);
        data.put("token", token); // Attach JWT Token

        HttpsCallableReference function = functions.getHttpsCallable("handleRequest"); // Firebase Function

        function.call(data).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Object> response = (Map<String, Object>) task.getResult().getData();

                // Handle error responses
                if (response.containsKey("error")) {
                    Log.e("BaseService", "API Error: " + response.get("error"));
                    callback.accept(null);
                    return;
                }

                try {
                    String json = gson.toJson(response);
                    if (isList) {
                        Type listType = TypeToken.getParameterized(List.class, modelClass).getType();
                        List<T> modelList = gson.fromJson(json, listType);
                        callback.accept(modelList);
                    } else {
                        T model = gson.fromJson(json, modelClass);
                        callback.accept(model);
                    }
                } catch (Exception e) {
                    Log.e("BaseService", "Error parsing response", e);
                    callback.accept(null);
                }
            } else {
                Log.e("BaseService", "API Request Failed", task.getException());
                callback.accept(null);
            }
        });
    }

    // Convenience methods for API requests
    protected <T> void get(String route, Class<T> modelClass, boolean isList, Consumer<Object> callback) {
        makeApiRequest("GET", route, null, modelClass, isList, callback);
    }

    protected <T> void get(String route, Class<T> modelClass, Consumer<Object> callback) {
        get(route, modelClass, false, callback);
    }

    protected <T> void post(String route, Map<String, Object> body, Class<T> modelClass, boolean isList, Consumer<Object> callback) {
        makeApiRequest("POST", route, body, modelClass, isList, callback);
    }

    protected <T> void post(String route, Map<String, Object> body, Class<T> modelClass, Consumer<Object> callback) {
        post(route, body, modelClass, false, callback);
    }

    protected <T> void put(String route, Map<String, Object> body, Class<T> modelClass, boolean isList, Consumer<Object> callback) {
        makeApiRequest("PUT", route, body, modelClass, isList, callback);
    }

    protected <T> void put(String route, Map<String, Object> body, Class<T> modelClass, Consumer<Object> callback) {
        put(route, body, modelClass, false, callback);
    }

    protected <T> void delete(String route, Class<T> modelClass, boolean isList, Consumer<Object> callback) {
        makeApiRequest("DELETE", route, null, modelClass, isList, callback);
    }

    protected <T> void delete(String route, Class<T> modelClass, Consumer<Object> callback) {
        delete(route, modelClass, false, callback);
    }
}
