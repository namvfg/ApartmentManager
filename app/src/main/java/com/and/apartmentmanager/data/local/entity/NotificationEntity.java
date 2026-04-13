package com.and.apartmentmanager.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "notifications",
        foreignKeys = @ForeignKey(
                entity = ApartmentEntity.class,
                parentColumns = "id",
                childColumns = "apartment_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("apartment_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    private String title;
    private String content;
    private String type;
    private String target;

    @ColumnInfo(name = "target_user_id")
    private Integer targetUserId;

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // ── Thêm mới ──
    @ColumnInfo(name = "is_read", defaultValue = "0")
    private boolean isRead;

    @ColumnInfo(name = "read_at")
    private Long readAt; // null nếu chưa đọc
}
