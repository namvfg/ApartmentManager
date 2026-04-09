package com.and.apartmentmanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.AuthOtpManager;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSendOtp;
    private android.view.View btnBack;

    private UserRepository userRepository;
    private AuthOtpManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.et_email);
        btnSendOtp = findViewById(R.id.btn_send_otp);

        userRepository = new UserRepository(getApplication());
        otpManager = AuthOtpManager.getInstance(getApplicationContext());

            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnSendOtp.setOnClickListener(v -> doSendOtp());
    }

    private void doSendOtp() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendOtp.setEnabled(false);
        new Thread(() -> {
            UserEntity user = userRepository.getByEmail(email);
            boolean ok = user != null && user.isActive() && !user.isDeleted();
            runOnUiThread(() -> {
                btnSendOtp.setEnabled(true);
                if (!ok) {
                    Toast.makeText(this, "Email không hợp lệ hoặc tài khoản bị khóa", Toast.LENGTH_LONG).show();
                    return;
                }
                otpManager.sendOtp(email);
                startActivity(new Intent(this, OtpResetPasswordActivity.class));
                finish();
            });
        }).start();
    }
}

