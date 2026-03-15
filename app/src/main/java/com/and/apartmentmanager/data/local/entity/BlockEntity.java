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

@Entity(tableName = "blocks",
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
public class BlockEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "apartment_id")
    private int apartmentId;

    private String name;
}