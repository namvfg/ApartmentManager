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

@Entity(tableName = "contracts",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = ApartmentEntity.class, parentColumns = "id", childColumns = "apartment_id", onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("user_id"), @Index("apartment_id"), @Index("unit_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    @ColumnInfo(name = "start_date")
    private long startDate;

    @ColumnInfo(name = "end_date")
    private long endDate;

    @ColumnInfo(name = "billing_day")
    private int billingDay;        // 1–28

    @ColumnInfo(name = "rent_price")
    private double rentPrice;      // cố định sau khi ký

    private String status;         // "active" / "expired" / "terminated"

    @ColumnInfo(name = "contract_url")
    private String contractUrl;

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "terminated_at")
    private Long terminatedAt;

    @ColumnInfo(name = "terminated_by")
    private Integer terminatedBy;

    @ColumnInfo(name = "terminate_reason")
    private String terminateReason;

    @ColumnInfo(name = "created_at")
    private long createdAt;
}
