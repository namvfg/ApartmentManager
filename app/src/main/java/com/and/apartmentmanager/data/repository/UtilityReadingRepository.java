package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.UtilityReadingDao;
import com.and.apartmentmanager.data.local.entity.UtilityReadingEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UtilityReadingRepository {
    private final UtilityReadingDao utilityReadingDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UtilityReadingRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        utilityReadingDao = db.utilityReadingDao();
    }

    public void insert(UtilityReadingEntity utilityReading) {
        executor.execute(() -> utilityReadingDao.insert(utilityReading));
    }


}
