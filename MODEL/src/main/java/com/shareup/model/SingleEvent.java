package com.shareup.model;

public class SingleEvent {
    private boolean hasBeenHandled = false;

    public boolean markHandled() {
        if (hasBeenHandled) return false;
        hasBeenHandled = true;
        return true;
    }
}