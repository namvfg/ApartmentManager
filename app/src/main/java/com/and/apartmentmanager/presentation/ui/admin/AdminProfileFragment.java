package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;

public class AdminProfileFragment extends Fragment {

    private TextView tvName;
    private TextView tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);

        SessionManager sm = SessionManager.getInstance(requireContext());
        long userId = sm.getUserId();
        if (userId != -1) {
            UserRepository repo = new UserRepository(requireActivity().getApplication());
            repo.getById((int) userId).observe(getViewLifecycleOwner(), user -> {
                if (user == null) return;
                bindHeader(user);
            });
        }

        View rowManageApartment = view.findViewById(R.id.row_manage_apartment);
        View rowManageAdmin = view.findViewById(R.id.row_manage_admin);
        View rowReport = view.findViewById(R.id.row_report);
        View rowChangePassword = view.findViewById(R.id.row_change_password);
        View rowLogout = view.findViewById(R.id.row_logout);

        if (rowManageApartment != null) {
            rowManageApartment.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "(P1) Quản lý chung cư: sẽ làm ở module khác", Toast.LENGTH_SHORT).show()
            );
        }
        if (rowManageAdmin != null) {
            rowManageAdmin.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "(P1) Quản lý Admin: sẽ làm ở module khác", Toast.LENGTH_SHORT).show()
            );
        }
        if (rowReport != null) {
            rowReport.setOnClickListener(v -> ((AdminMainActivity) requireActivity()).showStatistic());
        }
        if (rowChangePassword != null) {
            rowChangePassword.setOnClickListener(v -> ((AdminMainActivity) requireActivity()).showChangePassword());
        }
        if (rowLogout != null) {
            rowLogout.setOnClickListener(v -> confirmLogout());
        }
    }

    private void bindHeader(UserEntity user) {
        if (tvName != null) {
            String name = user.getName() == null ? "" : user.getName();
            tvName.setText(name.isEmpty() ? "Super Admin" : name);
        }
        if (tvEmail != null) {
            tvEmail.setText(user.getEmail() == null ? "" : user.getEmail());
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (d, w) -> {
                    SessionManager.getInstance(requireContext()).clear();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

