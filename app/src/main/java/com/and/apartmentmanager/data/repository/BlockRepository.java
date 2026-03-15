package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.BlockDao;
import com.and.apartmentmanager.data.local.entity.BlockEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockRepository {
    private final BlockDao blockDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BlockRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        blockDao = db.blockDao();
    }

    public void insert(BlockEntity block) {
        executor.execute(() -> blockDao.insert(block));
    }

}
