package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;

@Dao
public interface UserApartmentDao {
    @Insert
    long insert(UserApartmentEntity userApartment);

    @Update
    void update(UserApartmentEntity userApartment);
}
