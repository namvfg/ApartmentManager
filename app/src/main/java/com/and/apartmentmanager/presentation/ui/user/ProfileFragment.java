package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;

import androidx.lifecycle.Observer;

public class ProfileFragment extends Fragment {
    private TextView tvAvatar;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvRole;

    private View btnEditProfile;
    private View btnChangePassword;
    private View btnNotificationSettings;
    private View btnLogout;
    private Button btnRequestDelete;

    private UserEntity currentUser;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAvatar = view.findViewById(R.id.tv_profile_avatar);
        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvRole = view.findViewById(R.id.tv_profile_role);

        btnEditProfile = view.findViewById(R.id.btn_item_edit_profile);
        btnChangePassword = view.findViewById(R.id.btn_item_change_password);
        btnNotificationSettings = view.findViewById(R.id.btn_item_notification_settings);
        btnLogout = view.findViewById(R.id.btn_item_logout);
        btnRequestDelete = view.findViewById(R.id.btn_request_delete);

        SessionManager sm = SessionManager.getInstance(requireContext());
        long userIdLong = sm.getUserId();
        if (userIdLong == -1) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }

        int userId = (int) userIdLong;
        userRepository = new UserRepository(requireActivity().getApplication());

        userRepository.getById(userId).observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user == null) return;
                currentUser = user;
                renderUser(user);
            }
        });

        btnEditProfile.setOnClickListener(v -> ((UserMainActivity) requireActivity()).showProfileDetail());
        btnChangePassword.setOnClickListener(v -> ((UserMainActivity) requireActivity()).showChangePassword());
        btnNotificationSettings.setOnClickListener(v -> ((UserMainActivity) requireActivity()).showSettings());
        btnLogout.setOnClickListener(v -> doLogout());
        btnRequestDelete.setOnClickListener(v -> requestDeleteAccount());
    }

    private void renderUser(UserEntity user) {
        tvName.setText(user.getName() == null ? "" : user.getName());
        tvEmail.setText(user.getEmail() == null ? "" : user.getEmail());

        String role = user.getRole() == null ? "" : user.getRole();
        if ("admin".equals(role)) tvRole.setText("Admin");
        else tvRole.setText("Cư dân");

        String name = user.getName();
        String initials = "NA";
        if (!TextUtils.isEmpty(name)) {
            String trimmed = name.trim();
            initials = trimmed.substring(0, Math.min(2, trimmed.length())).toUpperCase();
        }
        tvAvatar.setText(initials);
    }

    private void doLogout() {
        SessionManager sm = SessionManager.getInstance(requireContext());
        sm.clear();
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }

    private void requestDeleteAccount() {
        if (currentUser == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tài khoản")
                .setMessage("Chỉ có thể yêu cầu xóa nếu bạn KHÔNG có hợp đồng active.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    int userId = (int) currentUser.getId();
                    new Thread(() -> {
                        boolean ok = userRepository.requestDeleteAccountBlocking(userId);
                        requireActivity().runOnUiThread(() -> {
                            if (!ok) {
                                Toast.makeText(requireContext(), "Không thể xóa do còn hợp đồng active", Toast.LENGTH_LONG).show();
                                return;
                            }
                            SessionManager.getInstance(requireContext()).clear();
                            startActivity(new Intent(requireContext(), LoginActivity.class));
                            requireActivity().finish();
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

