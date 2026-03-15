package com.and.apartmentmanager.data.local.dao;

import android.app.Service;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.ServiceEntity;

@Dao
public interface ServiceDao {
    @Insert
    long insert(ServiceEntity service);

    @Update
    void update(ServiceEntity service);
}
