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

    public void update(UserApartmentEntity userApartment) {
        executor.execute(() -> userApartmentDao.update(userApartment));
    }

    public void deleteUserFromUnit(int userId, int unitId) {
        executor.execute(() -> userApartmentDao.deleteUserFromUnit(userId, unitId));
    }

    public void deleteUserFromApartment(int userId, int apartmentId) {
        executor.execute(() -> userApartmentDao.deleteUserFromApartment(userId, apartmentId));
    }

    public UserApartmentEntity findByUserId(long userId) {
        return userApartmentDao.findByUserId(userId);
    }

    public String getUnitNameByUserId(int userId) {
        return userApartmentDao.getUnitNameByUserId(userId);
    }

    public String getRoleByUserId(int userId) {
        return userApartmentDao.getRoleByUserId(userId);
    }

    public int countActiveUsers(int apartmentId) {
        return userApartmentDao.countActiveUsers(apartmentId);
    }

    public int getUnitIdByUser(int userId) {
        return userApartmentDao.getUnitIdByUser(userId);
    }

    public long insertBlocking(UserApartmentEntity userApartment) {
        return userApartmentDao.insert(userApartment);
    }

    public int countActiveResidentsByUnit(int unitId) {
        return userApartmentDao.countActiveResidentsByUnit(unitId);
    }

    public int countActiveUserInUnit(int unitId, int userId) {
        return userApartmentDao.countActiveUserInUnit(unitId, userId);
    }
}
