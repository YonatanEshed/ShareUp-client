package com.shareup.model.BASE;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    protected String id;

    public BaseEntity() {
        id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }
}
