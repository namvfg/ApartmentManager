package com.and.apartmentmanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.AuthOtpManager;
import com.and.apartmentmanager.helper.PasswordVisibilityHelper;

import java.util.regex.Pattern;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText etNewPassword;
    private EditText etNewPasswordConfirm;
    private Button btnSave;
    private android.view.View btnBack;

    private ProgressBar pwStrengthBar;
    private TextView tvStrengthLabel;

    private UserRepository userRepository;
    private AuthOtpManager otpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        btnBack = findViewById(R.id.btn_back);
        etNewPassword = findViewById(R.id.et_new_password);
        etNewPasswordConfirm = findViewById(R.id.et_new_password_confirm);
        btnSave = findViewById(R.id.btn_save_password);

        pwStrengthBar = findViewById(R.id.pw_strength_bar);
        tvStrengthLabel = findViewById(R.id.tv_pw_strength_label);

        userRepository = new UserRepository(getApplication());
        otpManager = AuthOtpManager.getInstance(getApplicationContext());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        PasswordVisibilityHelper.bind(findViewById(R.id.iv_eye_new_password), etNewPassword);
        PasswordVisibilityHelper.bind(findViewById(R.id.iv_eye_new_password_confirm), etNewPasswordConfirm);

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateStrength(s == null ? "" : s.toString());
            }
        });

        btnSave.setOnClickListener(v -> doSave());
        updateStrength("");
    }

    private void doSave() {
        String email = otpManager.getPendingEmail();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Phiên OTP hết hạn. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            return;
        }

        String newPw = etNewPassword.getText().toString().trim();
        String confirm = etNewPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(newPw) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPw.equals(confirm)) {
            Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        new Thread(() -> {
            boolean ok = userRepository.resetPasswordBlocking(email, newPw);
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                if (!ok) {
                    Toast.makeText(this, "Đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpManager.clear();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }).start();
    }

    private void updateStrength(String pw) {
        // Demo đơn giản: chấm điểm theo độ dài + loại ký tự.
        int score = 0;
        int len = pw == null ? 0 : pw.length();
        if (len >= 8) score += 30;
        if (len >= 12) score += 20;

        if (Pattern.compile("[0-9]").matcher(pw).find()) score += 20;
        if (Pattern.compile("[a-z]").matcher(pw).find() && Pattern.compile("[A-Z]").matcher(pw).find()) score += 20;
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(pw).find()) score += 10;

        if (score > 100) score = 100;
        pwStrengthBar.setProgress(score);

        String label;
        if (score < 40) label = "Yếu";
        else if (score < 70) label = "Trung bình";
        else label = "Mạnh";
        tvStrengthLabel.setText(label);
    }
}

