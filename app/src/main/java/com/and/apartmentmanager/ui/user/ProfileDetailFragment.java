package com.and.apartmentmanager.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.helper.UserAvatarManager;

public class ProfileDetailFragment extends Fragment {

    private TextView tvAvatarBig;
    private EditText etName;
    private EditText etPhone;
    private TextView tvEmailReadonly;
    private Button btnSave;

    private UserRepository userRepository;
    private UserEntity currentUser;
    private long userId;
    private int avatarColorIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAvatarBig = view.findViewById(R.id.tv_avatar_big);
        etName = view.findViewById(R.id.et_name);
        etPhone = view.findViewById(R.id.et_phone);
        tvEmailReadonly = view.findViewById(R.id.tv_email_readonly);
        btnSave = view.findViewById(R.id.btn_save);

        View btnBack = view.findViewById(R.id.btn_back);

        SessionManager sm = SessionManager.getInstance(requireContext());
        userId = sm.getUserId();
        if (userId == -1) {
            requireActivity().finish();
            return;
        }

        userRepository = new UserRepository(requireActivity().getApplication());

        avatarColorIndex = UserAvatarManager.getColorIndex(requireContext(), userId, 0);
        applyAvatarStyle();

        // Load user hiện tại
        new Thread(() -> {
            UserEntity user = userRepository.getByIdBlocking((int) userId);
            currentUser = user;
            if (user == null) return;
            requireActivity().runOnUiThread(() -> bindUser(user));
        }).start();

        btnSave.setOnClickListener(v -> onSave());
        tvAvatarBig.setOnClickListener(v -> {
            avatarColorIndex = (avatarColorIndex + 1) % 4;
            UserAvatarManager.saveColorIndex(requireContext(), userId, avatarColorIndex);
            applyAvatarStyle();
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
    }

    private void bindUser(UserEntity user) {
        String name = user.getName() == null ? "" : user.getName();
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();

        etName.setText(name);
        etPhone.setText(phone);
        tvEmailReadonly.setText(email);

        String initials = "NA";
        if (!TextUtils.isEmpty(name)) {
            String trimmed = name.trim();
            initials = trimmed.substring(0, Math.min(2, trimmed.length())).toUpperCase();
        }
        tvAvatarBig.setText(initials);
    }

    private void onSave() {
        if (currentUser == null) return;

        String newName = etName.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPhone)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        new Thread(() -> {
            boolean ok = userRepository.updateProfileBlocking((int) userId, newName, newPhone);
            requireActivity().runOnUiThread(() -> {
                btnSave.setEnabled(true);
                if (ok) {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void applyAvatarStyle() {
        int colorRes;
        switch (avatarColorIndex) {
            case 1:
                colorRes = R.color.pl;
                break;
            case 2:
                colorRes = R.color.pp;
                break;
            case 3:
                colorRes = R.color.warn;
                break;
            default:
                colorRes = R.color.pp;
                break;
        }
        tvAvatarBig.setBackgroundResource(colorRes);
    }
}

