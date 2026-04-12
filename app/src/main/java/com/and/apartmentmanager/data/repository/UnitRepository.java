package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.UnitDao;
import com.and.apartmentmanager.data.local.entity.UnitEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnitRepository {
    private final UnitDao unitDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UnitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        unitDao = db.unitDao();
    }

    public LiveData<List<UnitEntity>> getByBlock(long blockId) {
        return unitDao.getByBlock(blockId);
    }

    public void insert(UnitEntity unit) {
        executor.execute(() -> unitDao.insert(unit));
    }

    public void update(UnitEntity unit) {
        executor.execute(() -> unitDao.update(unit));
    }

    public void delete(UnitEntity unit) {
        executor.execute(() -> unitDao.delete(unit));
    }

    public UnitEntity getById(int id) {
        return unitDao.getById(id);
    }

    public int countByApartment(long apartmentId) {
        return unitDao.countByApartment(apartmentId);
    }

    public String getFullUnitName(int unitId) {
        return unitDao.getFullUnitName(unitId);
    }
}
