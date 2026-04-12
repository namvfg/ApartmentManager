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

    public LiveData<List<UserEntity>> getByRole(String role) {
        return dao.getByRole(role);
    }

    public LiveData<List<UserEntity>> getByApartment(long apartmentId) {
        return dao.getByApartment(apartmentId);
    }

    public LiveData<List<UserEntity>> getByUnit(int unitId) {
        return dao.getByUnit(unitId);
    }

    public LiveData<List<UserEntity>> getUserRequestDelete() {
        return dao.getUserRequestDelete();
    }

    public void insert(UserEntity user) {
        executor.execute(() -> dao.insert(user));
    }

    public void update(UserEntity user) {
        executor.execute(() -> dao.update(user));
    }

    public void delete(UserEntity user) {
        executor.execute(() -> dao.delete(user));
    }

    public void softDelete(int id) {
        executor.execute(() -> dao.softDelete(id, System.currentTimeMillis()));
    }

    public UserEntity getByIdSync(int id) {
        return dao.getByIdSync(id);
    }

    public UserEntity getByEmail(String email) {
        return dao.getByEmail(email);
    }

    public int count() {
        return dao.count();
    }

    public void updateBlocking(UserEntity user) {
        dao.update(user);
    }

    public void deleteBlocking(UserEntity user) {
        dao.delete(user);
    }
}
