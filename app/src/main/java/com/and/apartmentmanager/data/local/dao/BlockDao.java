package com.and.apartmentmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.BlockEntity;

import java.util.List;

@Dao
public interface BlockDao {
    @Insert
    long insert(BlockEntity blockEntity);

    @Update
    void update(BlockEntity block);

    @Delete
    void delete(BlockEntity block); // Repository validate trước khi gọi

    @Query("SELECT * FROM blocks WHERE apartment_id = :apartmentId")
    LiveData<List<BlockEntity>> getByApartment(int apartmentId);

    @Query("SELECT * FROM blocks WHERE id = :id LIMIT 1")
    BlockEntity getByIdSync(int id);

    @Query("SELECT COUNT(*) FROM units WHERE block_id = :blockId")
    int countUnits(long blockId);

    @Query("SELECT * FROM blocks WHERE id = :id LIMIT 1")
    BlockEntity getById(int id);
    int countUnits(int blockId);
}
