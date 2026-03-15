package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ServicePriceHistoryDao;
import com.and.apartmentmanager.data.local.entity.ServicePriceHistoryEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicePriceHistoryRepository {
    private final ServicePriceHistoryDao servicePriceHistoryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ServicePriceHistoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        servicePriceHistoryDao = db.servicePriceHistoryDao();
    }

    public void insert(ServicePriceHistoryEntity servicePriceHistory) {
        executor.execute(() -> insert(servicePriceHistory));
    }




}
