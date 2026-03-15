package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.UnitDao;
import com.and.apartmentmanager.data.local.entity.UnitEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnitRepository {
    private final UnitDao unitDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UnitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        unitDao = db.unitDao();
    }

    public void insert(UnitEntity unit) {
        executor.execute(() -> unitDao.insert(unit));
    }

}
