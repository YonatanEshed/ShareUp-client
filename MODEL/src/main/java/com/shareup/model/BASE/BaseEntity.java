package com.shareup.model.BASE;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    protected String id;
    @SerializedName("message")
    protected String serverMessage;

    public BaseEntity() {
        id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }
}
