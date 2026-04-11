package com.and.apartmentmanager.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.PasswordVisibilityHelper;
import com.and.apartmentmanager.helper.SessionManager;

public class ChangePasswordFragment extends Fragment {
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etNewPasswordConfirm;
    private Button btnSavePassword;

    private UserRepository userRepository;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sm = SessionManager.getInstance(requireContext());
        long userIdLong = sm.getUserId();
        if (userIdLong == -1) {
            requireActivity().finish();
            return;
        }

        userId = (int) userIdLong;
        userRepository = new UserRepository(requireActivity().getApplication());

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        etOldPassword = view.findViewById(R.id.et_old_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etNewPasswordConfirm = view.findViewById(R.id.et_new_password_confirm);
        btnSavePassword = view.findViewById(R.id.btn_save_password);

        PasswordVisibilityHelper.bind(view.findViewById(R.id.iv_eye_change_old), etOldPassword);
        PasswordVisibilityHelper.bind(view.findViewById(R.id.iv_eye_change_new), etNewPassword);
        PasswordVisibilityHelper.bind(view.findViewById(R.id.iv_eye_change_confirm), etNewPasswordConfirm);

        btnSavePassword.setOnClickListener(v -> onSavePassword());
    }

    private void onSavePassword() {
        String oldPw = etOldPassword.getText().toString().trim();
        String newPw = etNewPassword.getText().toString().trim();
        String confirmPw = etNewPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(oldPw) || TextUtils.isEmpty(newPw) || TextUtils.isEmpty(confirmPw)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPw.equals(confirmPw)) {
            Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSavePassword.setEnabled(false);
        new Thread(() -> {
            boolean ok = userRepository.changePasswordBlocking(userId, oldPw, newPw);
            requireActivity().runOnUiThread(() -> {
                btnSavePassword.setEnabled(true);
                if (ok) {
                    Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etNewPasswordConfirm.setText("");
                } else {
                    Toast.makeText(requireContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}

