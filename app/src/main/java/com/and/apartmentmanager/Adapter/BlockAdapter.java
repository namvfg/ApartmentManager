package com.and.apartmentmanager.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {

    private List<BlockEntity> list = new ArrayList<>();
    private AppDatabase db;
    private LifecycleOwner lifecycleOwner;

    public BlockAdapter(AppDatabase db, LifecycleOwner lifecycleOwner) {
        this.db = db;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setData(List<BlockEntity> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BlockEntity block = list.get(position);

        h.tvBlockName.setText(block.getName());
        h.rvUnit.setNestedScrollingEnabled(false);

        UnitAdapter unitAdapter = new UnitAdapter(block.getApartmentId());
        h.rvUnit.setLayoutManager(new GridLayoutManager(h.itemView.getContext(), 4));
        h.rvUnit.setAdapter(unitAdapter);

        db.unitDao().getByBlock(block.getId())
                .observe(lifecycleOwner, units -> {
                    unitAdapter.setData(units);
                });

        h.btnAddUnit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdd(block);
            }
        });

        h.btnEditBlock.setOnClickListener(v -> {
            EditText edt = new EditText(v.getContext());
            edt.setText(block.getName());

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Sửa Block")
                    .setView(edt)
                    .setPositiveButton("Lưu", (d, w) -> {
                        block.setName(edt.getText().toString());

                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.blockDao().update(block);
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        h.btnDeleteBlock.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa Block")
                    .setMessage("Bạn chắc chắn?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            db.blockDao().delete(block);
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBlockName;
        RecyclerView rvUnit;
        ImageView btnAddUnit, btnDeleteBlock, btnEditBlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBlockName = itemView.findViewById(R.id.tvBlockName);
            rvUnit = itemView.findViewById(R.id.rvUnit);
            btnAddUnit = itemView.findViewById(R.id.btnAddUnit);
            btnDeleteBlock = itemView.findViewById(R.id.btnDeleteBlock);
            btnEditBlock = itemView.findViewById(R.id.btnEditBlock);
        }
    }

    public interface OnAddUnitClick {
        void onAdd(BlockEntity block);
    }

    private OnAddUnitClick listener;

    public void setOnAddUnitClick(OnAddUnitClick listener) {
        this.listener = listener;
    }
}