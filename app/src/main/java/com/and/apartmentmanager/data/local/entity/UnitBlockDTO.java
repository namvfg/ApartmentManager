package com.and.apartmentmanager.data.local.entity;

import androidx.room.ColumnInfo;

public class UnitBlockDTO {
    @ColumnInfo(name = "unit_id")
    public int unitId;

    @ColumnInfo(name = "block_name")
    public String blockName;
}