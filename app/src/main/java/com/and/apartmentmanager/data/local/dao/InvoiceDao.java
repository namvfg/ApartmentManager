package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.InvoiceEntity;

@Dao
public interface InvoiceDao {
    @Insert
    long insert(InvoiceEntity invoice);

    @Update
    void update(InvoiceEntity invoice);
}
