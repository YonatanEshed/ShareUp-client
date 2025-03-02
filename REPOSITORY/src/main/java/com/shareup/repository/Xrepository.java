package com.shareup.repository;

import com.google.firebase.firestore.Query;

import com.shareup.model.BASE.BaseEntity;
import com.shareup.repository.BASE.BaseRepository;

public class Xrepository extends BaseRepository {
    @Override
    protected Query getQueryForExist(BaseEntity entity) {
        return null;
    }
}
