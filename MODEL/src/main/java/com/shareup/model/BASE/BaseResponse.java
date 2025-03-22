package com.shareup.model.BASE;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("message")
    protected String serverMessage;
    protected int code;

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
