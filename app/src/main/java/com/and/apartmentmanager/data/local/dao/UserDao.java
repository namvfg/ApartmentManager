package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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

    // Blocking — dùng cho luồng Auth/Profile (Người 1).
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getByIdBlocking(int id);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getByEmail(String email);

    @Query("SELECT * FROM users WHERE role = :role AND is_deleted = 0")
    LiveData<List<UserEntity>> getByRole(String role);

    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    // Soft delete
    @Query("UPDATE users SET is_deleted = 1, deleted_at = :deletedAt WHERE id = :id")
    void softDelete(int id, long deletedAt);
}
