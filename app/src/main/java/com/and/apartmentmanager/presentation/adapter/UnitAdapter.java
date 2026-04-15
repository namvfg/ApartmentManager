package com.and.apartmentmanager.presentation.adapter;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.presentation.ui.admin.apartment.InviteActivity;
import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.ui.user.UserActivity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.repository.ContractRepository;
import com.and.apartmentmanager.data.repository.UserApartmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {

    private static final ExecutorService UNIT_STYLE_EXECUTOR = Executors.newFixedThreadPool(4);



    List<UnitEntity> list = new ArrayList<>();
    int apartmentId;
    private final ContractRepository contractRepository;
    private final UserApartmentRepository userApartmentRepository;
    int adminId;

    public UnitAdapter(Application application, int apartmentId, int adminId) {
        this.contractRepository = new ContractRepository(application);
        this.userApartmentRepository = new UserApartmentRepository(application);
        this.apartmentId = apartmentId;
        this.adminId = adminId;
    }

    public void setData(List<UnitEntity> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        UnitEntity unit = list.get(position);
        h.tvUnit.setText(unit.getName());

        h.itemView.setOnClickListener(v -> {
            String[] options = {"Xem cư dân", " Mã mời"};
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Phòng " + unit.getName())
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(v.getContext(), UserActivity.class);
                            intent.putExtra("unitId", unit.getId());
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), InviteActivity.class);
                            intent.putExtra("unitId", unit.getId());
                            intent.putExtra("apartmentId", apartmentId);
                            intent.putExtra("adminId", 1); // fix sau
                            v.getContext().startActivity(intent);
                        }
                    })
                    .show();
        });

        int unitId = unit.getId();
        UNIT_STYLE_EXECUTOR.execute(() -> {
            int residents = userApartmentRepository.countActiveResidentsByUnit(unitId);
            List<ContractEntity> contracts = contractRepository.getByUnitSync(unitId);
            boolean expiring = false;
            long now = System.currentTimeMillis();
            if (residents > 0) {
                for (ContractEntity c : contracts) {
                    if (!"active".equals(c.getStatus())) continue;
                    if (userApartmentRepository.countActiveUserInUnit(unitId, c.getUserId()) == 0) {
                        continue;
                    }
                    long days = (c.getEndDate() - now) / (1000L * 60 * 60 * 24);
                    if (days >= 0 && days < 30) {
                        expiring = true;
                        break;
                    }
                }
            }

            final int r = residents;
            final boolean ex = expiring;
            h.itemView.post(() -> {
                if (r == 0) {
                    h.tvUnit.setBackgroundColor(Color.parseColor("#F0F0F0"));
                    h.tvUnit.setTextColor(Color.parseColor("#757575"));
                } else if (ex) {
                    h.tvUnit.setBackgroundColor(Color.parseColor("#FFF3CD"));
                    h.tvUnit.setTextColor(Color.parseColor("#856404"));
                } else {
                    h.tvUnit.setBackgroundColor(Color.parseColor("#D1E7DD"));
                    h.tvUnit.setTextColor(Color.parseColor("#0F5132"));
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUnit = itemView.findViewById(R.id.tvUnit);
        }
    }
}
