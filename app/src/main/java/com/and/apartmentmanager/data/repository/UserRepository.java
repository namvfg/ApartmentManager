package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.UserDao;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Application application) {
        dao = AppDatabase.getInstance(application).userDao();
    }

    public LiveData<List<UserEntity>> getAll() {
        return dao.getAll();
    }

    public LiveData<UserEntity> getById(int id) {
        return dao.getById(id);
    }

    // Blocking — dùng trong background thread (ví dụ: kiểm tra login)
    public UserEntity getByEmail(String email) {
        return dao.getByEmail(email);
    }

    public LiveData<List<UserEntity>> getByRole(String role) {
        return dao.getByRole(role);
    }

    public void insert(UserEntity user) {
        executor.execute(() -> dao.insert(user));
    }

    public void update(UserEntity user) {
        executor.execute(() -> dao.update(user));
    }

    public void softDelete(int id) {
        executor.execute(() -> dao.softDelete(id, System.currentTimeMillis()));
    }

}
