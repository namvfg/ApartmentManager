package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.ContractEntity;

import java.util.List;

@Dao
public interface ContractDao {
    @Insert
    long insert(ContractEntity contract);

    @Update
    void update(ContractEntity contract);

    @Query("SELECT * FROM contracts " +
            "WHERE user_id = :userId " +
            "AND status = 'active' " +
            "ORDER BY id DESC LIMIT 1")
    ContractEntity getActiveByUser(int userId);

    @Query("SELECT * FROM contracts WHERE unit_id = :unitId")
    LiveData<List<ContractEntity>> getByUnit(int unitId);

    /** Đọc trên background thread (dùng kết hợp với số cư dân thực tế trong phòng). */
    @Query("SELECT * FROM contracts WHERE unit_id = :unitId")
    List<ContractEntity> getByUnitSync(int unitId);
}
