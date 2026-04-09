package com.and.apartmentmanager.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserEntity> list = new ArrayList<>();
    private AppDatabase db;
    private LifecycleOwner lifecycleOwner;
    int unitId;
    int apartmentId;
    public UserAdapter(AppDatabase db, LifecycleOwner lifecycleOwner,int unitId, int apartmentId) {
        this.db = db;
        this.lifecycleOwner = lifecycleOwner;
        this.unitId = unitId;
        this.apartmentId = apartmentId;
    }

    public void setData(List<UserEntity> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        UserEntity user = list.get(position);

        h.tvName.setText(user.getName());

        Executors.newSingleThreadExecutor().execute(() -> {

            String unitName = db.userApartmentDao().getUnitNameByUserId(user.getId());
            String role = db.userApartmentDao().getRoleByUserId(user.getId());

            h.itemView.post(() -> {
                h.tvInfo.setText("Phòng " + unitName + " - " + role);
            });
        });

        if (user.isActive()) {
            h.tvStatus.setText("Active");
            h.tvStatus.setTextColor(Color.GREEN);
        }
        else {
            h.tvStatus.setText("Inactive");
            h.tvStatus.setTextColor(Color.RED);
        }

        h.btnDelete.setOnClickListener(v -> {
            int userId = user.getId();
            Executors.newSingleThreadExecutor().execute(() -> {

                if (unitId != -1) {
                    db.userApartmentDao().deleteUserFromUnit(userId, unitId);
                } else {
                    db.userApartmentDao().deleteUserFromApartment(userId, apartmentId);
                }

            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvStatus;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}