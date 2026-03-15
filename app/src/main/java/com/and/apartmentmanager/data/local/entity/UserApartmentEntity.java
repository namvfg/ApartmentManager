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

@Entity(tableName = "user_apartments",
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE   // xóa user → xóa luôn record này
                ),
                @ForeignKey(
                        entity = ApartmentEntity.class,
                        parentColumns = "id",
                        childColumns = "apartment_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = UnitEntity.class,
                        parentColumns = "id",
                        childColumns = "unit_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("user_id"),        // bắt buộc phải index các cột foreign key
                @Index("apartment_id"),
                @Index("unit_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApartmentEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    private String status;         // "active" / "inactive" / "deleted"

    @ColumnInfo(name = "invite_code_used")
    private String inviteCodeUsed;

    @ColumnInfo(name = "joined_at")
    private long joinedAt;
}