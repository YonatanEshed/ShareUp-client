package com.shareup.service.BASE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.il.yonatan.core.SessionManager;
import com.shareup.helper.FileUtil;
import com.shareup.model.ApiMethod;
import com.shareup.model.ApiResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public abstract class BaseService {
    private static final String BASE_URL = "https://europe-west1-shareup-21b47.cloudfunctions.net/api/";
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

    protected <T> void makeApiRequest(ApiMethod method, String route, Map<String, Object> body, File file, boolean isListReponse, Class<T> dataClass, Consumer<Object> callback) {
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Object> call;

        /* File Upload Body Handling */
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MultipartBody multipartBody = null;

        MultipartBody.Part filePart = null;
        Map<String, RequestBody> partMap = new HashMap<>();

        // Add file to the multipart request
        if (file != null) {
            file = FileUtil.resizeImage(file, 1024, 1024, 80);
            if (file == null) {
                Log.e("BaseService", "Failed to resize image");
                callback.accept(null);
                return;
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }

        // Add dynamic body parameters to the part map
        if (body != null) {
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                partMap.put(entry.getKey(), RequestBody.create(MediaType.parse("text/plain"), entry.getValue().toString()));
            }
        }

        // initialize empty body if null
        if (body == null) {
            body = new HashMap<>();
        }

        /* Register Api Call */
        switch (method) {
            case GET:
                call = apiService.get(BASE_URL + SERVICE_ROUTE + route, getJwtToken());
                break;
            case POST:
                if (file != null)
                    call = apiService.postWithFile(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), filePart, partMap);
                else
                    call = apiService.post(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), body);
                break;
            case PUT:
                if (file != null)
                    call = apiService.putWithFile(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), filePart, partMap);
                else
                    call = apiService.put(BASE_URL + SERVICE_ROUTE + route, getJwtToken(), body);
                break;
            case DELETE:
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
                    Log.d("BaseService", "Response JSON: " + json);

                    if (isListReponse) {
                        Type responseType = TypeToken.getParameterized(ApiResponse.class, TypeToken.getParameterized(ArrayList.class, dataClass).getType()).getType();
                        ApiResponse<ArrayList<T>> apiResponse = gson.fromJson(json, responseType);

                        apiResponse.setStatusCode(response.code());
                        apiResponse.setSuccess(true);

                        Log.i("BaseService", apiResponse.toString());
                        callback.accept(apiResponse);
                    } else {

                        Type responseType = TypeToken.getParameterized(ApiResponse.class, dataClass).getType();
                        Log.d("BaseService", "Response Type: " + responseType);
                        ApiResponse<T> apiResponse = gson.fromJson(json, responseType);

                        apiResponse.setStatusCode(response.code());
                        apiResponse.setSuccess(true);

                        Log.i("BaseService", apiResponse.toString());
                        callback.accept(apiResponse);
                    }
                } else if(response.errorBody() != null) {
                    try {
                        String json = gson.toJson(response.errorBody().string());
                        if (json.startsWith("\"")) { // If it's a quoted string, decode it first
                            json = gson.fromJson(json, String.class);
                        }

                        Log.d("BaseService", "Response JSON: " + json);

                        if (response.code() == 401) {
                            SessionManager.getInstance().triggerLogout();
                        }

                        Type responseType = TypeToken.getParameterized(ApiResponse.class, dataClass).getType();
                        ApiResponse<T> apiResponse = gson.fromJson(json, responseType);

                        apiResponse.setStatusCode(response.code());
                        apiResponse.setSuccess(false);

                        Log.i("BaseService", apiResponse.toString());
                        callback.accept(apiResponse);

                    } catch (IOException e) {
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

    protected <T> void get(String route, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.GET, route, null, null, false, modelClass, callback);
    }

    protected <T> void get(String route, boolean isListResponse, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.GET, route, null, null, isListResponse, modelClass, callback);
    }

    protected <T> void post(String route, Map<String, Object> body, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.POST, route, body, null, false, modelClass, callback);
    }

    protected <T> void post(String route, Map<String, Object> body, File file, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.POST, route, body, file, false, modelClass, callback);
    }

    protected <T> void put(String route, Map<String, Object> body, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.PUT, route, body, null, false, modelClass, callback);
    }

    protected <T> void put(String route, Map<String, Object> body, File file, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.PUT, route, body, file, false, modelClass, callback);
    }

    protected <T> void delete(String route, Class<T> modelClass, Consumer<Object> callback) {
        makeApiRequest(ApiMethod.DELETE, route, null, null, false, modelClass, callback);
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

        @Multipart
        @POST
        Call<Object> postWithFile(@Url String url, @Header("Authorization") String token, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> partMap);

        @Multipart
        @PUT
        Call<Object> putWithFile(@Url String url, @Header("Authorization") String token, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> partMap);
    }
}
