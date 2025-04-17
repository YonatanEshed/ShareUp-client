package com.shareup.model.BASE;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    protected String id;
    protected ApiMethod actionType;

    public BaseEntity() {
        id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApiMethod getActionType() {
        return actionType;
    }

    public void setActionType(ApiMethod actionType) {
        this.actionType = actionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }
}
