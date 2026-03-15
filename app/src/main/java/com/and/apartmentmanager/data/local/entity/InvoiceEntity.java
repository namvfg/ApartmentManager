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

@Entity(tableName = "invoices",
        foreignKeys = {
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id", onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = ApartmentEntity.class, parentColumns = "id", childColumns = "apartment_id", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("unit_id"), @Index("apartment_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unit_id")
    private int unitId;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    private String month;          // format: "2024-01"

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    private String status;         // "draft/confirmed/paid/overdue/adjusted"

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "confirmed_at")
    private Long confirmedAt;

    @ColumnInfo(name = "confirmed_by")
    private Integer confirmedBy;

    private String note;
}
