package com.shareup.service.BASE;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.autofill.SaveRequest;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shareup.model.BASE.BaseEntity;
import com.shareup.model.MessageResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;

public abstract class BaseService {
    private static final String BASE_URL = "https://us-central1-shareup-21b47.cloudfunctions.net/api/";
    protected String SERVICE_ROUTE = "";

    private static Retrofit retrofit;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public BaseService(Context context) {
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        gson = new Gson();
        if (retrofit == null) {
            retrofit = createRetrofitInstance();
        }
    }

    private Retrofit createRetrofitInstance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    String token = getJwtToken();
                    if (token != null) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(requestBuilder.build());
                })
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private String getJwtToken() {
        return sharedPreferences.getString("jwt_token", null);
    }

    public String getUserId() {
        return sharedPreferences.getString("user_id", null);
    }

    public void saveLogin(String token, String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwt_token", token);
        editor.putString("user_id", userId);
        editor.apply();
    }

    public void clearLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");
        editor.remove("user_id");
        editor.apply();
    }

    protected <T> void makeApiRequest(String method, String route, Map<String, Object> body, Class<T> modelClass, ResponseType type, Consumer<Object> callback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Object> call;

        switch (method) {
            case "GET":
                call = apiService.get(BASE_URL + SERVICE_ROUTE + route, getJwtToken());
                break;
            case "POST":
                call = apiService.post(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), body);
                break;
            case "PUT":
                call = apiService.put(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), body);
                break;
            case "DELETE":
                call = apiService.delete(BASE_URL + SERVICE_ROUTE + route, getJwtToken());
                break;
            default:
                Log.e("BaseService", "Unsupported method: " + method);
                callback.accept(null);
                return;
        }

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = gson.toJson(response.body());
                    switch (type) {
                        case SINGLE:
                            T model = gson.fromJson(json, modelClass);
                            Log.i("BaseService", model.toString());
                            callback.accept(model);
                            break;
                        case LIST:
                            Type listType = TypeToken.getParameterized(ArrayList.class, modelClass).getType();
                            ArrayList<T> modelList = gson.fromJson(json, listType);
                            Log.i("BaseService", modelList.toString());
                            callback.accept(modelList);
                            break;
                    }
                } else if(response.errorBody() != null) {
                    try {
                        String json = gson.toJson(response.errorBody().string());
                        if (json.startsWith("\"")) { // If it's a quoted string, decode it first
                            json = gson.fromJson(json, String.class);
                        }
                        Log.d("BaseService", json);
                        MessageResponse message = gson.fromJson(json, MessageResponse.class);
                        Log.e("BaseService", message.toString());
                        BaseEntity model = (BaseEntity) modelClass.getConstructor().newInstance();

                        model.setServerMessage(message.getMessage());

                        callback.accept(model);
                    } catch (IOException | InvocationTargetException | NoSuchMethodException |
                             InstantiationException | IllegalAccessException e) {
                        Log.e("BaseService", "API Error: " + response.errorBody(), e);
                        callback.accept(null);
                    }
                } else{
                    Log.e("BaseService", "API Error: " + response.errorBody());

                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("BaseService", "API Request Failed", t);
                callback.accept(null);
            }
        });
    }

    protected <T> void get(String route, Class<T> modelClass, ResponseType type, Consumer<Object> callback) {
        makeApiRequest("GET", route, null, modelClass, type, callback);
    }

    protected <T> void post(String route, Map<String, Object> body, Class<T> modelClass, ResponseType type, Consumer<Object> callback) {
        makeApiRequest("POST", route, body, modelClass, type, callback);
    }

    protected <T> void put(String route, Map<String, Object> body, Class<T> modelClass, ResponseType type, Consumer<Object> callback) {
        makeApiRequest("PUT", route, body, modelClass, type, callback);
    }

    protected <T> void delete(String route, Class<T> modelClass, ResponseType type, Consumer<Object> callback) {
        makeApiRequest("DELETE", route, null, modelClass, type, callback);
    }

    interface ApiService {
        @GET
        Call<Object> get(@Url String url, @Header("Authorization") String token);

        @POST
        Call<Object> post(@Url String url, @Header("Authorization") String token, @Body Map<String, Object> body);

        @PUT
        Call<Object> put(@Url String url, @Header("Authorization") String token, @Body Map<String, Object> body);

        @DELETE
        Call<Object> delete(@Url String url, @Header("Authorization") String token);
    }

    protected enum ResponseType {
        SINGLE,
        LIST,
        MESSAGE
    }
}
