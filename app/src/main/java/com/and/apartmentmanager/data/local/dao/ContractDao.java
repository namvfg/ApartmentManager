package com.and.apartmentmanager.data.local.dao;

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
            "WHERE status = 'active' " +
            "AND end_date <= :deadline " +
            "AND end_date > :now")
    List<ContractEntity> getExpiringSoon(long deadline, long now);
}
