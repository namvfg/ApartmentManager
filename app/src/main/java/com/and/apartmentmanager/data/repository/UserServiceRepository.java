package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ApartmentDao;
import com.and.apartmentmanager.data.local.dao.UserServiceDao;
import com.and.apartmentmanager.data.local.dto.ServiceDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserServiceRepository {
    private final UserServiceDao userServiceDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserServiceRepository(Application application) {
        userServiceDao = AppDatabase.getInstance(application).userServiceDao();
    }

    public List<ServiceDTO> getServicesByUnitIdBlocking(int unitId) {
        return userServiceDao.getServicesByUnitId(unitId);
    }
}
