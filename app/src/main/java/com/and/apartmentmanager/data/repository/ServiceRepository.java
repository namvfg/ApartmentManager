package com.and.apartmentmanager.data.repository;

import android.app.Application;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ServiceDao;
import com.and.apartmentmanager.data.local.dto.ServiceDTO;
import com.and.apartmentmanager.data.local.entity.ServiceEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceRepository {
    private final ServiceDao serviceDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ServiceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        serviceDao = db.serviceDao();
    }

    private void insert(ServiceEntity service) {
        executor.execute(() -> serviceDao.insert(service));
    }

    public List<ServiceDTO> getServicesByUnitIdBlocking(int unitId) {
        return serviceDao.getServicesByUnitId(unitId);
    }

}
