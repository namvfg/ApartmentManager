package com.and.apartmentmanager.presentation.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.databinding.FragmentWorkspaceBinding;
import com.and.apartmentmanager.presentation.adapter.ServiceRowAdapter;

public class WorkspaceFragment extends Fragment {

    private FragmentWorkspaceBinding binding;

    private static final String ARG_APARTMENT_ID = "apartment_id";
    private static final String ARG_UNIT_ID      = "unit_id";

    public static WorkspaceFragment newInstance(long apartmentId, long unitId) {
        WorkspaceFragment f = new WorkspaceFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_APARTMENT_ID, apartmentId);
        args.putLong(ARG_UNIT_ID, unitId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkspaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        long apartmentId = getArguments() != null ? getArguments().getLong(ARG_APARTMENT_ID) : -1;
        long unitId      = getArguments() != null ? getArguments().getLong(ARG_UNIT_ID) : -1;

        // Header info — TODO: load từ DB
        binding.textApartmentName.setText("Chung Cư Xanh ABC");
        binding.textUnitInfo.setText("Block A · P.302");
        binding.textContractDays.setText("Còn 45 ngày");
        binding.textInvoiceAmount.setText("3.250.000đ");
        binding.textInvoiceMonth.setText("Tháng 3/2026");
        binding.textInvoiceDue.setText("Hạn: 15/03/2026");
        binding.textInvoiceTotal.setText("3.250.000đ");

        // Notification bell
        binding.btnNotification.setOnClickListener(v -> {
            // Navigate sang NotificationListFragment
        });

        // Thanh toán
        binding.btnPayNow.setOnClickListener(v -> {
            // Navigate sang PaymentFragment
        });

        // Services RecyclerView — TODO: load từ DB
        ServiceRowAdapter serviceAdapter = new ServiceRowAdapter();
        binding.recyclerServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerServices.setAdapter(serviceAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}