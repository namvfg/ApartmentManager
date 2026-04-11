package com.and.apartmentmanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.AuthOtpManager;
import com.and.apartmentmanager.helper.PasswordVisibilityHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private EditText etReferral;
    private Button btnRegister;
    private TextView tvGotoLogin;
    private android.view.View btnBack;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.btn_back);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        etReferral = findViewById(R.id.et_referral);
        btnRegister = findViewById(R.id.btn_register);
        tvGotoLogin = findViewById(R.id.tv_goto_login);

        userRepository = new UserRepository(getApplication());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        tvGotoLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        PasswordVisibilityHelper.bind(findViewById(R.id.iv_eye_register_1), etPassword);
        PasswordVisibilityHelper.bind(findViewById(R.id.iv_eye_register_2), etPasswordConfirm);
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        new Thread(() -> {
            UserEntity created = userRepository.registerBlocking(name, email, phone, password);
            runOnUiThread(() -> {
                btnRegister.setEnabled(true);
                if (created == null) {
                    Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                // OTP giả lập: logcat
                AuthOtpManager otpManager = AuthOtpManager.getInstance(getApplicationContext());
                otpManager.sendOtp(email);

                startActivity(new Intent(this, OtpVerifyActivity.class));
                finish();
            });
        }).start();
    }
}

