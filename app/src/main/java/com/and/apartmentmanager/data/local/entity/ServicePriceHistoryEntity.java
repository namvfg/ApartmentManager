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

@Entity(tableName = "service_price_history",
        foreignKeys = {
                @ForeignKey(entity = ServiceEntity.class, parentColumns = "id", childColumns = "service_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "changed_by", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("service_id"), @Index("changed_by")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePriceHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "service_id")
    private int serviceId;

    private double price;

    @ColumnInfo(name = "effective_from")
    private long effectiveFrom;

    @ColumnInfo(name = "apply_from_next_cycle")
    private boolean applyFromNextCycle;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "changed_by")
    private int changedBy;

    @ColumnInfo(name = "changed_at")
    private long changedAt;
}