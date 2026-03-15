package com.and.apartmentmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;

@Dao
public interface InviteCodeDao {
    @Insert
    long insert(InviteCodeEntity inviteCode);
}
