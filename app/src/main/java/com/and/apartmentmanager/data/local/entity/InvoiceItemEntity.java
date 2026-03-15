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

@Entity(tableName = "invoice_items",
        foreignKeys = {
                @ForeignKey(entity = InvoiceEntity.class,  parentColumns = "id", childColumns = "invoice_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ServiceEntity.class,  parentColumns = "id", childColumns = "service_id", onDelete = ForeignKey.RESTRICT)
        },
        indices = {@Index("invoice_id"), @Index("service_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "invoice_id")
    private int invoiceId;

    @ColumnInfo(name = "service_id")
    private Integer serviceId;

    @ColumnInfo(name = "service_name")
    private String serviceName;    // snapshot tên dịch vụ

    private double price;          // snapshot giá lúc tạo

    private Double consumption;    // null nếu fixed

    private double total;

    private String type;           // "rent" / "fixed" / "variable"
}
