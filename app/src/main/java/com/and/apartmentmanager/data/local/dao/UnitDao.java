package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UnitEntity;

@Dao
public interface UnitDao {
    @Insert
    long insert(UnitEntity unit);

    @Update
    void update(UnitEntity unit);

    @Delete
    void delete(UnitEntity unit);
    @Query("SELECT * FROM units WHERE id = :id LIMIT 1")
    UnitEntity getById(int id);
}
