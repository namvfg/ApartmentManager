package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.BlockDao;
import com.and.apartmentmanager.data.local.entity.BlockEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockRepository {
    private final BlockDao blockDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BlockRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        blockDao = db.blockDao();
    }

    public LiveData<List<BlockEntity>> getByApartment(int apartmentId) {
        return blockDao.getByApartment(apartmentId);
    }

    public void insert(BlockEntity block) {
        executor.execute(() -> blockDao.insert(block));
    }

    public void update(BlockEntity block) {
        executor.execute(() -> blockDao.update(block));
    }

    public void delete(BlockEntity block) {
        executor.execute(() -> blockDao.delete(block));
    }

    public long insertBlocking(BlockEntity block) {
        return blockDao.insert(block);
    }

    public BlockEntity getByIdSync(int id) {
        return blockDao.getByIdSync(id);
    }

    public int countUnits(int blockId) {
        return blockDao.countUnits(blockId);
    }
}
