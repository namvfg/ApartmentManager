package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;

@Dao
public interface InviteCodeDao {
    @Insert
    long insert(InviteCodeEntity inviteCode);

    @Query("SELECT * FROM invite_codes WHERE code = :code LIMIT 1")
    InviteCodeEntity findByCode(String code);

    @Update
    void update(InviteCodeEntity inviteCode);

    @Query("SELECT * FROM invite_codes " +
            "WHERE unit_id = :unitId " +
            "AND is_used = 0 " +
            "AND expires_at > :currentTime " +
            "ORDER BY id DESC LIMIT 1")
    InviteCodeEntity getLatestValidCode(int unitId, long currentTime);
}
