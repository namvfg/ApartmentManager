package com.and.apartmentmanager.Adapter;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.UserDetailActivity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserApartmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserEntity> list = new ArrayList<>();
    private final UserApartmentRepository userApartmentRepository;
    int unitId;
    int apartmentId;

    public UserAdapter(Application application, int unitId, int apartmentId) {
        this.userApartmentRepository = new UserApartmentRepository(application);
        this.unitId = unitId;
        this.apartmentId = apartmentId;
    }

    public UserAdapter(Application application) {
        this.userApartmentRepository = new UserApartmentRepository(application);
        this.unitId = -1;
        this.apartmentId = -1;
    }

    public interface OnItemClickListener {
        void onClick(UserEntity user);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
            String unitName = userApartmentRepository.getUnitNameByUserId(user.getId());
            String role = userApartmentRepository.getRoleByUserId(user.getId());
            h.itemView.post(() -> h.tvInfo.setText("Phòng " + unitName + " - " + role));
        });

        if (user.isActive()) {
            h.tvStatus.setText("Active");
            h.tvStatus.setTextColor(Color.GREEN);
        } else {
            h.tvStatus.setText("Inactive");
            h.tvStatus.setTextColor(Color.RED);
        }

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(user);
            }
        });
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(user);
                return;
            }
            Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
            intent.putExtra("userId", user.getId());
            v.getContext().startActivity(intent);
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
