package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.NotificationDao;
import com.and.apartmentmanager.data.local.entity.NotificationEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationRepository {
    private final NotificationDao notificationDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        notificationDao = db.notificationDao();
    }

    public void insert(NotificationEntity notification) {
        executor.execute(() -> notificationDao.insert(notification));
    }
}
