package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.and.apartmentmanager.data.local.entity.ServicePriceHistoryEntity;

@Dao
public interface ServicePriceHistoryDao {
    @Insert
    long insert(ServicePriceHistoryEntity history);
}
