package com.shareup.service;

import android.content.Context;

import com.shareup.model.Xmodel;
import com.shareup.service.BASE.BaseService;
import java.util.function.Consumer;

import java.util.List;

public class Xservice extends BaseService {

    public Xservice(Context context) {
        super(context);
    }

    public void fetchData(String itemId, Consumer<List<Xmodel>> callback) {
        String route = "data-route/" + itemId;
        get(route, Xmodel.class, true, response -> callback.accept((List<Xmodel>) response));
    }
}
