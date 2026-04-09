package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT COUNT(*) FROM users")
    int count();

    @Query("SELECT * FROM users WHERE is_deleted = 0")
    LiveData<List<UserEntity>> getAll();

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<UserEntity> getById(int id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getByIdSync(int id);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getByEmail(String email);

    @Query("SELECT * FROM users WHERE role = :role AND is_deleted = 0")
    LiveData<List<UserEntity>> getByRole(String role);

    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("UPDATE users SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :id")
    void softDelete(int id, long deletedAt);

    @Query("SELECT u.* FROM users u " +
            "JOIN user_apartments ua ON u.id = ua.user_id " +
            "JOIN units un ON ua.unit_id = un.id " +
            "JOIN blocks b ON un.block_id = b.id " +
            "WHERE ua.apartment_id = :apartmentId " )
    LiveData<List<UserEntity>> getByApartment(long apartmentId);

    @Query("SELECT u.* FROM users u " +
            "JOIN user_apartments ua ON u.id = ua.user_id " +
            "WHERE ua.unit_id = :unitId " +
            "AND ua.status = 'active'")
    LiveData<List<UserEntity>> getByUnit(int unitId);
}
