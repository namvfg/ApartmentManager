package com.and.apartmentmanager.ui.user;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;

public class ProfileViewModel {
    private final UserRepository userRepository;

    public ProfileViewModel(Application application) {
        this.userRepository = new UserRepository(application);
    }

    public LiveData<UserEntity> getUser(int userId) {
        return userRepository.getById(userId);
    }

    public boolean updateProfileBlocking(int userId, String newName, String newPhone) {
        return userRepository.updateProfileBlocking(userId, newName, newPhone);
    }

    public boolean requestDeleteAccountBlocking(int userId) {
        return userRepository.requestDeleteAccountBlocking(userId);
    }
}

