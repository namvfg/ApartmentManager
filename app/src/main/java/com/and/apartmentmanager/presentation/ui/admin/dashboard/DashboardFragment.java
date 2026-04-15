package com.and.apartmentmanager.presentation.ui.admin.dashboard;

import android.content.Intent;
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
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.UnitDao;
import com.and.apartmentmanager.databinding.FragmentDashboardBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.ui.admin.AdminProfileFragment;
import com.and.apartmentmanager.presentation.ui.admin.chat.ChatListFragment;
import com.and.apartmentmanager.presentation.ui.auth.login.LoginActivity;

public class DashboardFragment extends Fragment {

    private DashboardViewModel mViewModel;
    private FragmentDashboardBinding binding;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         AppDatabase db = AppDatabase.getInstance(requireContext());
         UnitDao unitDao = db.unitDao();
         ContractDao contractDao = db.contractDao();
         InvoiceDao invoiceDao = db.invoiceDao();

        // 2. Tạo Factory và truyền các DAO vào
        DashboardViewModelFactory factory = new DashboardViewModelFactory(unitDao, contractDao, invoiceDao);

        // 3. Khởi tạo ViewModel kèm theo Factory
        mViewModel = new ViewModelProvider(this, factory).get(DashboardViewModel.class);
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

        mViewModel.totalUnits.observe(getViewLifecycleOwner(),
                value -> binding.textTotalUnits.setText(String.valueOf(value)));

        mViewModel.occupiedUnits.observe(getViewLifecycleOwner(),
                value -> binding.textOccupiedUnits.setText(String.valueOf(value)));

        mViewModel.expiringContracts.observe(getViewLifecycleOwner(),
                value -> binding.textExpiringContracts.setText(String.valueOf(value)));

        mViewModel.draftInvoices.observe(getViewLifecycleOwner(),
                value -> binding.textPendingInvoicesCount.setText(value + " hóa đơn chờ xác nhận"));

        mViewModel.overdueInvoices.observe(getViewLifecycleOwner(),
                value -> binding.textOverdueCount.setText(value + " hóa đơn quá hạn"));

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

                // Thực hiện chuyển Fragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        // Thay R.id.fragment_container bằng ID của FrameLayout/FragmentContainerView trong layout Activity của bạn
                        .replace(R.id.fragment_container, new AdminProfileFragment())
                        .addToBackStack(null) // Lưu vào lịch sử để bấm nút Back trên điện thoại có thể quay lại
                        .commit();
            });

            popupView.findViewById(R.id.itemChat).setOnClickListener(v2 -> {
                popup.dismiss();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        // Nhớ dùng đúng ID fragment_container của bạn nhé
                        .replace(R.id.fragment_container, ChatListFragment.newInstance())
                        .addToBackStack(null) // Để người dùng có thể Back lại Dashboard
                        .commit();
            });

            popupView.findViewById(R.id.itemLogout).setOnClickListener(v2 -> {
                popup.dismiss();
                SessionManager.getInstance(requireContext()).clear();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}