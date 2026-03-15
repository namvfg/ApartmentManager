package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.InviteCodeDao;
import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;
import com.and.apartmentmanager.data.local.entity.NotificationEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InviteCodeRepository {
    private final InviteCodeDao inviteCodeDao;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public InviteCodeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        inviteCodeDao = db.inviteCodeDao();
    }

    public void insert(InviteCodeEntity inviteCode) {
        executor.execute(() -> inviteCodeDao.insert(inviteCode));
    }
}
