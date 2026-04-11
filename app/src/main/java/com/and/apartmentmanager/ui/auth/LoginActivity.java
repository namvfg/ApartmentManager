package com.and.apartmentmanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.PasswordVisibilityHelper;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.ui.admin.AdminMainActivity;
import com.and.apartmentmanager.ui.user.UserMainActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvForgot;
    private TextView tvRegister;
    private android.view.View btnBack;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgot = findViewById(R.id.tv_forgot);
        tvRegister = findViewById(R.id.tv_register);

        userRepository = new UserRepository(getApplication());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        tvForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        ImageView ivEye = findViewById(R.id.iv_eye);
        PasswordVisibilityHelper.bind(ivEye, etPassword);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        new Thread(() -> {
            UserRepository.LoginResult result = userRepository.loginBlocking(email, password);
            runOnUiThread(() -> {
                btnLogin.setEnabled(true);
                if (result == null) {
                    Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    return;
                }

                SessionManager sm = SessionManager.getInstance(getApplicationContext());
                sm.saveSession(result.userId, result.role, result.apartmentId);

                if ("admin".equals(result.role)) {
                    startActivity(new Intent(this, AdminMainActivity.class));
                } else {
                    startActivity(new Intent(this, UserMainActivity.class));
                }
                finish();
            });
        }).start();
    }
}

