package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;

@Dao
public interface UserApartmentDao {
    @Insert
    long insert(UserApartmentEntity userApartment);

    @Update
    void update(UserApartmentEntity userApartment);

    // Lấy apartmentId của user đang ở (status = 'active').
    // Dùng cho SessionManager (Người 1) để các module khác biết workspace nào thuộc về user.
    @Query("SELECT apartment_id FROM user_apartments WHERE user_id = :userId AND status = 'active' LIMIT 1")
    Integer getActiveApartmentIdByUserId(int userId);
}
