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

    @Query("SELECT COUNT(*) FROM user_apartments WHERE apartment_id = :apartmentId AND status = 'active'")
    int countActiveUsers(int apartmentId);

    @Query("SELECT un.name FROM units un " +
            "JOIN user_apartments ua ON ua.unit_id = un.id " +
            "WHERE ua.user_id = :userId LIMIT 1")
    String getUnitNameByUserId(int userId);

    @Query("SELECT status FROM user_apartments WHERE user_id = :userId LIMIT 1")
    String getRoleByUserId(int userId);

    @Query("SELECT * FROM user_apartments WHERE user_id = :userId LIMIT 1")
    UserApartmentEntity findByUserId(int userId);

    @Query("DELETE FROM user_apartments WHERE user_id = :userId AND unit_id = :unitId")
    void deleteUserFromUnit(int userId, int unitId);

    @Query("DELETE FROM user_apartments WHERE user_id = :userId AND apartment_id = :apartmentId")
    void deleteUserFromApartment(int userId, int apartmentId);

    @Query("SELECT unit_id FROM user_apartments WHERE user_id = :userId LIMIT 1")
    int getUnitIdByUser(int userId);

    @Query("SELECT COUNT(*) FROM user_apartments ua " +
            "INNER JOIN users u ON u.id = ua.user_id " +
            "WHERE ua.unit_id = :unitId AND ua.status = 'active' AND u.is_deleted = 0")
    int countActiveResidentsByUnit(int unitId);

    @Query("SELECT COUNT(*) FROM user_apartments ua " +
            "INNER JOIN users u ON u.id = ua.user_id " +
            "WHERE ua.unit_id = :unitId AND ua.user_id = :userId AND ua.status = 'active' AND u.is_deleted = 0")
    int countActiveUserInUnit(int unitId, int userId);
}
