package com.shareup.service.BASE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    public void saveJwtToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwt_token", token);
        editor.apply();
    }

    public void clearJwtToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");
        editor.apply();
    }

    protected <T> void makeApiRequest(String method, String route, Map<String, Object> body, Class<T> modelClass, boolean isList, Consumer<Object> callback) {
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
                    if (isList) {
                        Type listType = TypeToken.getParameterized(ArrayList.class, modelClass).getType();
                        ArrayList<T> modelList = gson.fromJson(json, listType);
                        callback.accept(modelList);
                    } else {
                        T model = gson.fromJson(json, modelClass);
                        callback.accept(model);
                    }
                } else {
                    Log.e("BaseService", "API Error: " + response.errorBody());

                    if (response.code() == 401) {
                        clearJwtToken();
                        // TODO: Redirect to login page
                    }

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
}
