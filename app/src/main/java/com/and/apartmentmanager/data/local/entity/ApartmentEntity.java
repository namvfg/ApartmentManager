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

@Entity(tableName = "apartments",
        foreignKeys = @ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "admin_id",
                onDelete = ForeignKey.RESTRICT   // không xóa user nếu còn là admin
        ),
        indices = @Index("admin_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String address;

    @ColumnInfo(name = "is_active", defaultValue = "1")
    private boolean isActive;

    @ColumnInfo(name = "admin_id")
    private int adminId;
}
