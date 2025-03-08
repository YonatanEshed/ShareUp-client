package com.shareup.viewmodel.BASE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel<T> extends ViewModel {

    protected MutableLiveData<T> data = new MutableLiveData<>();
    protected MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    protected MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<T> getData() {
        return data;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Helper method to handle API requests
    protected void executeApiCall(ApiCall<T> apiCall) {
        isLoading.setValue(true);
        error.setValue(null); // Clear previous errors

        apiCall.execute(result -> {
            isLoading.postValue(false);

            if (result != null) {
                data.postValue(result);
            } else {
                error.postValue("Failed to fetch data.");
            }
        });
    }

    // Functional interface for API calls
    public interface ApiCall<T> {
        void execute(ApiCallback<T> callback);
    }

    // Callback for API results
    public interface ApiCallback<T> {
        void onResult(T result);
    }
}

