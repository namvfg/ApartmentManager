package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.ServicePriceHistoryEntity;

@Dao
public interface ServicePriceHistoryDao {

    @Insert
    long insert(ServicePriceHistoryEntity history);

    @Update
    void update(ServicePriceHistoryEntity history);

    @Query("SELECT * FROM service_price_history WHERE service_id = :serviceId AND is_active = 1 LIMIT 1")
    ServicePriceHistoryEntity getCurrentPrice(int serviceId);
}