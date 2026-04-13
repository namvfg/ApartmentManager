package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.ContractEntity;

@Dao
public interface ContractDao {
    @Insert
    long insert(ContractEntity contract);

    @Update
    void update(ContractEntity contract);

    // Kiểm tra user có hợp đồng active hay không (phục vụ UC07 - xin xóa tài khoản).
    @Query("SELECT COUNT(*) FROM contracts WHERE user_id = :userId AND status = 'active'")
    int countActiveContractsByUserId(int userId);
}
