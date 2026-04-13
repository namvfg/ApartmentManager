package com.and.apartmentmanager.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.ItemNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<NotifItem> items = new ArrayList<>();

    public void setItems(List<NotifItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotifViewHolder(ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    class NotifViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        NotifViewHolder(ItemNotificationBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(NotifItem item) {
            binding.textTitle.setText(item.title);
            binding.textTime.setText(item.time);

            // Content (có thể null)
            if (item.content != null && !item.content.isEmpty()) {
                binding.textContent.setVisibility(View.VISIBLE);
                binding.textContent.setText(item.content);
            } else {
                binding.textContent.setVisibility(View.GONE);
            }

            // Icon + màu theo type
            switch (item.type) {
                case INVOICE:
                    binding.iconNotif.setImageResource(R.drawable.ic_invoice);
                    binding.layoutIcon.setBackgroundResource(R.drawable.bg_notif_icon_green);
                    break;
                case PRICE_CHANGE:
                    binding.iconNotif.setImageResource(R.drawable.ic_warning);
                    binding.layoutIcon.setBackgroundResource(R.drawable.bg_notif_icon_yellow);
                    break;
                case GENERAL:
                default:
                    binding.iconNotif.setImageResource(R.drawable.ic_check);
                    binding.layoutIcon.setBackgroundResource(R.drawable.bg_notif_icon_gray);
                    break;
            }

            // Nền khác nhau: chưa đọc vs đã đọc
            binding.getRoot().setBackgroundResource(
                    item.isRead
                            ? R.drawable.bg_notif_item_read
                            : R.drawable.bg_notif_item
            );

            // Mờ nếu đã đọc
            binding.getRoot().setAlpha(item.isRead ? 0.65f : 1.0f);
        }
    }

    // ── Data model ────────────────────────────────────────────────────────────

    public enum NotifType { INVOICE, PRICE_CHANGE, GENERAL }

    public static class NotifItem {
        public String title;
        public String content;
        public String time;
        public boolean isRead;
        public NotifType type;

        public NotifItem(String title, String content, String time,
                         boolean isRead, NotifType type) {
            this.title   = title;
            this.content = content;
            this.time    = time;
            this.isRead  = isRead;
            this.type    = type;
        }
    }
}