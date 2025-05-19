package com.shareup.application.ADPTERS;

import com.shareup.application.ADPTERS.BASE.GenericAdapter;
import com.shareup.model.Activity;

import java.util.List;

public class ActivityAdapter  extends GenericAdapter<Activity> {
    public ActivityAdapter(List<Activity> items, int layoutId, InitializeViewHolder initializeViewHolder, BindViewHolder<Activity> bindViewHolder) {
        super(items, layoutId, initializeViewHolder, bindViewHolder);
    }
}
