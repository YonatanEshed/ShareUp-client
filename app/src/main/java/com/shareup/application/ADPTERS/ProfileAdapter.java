package com.shareup.application.ADPTERS;

import com.shareup.application.ADPTERS.BASE.GenericAdapter;
import com.shareup.model.Profile;

import java.util.List;

public class ProfileAdapter  extends GenericAdapter<Profile> {
    public ProfileAdapter(List<Profile> items, int layoutId, InitializeViewHolder initializeViewHolder, BindViewHolder<Profile> bindViewHolder) {
        super(items, layoutId, initializeViewHolder, bindViewHolder);
    }
}
