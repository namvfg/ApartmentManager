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

@Entity(tableName = "units",
        foreignKeys = @ForeignKey(
                entity = BlockEntity.class,
                parentColumns = "id",
                childColumns = "block_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("block_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "block_id")
    private int blockId;

    private String name;
    private int floor;
}
