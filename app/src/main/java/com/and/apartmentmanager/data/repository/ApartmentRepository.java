package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ApartmentDao;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApartmentRepository {
    private final ApartmentDao apartmentDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ApartmentRepository(Application application) {
        apartmentDao = AppDatabase.getInstance(application).apartmentDao();
    }

    public LiveData<List<ApartmentEntity>> getAll() {
        return apartmentDao.getAll();
    }

    public LiveData<ApartmentEntity> getById(int id) {
        return apartmentDao.getById(id);
    }

    public LiveData<List<ApartmentEntity>> getByAdmin(int adminId) {
        return apartmentDao.getByAdmin(adminId);
    }

    public void insert(ApartmentEntity apartment) {
        executor.execute(() -> apartmentDao.insert(apartment));
    }

    public void update(ApartmentEntity apartment) {
        executor.execute(() -> apartmentDao.update(apartment));
    }

    public void deactivate(int id) {
        executor.execute(() -> apartmentDao.deactivate(id));
    }
}
