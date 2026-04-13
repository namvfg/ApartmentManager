package com.and.apartmentmanager.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.ItemServiceRowBinding;

import java.util.ArrayList;
import java.util.List;

public class ServiceRowAdapter extends RecyclerView.Adapter<ServiceRowAdapter.ViewHolder> {

    private List<ServiceItem> items = new ArrayList<>();

    public void setItems(List<ServiceItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemServiceRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position == items.size() - 1);
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemServiceRowBinding binding;

        ViewHolder(ItemServiceRowBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(ServiceItem item, boolean isLast) {
            binding.textServiceName.setText(item.name);
            binding.textServiceDetail.setText(item.detail);
            binding.iconService.setImageResource(item.iconRes);

            // Ẩn divider ở item cuối
            binding.divider.setVisibility(isLast ? View.GONE : View.VISIBLE);
        }
    }

    // ── Data model ────────────────────────────────────────────────────────────

    public static class ServiceItem {
        public String name;
        public String detail;
        public int iconRes;

        public ServiceItem(String name, String detail, int iconRes) {
            this.name    = name;
            this.detail  = detail;
            this.iconRes = iconRes;
        }
    }
}