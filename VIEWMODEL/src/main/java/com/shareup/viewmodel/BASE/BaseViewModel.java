package com.shareup.viewmodel.BASE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shareup.model.ApiResponse;

import java.util.ArrayList;

public abstract class BaseViewModel<T> extends ViewModel {

    protected MutableLiveData<T> data = new MutableLiveData<>();
    protected MutableLiveData<ArrayList<T>> dataList = new MutableLiveData<>();
    protected MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    protected MutableLiveData<String> message = new MutableLiveData<>(null);

    public LiveData<T> getData() {
        return data;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // Helper method to handle API requests
    protected void executeApiCall(ApiCall<T> apiCall) {
        isLoading.setValue(true);
        message.setValue(null); // Clear previous errors

        apiCall.execute(result -> {
            isLoading.postValue(false);

            if (result != null) {
                data.postValue(result.getData());
                // ensure that if data is null, the message is not being set(to prevent display of a message when data was fetched successfully)
                if (result.getData() == null) {
                    message.postValue(result.getMessage());
                } else {
                    message.postValue(null);
                }
            } else {
                message.postValue("Failed to fetch data.");
            }
        });
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

