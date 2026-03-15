package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.UtilityReadingEntity;

@Dao
public interface UtilityReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UtilityReadingEntity reading);

    @Update
    void update(UtilityReadingEntity reading);
}
