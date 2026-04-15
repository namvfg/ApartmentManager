package com.and.apartmentmanager.presentation.ui.auth.password;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.helper.AuthOtpManager;
import com.and.apartmentmanager.helper.OtpEmailSender;

public class OtpResetPasswordActivity extends AppCompatActivity {
    private TextView tvPendingEmail;
    private EditText[] otpEdits = new EditText[6];
    private Button btnConfirm;
    private TextView tvResend;
    private AuthOtpManager otpManager;
    private android.view.View btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_reset);

        btnBack = findViewById(R.id.btn_back);
        tvPendingEmail = findViewById(R.id.tv_pending_email);
        btnConfirm = findViewById(R.id.btn_otp_confirm);
        tvResend = findViewById(R.id.tv_resend);

        otpEdits[0] = findViewById(R.id.otp_1);
        otpEdits[1] = findViewById(R.id.otp_2);
        otpEdits[2] = findViewById(R.id.otp_3);
        otpEdits[3] = findViewById(R.id.otp_4);
        otpEdits[4] = findViewById(R.id.otp_5);
        otpEdits[5] = findViewById(R.id.otp_6);

        otpManager = AuthOtpManager.getInstance(getApplicationContext());

        String email = otpManager.getPendingEmail();
        tvPendingEmail.setText(email);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        for (int i = 0; i < otpEdits.length; i++) {
            final int index = i;
            otpEdits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s == null) return;
                    if (s.length() == 1) {
                        if (index < otpEdits.length - 1) {
                            otpEdits[index + 1].requestFocus();
                        } else {
                            otpEdits[index].clearFocus();
                        }
                    }
                }
            });

            otpEdits[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (TextUtils.isEmpty(otpEdits[index].getText()) && index > 0) {
                        otpEdits[index - 1].requestFocus();
                        otpEdits[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
        otpEdits[0].requestFocus();

        btnConfirm.setOnClickListener(v -> {
            String code = getOtpCode();
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                Toast.makeText(this, "Nhập đủ mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = otpManager.verifyOtp(email, code);
            if (!ok) {
                Toast.makeText(this, "OTP không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(new Intent(this, NewPasswordActivity.class));
            // Không clear ở đây để NewPasswordActivity còn dùng email
        });

        tvResend.setOnClickListener(v -> {
            String pending = otpManager.getPendingEmail();
            if (TextUtils.isEmpty(pending)) {
                Toast.makeText(this, "Không có email chờ xác thực", Toast.LENGTH_SHORT).show();
                return;
            }
            tvResend.setEnabled(false);
            new Thread(() -> {
                AuthOtpManager.SendResult r = otpManager.sendOtp(pending);
                boolean ok = OtpEmailSender.sendOtpEmail(
                        r.email, r.otp, OtpEmailSender.OtpEmailKind.PASSWORD_RESET);
                runOnUiThread(() -> {
                    tvResend.setEnabled(true);
                    for (EditText e : otpEdits) e.setText("");
                    otpEdits[0].requestFocus();
                    if (ok) {
                        Toast.makeText(this, "Đã gửi lại OTP tới email.", Toast.LENGTH_SHORT).show();
                    } else if (!OtpEmailSender.isConfigured()) {
                        Toast.makeText(this,
                                "Chưa cấu hình SMTP trong local.properties. Xem Logcat để lấy mã.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Gửi lại email thất bại. Xem Logcat để lấy mã.", Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });
    }

    private String getOtpCode() {
        StringBuilder sb = new StringBuilder();
        for (EditText e : otpEdits) {
            if (e.getText() == null) return "";
            sb.append(e.getText().toString());
        }
        return sb.toString();
    }
}
