package com.shareup.viewmodel.BASE;

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

    protected static int statusCode = 0;

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

    public int getStatusCode() {
        return statusCode;
    }

    // Helper method to handle API requests

    protected void executeApiCall(ApiMethod method, ApiCall<T> apiCall) {
        isLoading.setValue(true);
        message.setValue(null); // Clear previous errors

        apiCall.execute(result -> {
            isLoading.postValue(false);

            if (result != null) {
                switch (method) {
                    case GET:
                        data.postValue(result.getData());
                        break;
                    case POST:
                        postData.postValue(result.getData());
                        break;
                    case PUT:
                        updateData.postValue(result.getData());
                        break;
                    case DELETE:
                        deleteData.postValue(result.isSuccess());
                        break;
                    case ACTION:
                        actionData.postValue(result.isSuccess());
                }

                // set message to null if the result is successful
                if (!result.isSuccess()) {
                    message.postValue(result.getMessage());
                } else {
                    message.postValue(null);
                }
            } else {
                message.postValue("Failed to fetch data.");
            }
        });
    }


    protected void executeListApiCall(ApiCall<ArrayList<T>> apiCall) {
        isLoading.setValue(true);
        message.setValue(null); // Clear previous errors

        apiCall.execute(result -> {
            isLoading.postValue(false);

            if (result != null) {
                // ensure that if data is null, the message is not being set(to prevent display of a message when data was fetched successfully)
                if (result.getData() == null) {
                    result.setData(new ArrayList<>());
                    dataList.postValue(result.getData());
                    message.postValue(result.getMessage());
                } else {
                    dataList.postValue(result.getData());
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

