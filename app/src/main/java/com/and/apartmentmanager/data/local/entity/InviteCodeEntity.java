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

@Entity(tableName = "invite_codes",
        foreignKeys = {
                @ForeignKey(entity = ApartmentEntity.class, parentColumns = "id", childColumns = "apartment_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "admin_id", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("apartment_id"), @Index("unit_id"), @Index("admin_id")}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCodeEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    @ColumnInfo(name = "admin_id")
    private int adminId;

    private String code;

    @ColumnInfo(name = "expires_at")
    private long expiresAt;

    @ColumnInfo(name = "is_used")
    private boolean isUsed;

    @ColumnInfo(name = "used_by")
    private Integer usedBy;        // nullable
}