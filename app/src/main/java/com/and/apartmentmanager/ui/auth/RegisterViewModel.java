package com.and.apartmentmanager.ui.auth;

import android.app.Application;

import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;

public class RegisterViewModel {
    private final UserRepository userRepository;

    public RegisterViewModel(Application application) {
        this.userRepository = new UserRepository(application);
    }

    public UserEntity register(String name, String email, String phone, String password) {
        return userRepository.registerBlocking(name, email, phone, password);
    }
}

