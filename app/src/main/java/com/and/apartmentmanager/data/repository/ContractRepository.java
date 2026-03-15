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
}
