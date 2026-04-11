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
import com.and.apartmentmanager.helper.OtpEmailSender;

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
            if (!ok) {
                runOnUiThread(() -> {
                    btnSendOtp.setEnabled(true);
                    Toast.makeText(this, "Email không hợp lệ hoặc tài khoản bị khóa", Toast.LENGTH_LONG).show();
                });
                return;
            }

            AuthOtpManager.SendResult sentOtp = otpManager.sendOtp(email);
            boolean mailed = OtpEmailSender.sendOtpEmail(
                    sentOtp.email,
                    sentOtp.otp,
                    OtpEmailSender.OtpEmailKind.PASSWORD_RESET);

            runOnUiThread(() -> {
                btnSendOtp.setEnabled(true);
                if (!mailed) {
                    if (!OtpEmailSender.isConfigured()) {
                        Toast.makeText(this,
                                "Chưa cấu hình SMTP: thêm smtp.otp.user và smtp.otp.password vào local.properties (Gmail App Password).",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Không gửi được email. Kiểm tra mạng và App Password Gmail.",
                                Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                Toast.makeText(this, "Đã gửi OTP tới email của bạn.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OtpResetPasswordActivity.class));
                finish();
            });
        }).start();
    }
}

