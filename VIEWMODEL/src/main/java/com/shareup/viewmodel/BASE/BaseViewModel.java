package com.shareup.viewmodel.BASE;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shareup.model.ApiResponse;
import com.shareup.model.ApiMethod;

import java.util.ArrayList;

public abstract class BaseViewModel<T> extends ViewModel {

    protected MutableLiveData<T> data = new MutableLiveData<>();
    protected MutableLiveData<ArrayList<T>> dataList = new MutableLiveData<>();
    protected MutableLiveData<T> updateData = new MutableLiveData<>();
    protected MutableLiveData<T> postData = new MutableLiveData<>();
    protected MutableLiveData<Boolean> deleteData = new MutableLiveData<>();
    protected MutableLiveData<Boolean> actionData = new MutableLiveData<>();

    protected MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    protected MutableLiveData<String> message = new MutableLiveData<>(null);

    public LiveData<T> getData() {
        return data;
    }

    public LiveData<ArrayList<T>> getDataList() {
        return dataList;
    }

    public LiveData<T> getUpdateData() {
        return updateData;
    }

    public LiveData<T> getPostData() {
        return postData;
    }

    public LiveData<Boolean> getDeleteData() {
        return deleteData;
    }

    public LiveData<Boolean> getActionData() {
        return actionData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // Helper method to handle API requests

    protected void executeApiCall(ApiMethod method, ApiCall<T> apiCall) {
        updateLiveData(isLoading, true);
        updateLiveData(message, null); // Clear previous errors

        apiCall.execute(result -> {
            updateLiveData(isLoading, false);

            if (result != null) {
                switch (method) {
                    case GET:
                        updateLiveData(data, result.getData());
                        break;
                    case POST:
                        updateLiveData(postData, result.getData());
                        break;
                    case PUT:
                        updateLiveData(updateData, result.getData());
                        break;
                    case DELETE:
                        updateLiveData(deleteData, result.isSuccess());
                        break;
                    case ACTION:
                        updateLiveData(actionData, result.isSuccess());
                }

                if (!result.isSuccess()) {
                    updateLiveData(message, result.getMessage());
                } else {
                    updateLiveData(message, null);
                }
            } else {
                updateLiveData(message, "Failed to fetch data.");
            }
        });
    }


    protected void executeListApiCall(ApiCall<ArrayList<T>> apiCall) {
        updateLiveData(isLoading, true);
        updateLiveData(message, null); // Clear previous errors

        apiCall.execute(result -> {
            updateLiveData(isLoading, false);

            if (result != null) {
                if (result.getData() == null) {
                    result.setData(new ArrayList<>());
                    updateLiveData(dataList, result.getData());
                    updateLiveData(message, result.getMessage());
                } else {
                    updateLiveData(dataList, result.getData());
                    updateLiveData(message, null);
                }
            } else {
                updateLiveData(message, "Failed to fetch data.");
            }
        });
    }

    private <V> void updateLiveData(MutableLiveData<V> liveData, V value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            liveData.setValue(value);
        } else {
            liveData.postValue(value);
        }
    }

    // Functional interface for API calls
    public interface ApiCall<T> {
        void execute(ApiCallback<T> callback);
    }

    // Callback for API results
    public interface ApiCallback<T> {
        void onResult(ApiResponse<T> result);
    }
}

