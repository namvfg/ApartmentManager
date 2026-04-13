package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UnitEntity;

import java.util.List;

@Dao
public interface UnitDao {
    @Insert
    long insert(UnitEntity unit);

    @Update
    void update(UnitEntity unit);

    @Delete
    void delete(UnitEntity unit);

    @Query("SELECT COUNT(u.id) " +
            "FROM units u JOIN blocks b ON u.block_id = b.id " +
            "WHERE b.apartment_id = :apartmentId")
    int countByApartment(long apartmentId);

    @Query("SELECT * FROM units WHERE block_id = :blockId")
    LiveData<List<UnitEntity>> getByBlock(long blockId);

    @Query("SELECT * FROM units WHERE id = :id LIMIT 1")
    UnitEntity getById(int id);

    @Query("SELECT b.name || ' · ' || u.name FROM units u " +
            "JOIN blocks b ON u.block_id = b.id " +
            "WHERE u.id = :unitId LIMIT 1")
    String getFullUnitName(int unitId);


}
