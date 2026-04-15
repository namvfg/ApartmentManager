package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ServiceEntity;
import com.and.apartmentmanager.presentation.ui.admin.UpdatePriceElectricActivity;
import com.and.apartmentmanager.presentation.ui.admin.UpdatePriceWaterActivity;

import java.util.List;

public class ServiceFragment extends Fragment {

    private LinearLayout layoutFixed, layoutVariable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutFixed = view.findViewById(R.id.layoutFixed);
        layoutVariable = view.findViewById(R.id.layoutVariable);

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            List<ServiceEntity> all = db.serviceDao().getAll();
            Log.d("CHECK_DB", "all size = " + all.size());

            List<ServiceEntity> services = db.serviceDao().getServicesByApartment(1);
            Log.d("SERVICE", "size = " + services.size());

            requireActivity().runOnUiThread(() -> {

                for (ServiceEntity s : services) {

                    View item = getLayoutInflater().inflate(R.layout.item_service, layoutFixed, false);

                    TextView tvName = item.findViewById(R.id.tvName);
                    TextView tvDesc = item.findViewById(R.id.tvDesc);
                    TextView tvTag = item.findViewById(R.id.tvTag);

                    tvName.setText(s.getName());
                    tvDesc.setText(s.getDescription());

                    String name = s.getName().toLowerCase();
                    String pricing;

                    // ===== LOGIC PHÂN LOẠI =====
                    if (name.contains("điện") || name.contains("nước")) {
                        pricing = "Biến đổi";
                        layoutVariable.addView(item); // ⚠️ sửa đúng chỗ
                    } else {
                        pricing = "Cố định";
                        layoutFixed.addView(item);
                    }

                    tvTag.setText(pricing);

                    // ===== CLICK =====
                    item.setOnClickListener(v -> {

                        String nameLower = s.getName().toLowerCase().trim();

                        if (nameLower.contains("điện")) {
                            Intent intent = new Intent(getContext(), UpdatePriceElectricActivity.class);
                            intent.putExtra("service_name", s.getName());
                            startActivity(intent);
                        }

                        else if (nameLower.contains("nước")) {
                            Intent intent = new Intent(getContext(), UpdatePriceWaterActivity.class);
                            intent.putExtra("service_name", s.getName());
                            startActivity(intent);
                        }
                    });
                }
            });

        }).start();
    }
}