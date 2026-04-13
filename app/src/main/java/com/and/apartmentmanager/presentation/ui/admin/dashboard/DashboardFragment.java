package com.and.apartmentmanager.presentation.ui.admin.dashboard;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private DashboardViewModel mViewModel;
    private FragmentDashboardBinding binding;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.avatarAdmin.setOnClickListener(v -> {
            View popupView = getLayoutInflater().inflate(R.layout.popup_profile, null);

            PopupWindow popup = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true // focusable — bấm ngoài tự đóng
            );

            // Bo góc + shadow
            popup.setElevation(16f);
            popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Hiện ngay dưới avatar, căn phải
            popup.showAsDropDown(v, 0, 8);

            popupView.findViewById(R.id.itemPersonalInfo).setOnClickListener(v2 -> {
                popup.dismiss();
            });

            popupView.findViewById(R.id.itemChangePassword).setOnClickListener(v2 -> {
                popup.dismiss();
            });

//            popupView.findViewById(R.id.itemLogout).setOnClickListener(v2 -> {
//                popup.dismiss();
//                SessionManager.getInstance(requireContext()).clear();
//                startActivity(new Intent(requireActivity(), LoginActivity.class));
//                requireActivity().finish();
//            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}