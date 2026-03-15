package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.and.apartmentmanager.data.local.entity.NotificationEntity;

@Dao
public interface NotificationDao {
    @Insert
    long insert(NotificationEntity notification);
}
