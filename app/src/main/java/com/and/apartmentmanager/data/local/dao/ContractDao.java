package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import com.and.apartmentmanager.data.local.entity.ContractEntity;

@Dao
public interface ContractDao {
    @Insert
    long insert(ContractEntity contract);

    @Update
    void update(ContractEntity contract);
    @Query("SELECT * FROM contracts WHERE user_id = :userId LIMIT 1")
    ContractEntity getByUserId(int userId);
}

