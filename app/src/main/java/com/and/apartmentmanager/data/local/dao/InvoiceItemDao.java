package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.InvoiceItemEntity;

import java.util.List;

@Dao
public interface InvoiceItemDao {
    @Insert
    long insert(InvoiceItemEntity item);

    @Insert
    void insertAll(List<InvoiceItemEntity> items);

    @Update
    void update(InvoiceItemEntity item);
}
