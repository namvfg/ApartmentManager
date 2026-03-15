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
    private String type;           // "manual" / "auto"
    private String target;         // "all" / "user"

    @ColumnInfo(name = "target_user_id")
    private Integer targetUserId;  // nullable nếu target = "all"

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "created_at")
    private long createdAt;
}
