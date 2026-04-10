package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;

import java.util.List;

@Dao
public interface UserApartmentDao {

    @Insert
    long insert(UserApartmentEntity userApartment);

    @Update
    void update(UserApartmentEntity userApartment);

    @Query("SELECT * FROM user_apartments WHERE user_id = :userId")
    List<UserApartmentEntity> getAllByUserId(int userId);

    @Query("SELECT * FROM user_apartments " +
            "WHERE user_id = :userId AND status = 'active' LIMIT 1")
    UserApartmentEntity getActiveByUserId(int userId);

    @Query("SELECT * FROM user_apartments WHERE apartment_id = :apartmentId")
    List<UserApartmentEntity> getByApartmentId(int apartmentId);
}