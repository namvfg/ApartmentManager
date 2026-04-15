package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.entity.ContractEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContractRepository {
    private final ContractDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ContractRepository(Application application) {
        dao = AppDatabase.getInstance(application).contractDao();
    }

    public void insert(ContractEntity contract) {
        executor.execute(() -> dao.insert(contract));
    }

    public void update(ContractEntity contract) {
        executor.execute(() -> dao.update(contract));
    }

    public ContractEntity getActiveByUser(int userId) {
        return dao.getActiveByUser(userId);
    }

    public LiveData<List<ContractEntity>> getByUnit(int unitId) {
        return dao.getByUnit(unitId);
    }

    public List<ContractEntity> getByUnitSync(int unitId) {
        return dao.getByUnitSync(unitId);
    }

    public ContractEntity getActiveByUserIdBlocking(long userId) {
        return dao.getActiveByUserId(userId);
    }

    // Lấy contract active theo unit
    public ContractEntity getActiveByUnitIdBlocking(long unitId) {
        return dao.getActiveByUnitId(unitId);
    }

    public long getRemainingDays(ContractEntity contract) {
        if (contract == null) return -1;

        long now = System.currentTimeMillis();
        long end = contract.getEndDate();

        long diff = end - now;

        if (diff <= 0) return 0;

        return diff / (1000 * 60 * 60 * 24); // ms → days
    }
}
