package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.ApartmentEntity;

import java.util.List;

@Dao
public interface ApartmentDao {

    @Query("SELECT * FROM apartments")
    LiveData<List<ApartmentEntity>> getAll();

    @Query("SELECT * FROM apartments WHERE id = :id")
    LiveData<ApartmentEntity> getById(int id);

    @Query("SELECT * FROM apartments WHERE admin_id = :adminId")
    LiveData<List<ApartmentEntity>> getByAdmin(int adminId);

    @Insert
    void insert(ApartmentEntity apartment);

    @Update
    void update(ApartmentEntity apartment);

    @Query("UPDATE apartments SET is_active = 0 WHERE id = :id")
    void deactivate(int id);

    @Query("SELECT COUNT(*) FROM blocks WHERE apartment_id = :apartmentId")
    int countBlocks(long apartmentId);
}
