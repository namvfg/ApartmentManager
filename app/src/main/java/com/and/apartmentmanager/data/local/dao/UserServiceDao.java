package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.and.apartmentmanager.data.local.dto.ServiceDTO;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserServiceEntity;

import java.util.List;

@Dao
public interface UserServiceDao {
    @Query(
            "SELECT s.name, s.pricing_type, s.description " +
            "FROM services s " +
            "INNER JOIN user_services us ON us.service_id = s.id " +
            "WHERE us.unit_id = :unitId"
    )
    List<ServiceDTO> getServicesByUnitId(int unitId);

    @Insert
    void insert(UserServiceEntity entity);
}
