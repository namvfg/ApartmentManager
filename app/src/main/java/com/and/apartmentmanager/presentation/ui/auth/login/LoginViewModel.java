package com.and.apartmentmanager.presentation.ui.auth.login;

import android.app.Application;

import com.and.apartmentmanager.data.repository.UserRepository;

public class LoginViewModel {
    private final UserRepository userRepository;

    public LoginViewModel(Application application) {
        this.userRepository = new UserRepository(application);
    }

    public UserRepository.LoginResult login(String email, String password) {
        return userRepository.loginBlocking(email, password);
    }
}

