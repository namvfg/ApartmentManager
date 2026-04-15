package com.and.apartmentmanager.presentation.ui.user.statistic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.databinding.FragmentContractDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ContractDetailFragment extends Fragment {

    private FragmentContractDetailBinding binding;
    private String contractUrl = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentContractDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {

            UserEntity user = db.userDao().getByIdSync(3);
            ContractEntity contract = db.contractDao().getContract(3, 1, 1);

            if (contract == null) return;

            contractUrl = contract.getContractUrl();

            UnitEntity unit = db.unitDao().getById(contract.getUnitId());
            BlockEntity block = unit != null ? db.blockDao().getById(unit.getBlockId()) : null;

            requireActivity().runOnUiThread(() -> {

                if (!isAdded()) return;

                // OWNER
                binding.tvOwner.setText("Chủ hợp đồng    " +
                        (user != null ? user.getName() : "Không có"));

                // DATE
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.tvStartDate.setText(sdf.format(new Date(contract.getStartDate())));
                binding.tvEndDate.setText(sdf.format(new Date(contract.getEndDate())));

                // REMAIN
                long diff = contract.getEndDate() - System.currentTimeMillis();
                long days = TimeUnit.MILLISECONDS.toDays(diff);

                if (days <= 0) {
                    binding.tvRemainDays.setText("Hết hạn");
                    binding.tvStatus.setText("Đã hết hạn ❌");
                } else {
                    binding.tvRemainDays.setText(days + " ngày");
                    binding.tvStatus.setText("Đang hiệu lực ✓");
                }

                // PRICE
                binding.tvRentPrice.setText("Tiền thuê: " +
                        String.format("%,.0fđ/tháng", contract.getRentPrice()));

                // ROOM
                if (block != null && unit != null) {
                    binding.tvRoom.setText("Phòng " + block.getName() + " - " + unit.getName());
                } else {
                    binding.tvRoom.setText("Không có dữ liệu");
                }

                // PDF
                binding.btnViewContract.setOnClickListener(v -> {

                    if (contractUrl == null || contractUrl.isEmpty()) {
                        Toast.makeText(getContext(), "Không có file hợp đồng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(contractUrl), "application/pdf");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Không mở được PDF", Toast.LENGTH_SHORT).show();
                    }
                });

            });

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}