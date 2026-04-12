package com.and.apartmentmanager.presentation.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.ui.user.UserActivity;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import lombok.NonNull;

public class ApartmentAdapter extends RecyclerView.Adapter<ApartmentAdapter.ViewHolder> {

    AppDatabase db;
    private List<ApartmentEntity> list = new ArrayList<>();

    public void setData(List<ApartmentEntity> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = AppDatabase.getInstance(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ApartmentEntity item = list.get(position);

        h.tvName.setText(item.getName());
        h.tvAddress.setText(item.getAddress());
        if (item.isActive()) {
            h.tvStatus.setText("Active");
            h.tvStatus.setTextColor(Color.GREEN);
        } else {
            h.tvStatus.setText("Inactive");
            h.tvStatus.setTextColor(Color.RED);
        }
        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item);
            }
        });

        h.tvUser.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UserActivity.class);
            intent.putExtra("apartmentId", item.getId());
            v.getContext().startActivity(intent);
        });
        h.btnEditApartment.setOnClickListener(v -> {
            LinearLayout layout = new LinearLayout(v.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 30, 50, 10);

            EditText edtName = new EditText(v.getContext());
            edtName.setHint("Tên chung cư");
            edtName.setText(item.getName());

            EditText edtAddress = new EditText(v.getContext());
            edtAddress.setHint("Địa chỉ");
            edtAddress.setText(item.getAddress());

            layout.addView(edtName);
            layout.addView(edtAddress);

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Sửa chung cư")
                    .setView(layout)
                    .setPositiveButton("Lưu", (d, w) -> {

                        String newName = edtName.getText().toString().trim();
                        String newAddress = edtAddress.getText().toString().trim();

                        item.setName(newName);
                        item.setAddress(newAddress);

                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.apartmentDao().update(item);
                        });

                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        h.btnDeleteApartment.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa chung cư")
                    .setMessage("Bạn chắc chắn?")
                    .setPositiveButton("Xóa", (d, w) -> {

                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.apartmentDao().delete(item);
                        });

                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
        Executors.newSingleThreadExecutor().execute(() -> {

            int blockCount = db.apartmentDao().countBlocks(item.getId());
            int unitCount = db.unitDao().countByApartment(item.getId());
            int userCount = db.userApartmentDao().countActiveUsers(item.getId());
            UserEntity  admin= db.userDao().getByIdSync(item.getAdminId());
            h.itemView.post(() -> {
                h.tvBlock.setText(String.valueOf(blockCount));
                h.tvUnit.setText(String.valueOf(unitCount));
                h.tvUser.setText(String.valueOf(userCount));

                if (admin != null) {
                    h.tvAdmin.setText("👤 " + admin.getName());
                } else {
                    h.tvAdmin.setText("Thiếu QL");
                    h.tvAdmin.setTextColor(Color.parseColor("#FF9800"));
                }

            });

        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress,tvStatus,tvBlock, tvUnit, tvUser,tvAdmin;
        ImageView btnEditApartment,btnDeleteApartment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBlock = itemView.findViewById(R.id.tvBlock);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            btnEditApartment=itemView.findViewById(R.id.btnEditApartment);
            btnDeleteApartment=itemView.findViewById(R.id.btnDeleteApartment);
        }
    }


    public interface OnItemClickListener {
        void onClick(ApartmentEntity apartment);

    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}