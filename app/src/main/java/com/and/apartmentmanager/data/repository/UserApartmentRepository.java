package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.UserApartmentDao;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserApartmentRepository {
    private final UserApartmentDao userApartmentDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserApartmentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userApartmentDao = db.userApartmentDao();
    }

    public void insert(UserApartmentEntity userApartment) {
        executor.execute(() -> userApartmentDao.insert(userApartment));
    }
}
