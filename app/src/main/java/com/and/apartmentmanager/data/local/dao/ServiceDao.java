package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.dto.ServiceDTO;
import com.and.apartmentmanager.data.local.entity.ServiceEntity;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert
    long insert(ServiceEntity service);

    @Update
    void update(ServiceEntity service);

    @Query("SELECT * FROM services")
    List<ServiceEntity> getAll();
    @Query("SELECT * FROM services WHERE apartment_id = :apartmentId")
    List<ServiceEntity> getServicesByApartment(int apartmentId);

    @Query(
            "SELECT s.name, s.pricing_type, s.description " +
                    "FROM services s " +
                    "INNER JOIN user_services us ON us.service_id = s.id " +
                    "WHERE us.unit_id = :unitId"
    )
    List<ServiceDTO> getServicesByUnitId(int unitId);
}