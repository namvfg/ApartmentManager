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

@Entity(tableName = "utility_readings",
        foreignKeys = {
                @ForeignKey(entity = ServiceEntity.class, parentColumns = "id", childColumns = "service_id", onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("service_id"), @Index("unit_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilityReadingEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "service_id")
    private int serviceId;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    private String month;          // format: "2024-01"

    @ColumnInfo(name = "previous_reading")
    private double previousReading;

    @ColumnInfo(name = "current_reading")
    private double currentReading;

    private double consumption;    // = current - previous, tự tính

    @ColumnInfo(name = "recorded_by")
    private int recordedBy;

    @ColumnInfo(name = "recorded_at")
    private long recordedAt;
}
