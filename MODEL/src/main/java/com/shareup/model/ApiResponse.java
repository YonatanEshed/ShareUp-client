package com.shareup.model;

public class ApiResponse<T> {
    private T data;
    private String message;
    private int statusCode;

    public ApiResponse() {
        this.data = null;
        this.message = "";
        this.statusCode = 0;
    }

    public ApiResponse(T data, String message, int statusCode) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
