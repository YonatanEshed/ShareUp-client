package com.shareup.viewmodel;

import android.app.Application;

import com.shareup.repository.BASE.BaseRepository;
import com.shareup.viewmodel.BASE.BaseViewModel;

public class XviewModel extends BaseViewModel {
    @Override
    protected BaseRepository createRepository(Application application) {
        return null;
    }

    public XviewModel(Class tEntity, Class tCollection, Application application) {
        super(tEntity, tCollection, application);
    }
}
