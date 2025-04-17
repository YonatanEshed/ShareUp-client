package com.shareup.model;

public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean success;
    private String error;
    private int statusCode;

    public ApiResponse() {
        this.data = null;
        this.message = "";
        this.statusCode = 0;
        success = true;
    }

    public ApiResponse(T data, String message, int statusCode, boolean success) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
