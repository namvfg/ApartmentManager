package com.and.apartmentmanager.presentation.adapter;

import android.content.Intent;
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
import com.and.apartmentmanager.data.local.entity.UnitEntity;

import java.util.ArrayList;
import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {

     List<UnitEntity> list = new ArrayList<>();

    int apartmentId;

    public UnitAdapter(int apartmentId) {
        this.apartmentId = apartmentId;
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
                            // xem cư dân của unit
                            Intent intent = new Intent(v.getContext(), UserActivity.class);
                            intent.putExtra("unitId", unit.getId());
                            v.getContext().startActivity(intent);

                        } else {
                            // mã mời
                            Intent intent = new Intent(v.getContext(), InviteActivity.class);
                            intent.putExtra("unitId", unit.getId());
                            intent.putExtra("apartmentId", apartmentId);
                            intent.putExtra("adminId", 1); // fix sau
                            v.getContext().startActivity(intent);
                        }

                    })
                    .show();
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