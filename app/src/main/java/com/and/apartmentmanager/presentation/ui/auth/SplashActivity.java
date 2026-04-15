package com.and.apartmentmanager.presentation.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.admin.AdminMainActivity;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;
import com.and.apartmentmanager.presentation.ui.user.UserMainActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Mở DB để trigger seed nếu lần đầu cài đặt.
        UserRepository userRepository = new UserRepository(getApplication());
        // Khởi tạo DB và chạy seed
        new Thread(() -> {
            int count = userRepository.count();
            Log.d("DB_TEST", "count = " + count);
        }).start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            SessionManager sm = SessionManager.getInstance(getApplicationContext());
            if (!sm.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            String role = sm.getRole();
            if ("admin".equals(role)) {
                startActivity(new Intent(this, AdminMainActivity.class));
            } else {
                startActivity(new Intent(this, UserMainActivity.class));
            }
            finish();
        }, 900);
    }
}
