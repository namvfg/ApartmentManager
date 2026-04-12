package com.and.apartmentmanager.Adapter;

import android.app.Application;
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
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.repository.BlockRepository;
import com.and.apartmentmanager.data.repository.UnitRepository;

import java.util.ArrayList;
import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {

    private List<BlockEntity> list = new ArrayList<>();
    private final BlockRepository blockRepository;
    private final UnitRepository unitRepository;
    private final LifecycleOwner lifecycleOwner;
    int adminId;

    public BlockAdapter(Application application, LifecycleOwner lifecycleOwner, int adminId) {
        this.blockRepository = new BlockRepository(application);
        this.unitRepository = new UnitRepository(application);
        this.lifecycleOwner = lifecycleOwner;
        this.adminId = adminId;
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

        UnitAdapter unitAdapter = new UnitAdapter(
                (Application) h.itemView.getContext().getApplicationContext(),
                block.getApartmentId(),
                adminId);
        h.rvUnit.setLayoutManager(new GridLayoutManager(h.itemView.getContext(), 4));
        h.rvUnit.setAdapter(unitAdapter);

        unitRepository.getByBlock(block.getId())
                .observe(lifecycleOwner, units -> unitAdapter.setData(units));

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
                        blockRepository.update(block);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        h.btnDeleteBlock.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa Block")
                    .setMessage("Bạn chắc chắn?")
                    .setPositiveButton("Xóa", (d, w) -> blockRepository.delete(block))
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
