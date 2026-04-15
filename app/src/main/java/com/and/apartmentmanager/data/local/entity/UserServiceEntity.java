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

@Entity(tableName = "user_services",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "user_id"),
                @ForeignKey(entity = ServiceEntity.class, parentColumns = "id", childColumns = "service_id"),
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id")
        },
        indices = {@Index("user_id"), @Index("service_id"), @Index("unit_id")}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserServiceEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "service_id")
    private int serviceId;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    @ColumnInfo(name = "created_at")
    private long createdAt;
}