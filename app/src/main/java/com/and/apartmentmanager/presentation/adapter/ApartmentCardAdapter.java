package com.and.apartmentmanager.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.ItemApartmentCardBinding;

import java.util.ArrayList;
import java.util.List;

public class ApartmentCardAdapter extends RecyclerView.Adapter<ApartmentCardAdapter.ViewHolder> {

    private List<ApartmentItem> items = new ArrayList<>();
    private final OnApartmentClickListener listener;

    public interface OnApartmentClickListener {
        void onApartmentClick(ApartmentItem item);
    }

    public ApartmentCardAdapter(OnApartmentClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ApartmentItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemApartmentCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemApartmentCardBinding binding;

        ViewHolder(ItemApartmentCardBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(ApartmentItem item) {
            binding.textAptName.setText(item.apartmentName);
            binding.textAptInfo.setText(item.blockName + " · Phòng " + item.unitName);

            // Status chip
            if (item.isActive) {
                binding.chipStatus.setText("Active");
                binding.chipStatus.setBackgroundResource(R.drawable.bg_chip_green);
                binding.chipStatus.setTextColor(
                        binding.getRoot().getContext().getColor(R.color.primary)
                );
                // Icon màu xanh
                binding.layoutAptIcon.setBackgroundResource(R.drawable.bg_apt_icon);
                binding.getRoot().setAlpha(1.0f);
            } else {
                binding.chipStatus.setText("Inactive");
                binding.chipStatus.setBackgroundResource(R.drawable.bg_chip_gray);
                binding.chipStatus.setTextColor(
                        binding.getRoot().getContext().getColor(R.color.text_hint)
                );
                // Icon màu xám
                binding.layoutAptIcon.setBackgroundResource(R.drawable.bg_apt_icon_gray);
                binding.getRoot().setAlpha(0.55f);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (item.isActive && listener != null) listener.onApartmentClick(item);
            });
        }
    }

    // ── Data model ────────────────────────────────────────────────────────────

    public static class ApartmentItem {
        public long apartmentId;
        public long unitId;
        public String apartmentName;
        public String blockName;
        public String unitName;
        public boolean isActive;

        public ApartmentItem(long apartmentId, long unitId, String apartmentName,
                             String blockName, String unitName, boolean isActive) {
            this.apartmentId   = apartmentId;
            this.unitId        = unitId;
            this.apartmentName = apartmentName;
            this.blockName     = blockName;
            this.unitName      = unitName;
            this.isActive      = isActive;
        }
    }
}