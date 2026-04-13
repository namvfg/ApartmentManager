package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.and.apartmentmanager.data.local.entity.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    long insert(NotificationEntity notification);

    // Lấy thông báo cho User (gửi cho tất cả hoặc đích danh)
    @Query("SELECT * FROM notifications " +
            "WHERE apartment_id = :apartmentId " +
            "AND (target = 'all' OR target_user_id = :userId) " +
            "ORDER BY created_at DESC")
    LiveData<List<NotificationEntity>> getForUser(int apartmentId, int userId);

    // Admin xem tất cả thông báo của chung cư
    @Query("SELECT * FROM notifications " +
            "WHERE apartment_id = :apartmentId " +
            "ORDER BY created_at DESC")
    LiveData<List<NotificationEntity>> getByApartment(int apartmentId);

    // Đánh dấu 1 thông báo đã đọc
    @Query("UPDATE notifications SET is_read = 1, read_at = :readAt WHERE id = :id")
    void markRead(int id, long readAt);

    // Đánh dấu tất cả đã đọc
    @Query("UPDATE notifications SET is_read = 1, read_at = :readAt " +
            "WHERE apartment_id = :apartmentId " +
            "AND (target = 'all' OR target_user_id = :userId) " +
            "AND is_read = 0")
    void markAllRead(int apartmentId, int userId, long readAt);

    // Đếm chưa đọc (dùng cho badge)
    @Query("SELECT COUNT(*) FROM notifications " +
            "WHERE apartment_id = :apartmentId " +
            "AND (target = 'all' OR target_user_id = :userId) " +
            "AND is_read = 0")
    LiveData<Integer> countUnread(int apartmentId, int userId);
}
