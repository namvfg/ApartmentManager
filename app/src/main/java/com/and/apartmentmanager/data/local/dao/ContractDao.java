package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Query;
import com.and.apartmentmanager.data.local.entity.ContractEntity;

import java.util.List;

@Dao
public interface ContractDao {
    @Insert
    long insert(ContractEntity contract);

    @Update
    void update(ContractEntity contract);

    @Query("SELECT * FROM contracts")
    List<ContractEntity> getAllContracts();
    @Query("SELECT * FROM contracts WHERE user_id = :userId LIMIT 1")
    ContractEntity getByUserId(int userId);
    @Query("SELECT * FROM contracts WHERE user_id = :userId AND apartment_id = :apartmentId AND unit_id = :unitId LIMIT 1")
    ContractEntity getContract(int userId, int apartmentId, int unitId);

    // Kiểm tra user có hợp đồng active hay không (phục vụ UC07 - xin xóa tài khoản).
    @Query("SELECT COUNT(*) FROM contracts WHERE user_id = :userId AND status = 'active'")
    int countActiveContractsByUserId(int userId);

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


    @Query("SELECT * FROM contracts " +
            "WHERE status = 'active' " +
            "AND end_date <= :deadline " +
            "AND end_date > :now")
    List<ContractEntity> getExpiringSoon(long deadline, long now);
}

