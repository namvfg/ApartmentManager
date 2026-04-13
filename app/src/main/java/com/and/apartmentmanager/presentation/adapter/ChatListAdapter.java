package com.and.apartmentmanager.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.databinding.ItemChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> items = new ArrayList<>();
    private OnChatClickListener listener;

    // Interface để Fragment xử lý click
    public interface OnChatClickListener {
        void onChatClick(ChatItem item);
    }

    public ChatListAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ChatItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────
    class ChatViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatBinding binding;

        ChatViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatItem item) {
            // Avatar: lấy chữ cái đầu tên
            binding.textAvatar.setText(
                    item.userName.isEmpty() ? "?" :
                            String.valueOf(item.userName.charAt(0)).toUpperCase()
            );

            // Màu avatar khác nhau theo userId
            int[] colors = {
                    0xFF2E6F40, 0xFF68BA7F, 0xFF3D8B52,
                    0xFF1565C0, 0xFF6A1B9A, 0xFFAD1457
            };
            int colorIndex = (int)(item.userId % colors.length);
            binding.textAvatar.getBackground().setTint(colors[colorIndex]);

            binding.textName.setText(item.userName);
            binding.textLastMessage.setText(item.lastMessage);
            binding.textTime.setText(item.time);

            // Online indicator
            binding.onlineIndicator.setVisibility(
                    item.isOnline ? View.VISIBLE : View.GONE
            );

            // Unread badge
            if (item.unreadCount > 0) {
                binding.badgeUnread.setVisibility(View.VISIBLE);
                binding.badgeUnread.setText(
                        item.unreadCount > 99 ? "99+" : String.valueOf(item.unreadCount)
                );
                // Tin nhắn chưa đọc → text đậm hơn
                binding.textLastMessage.setTextColor(
                        binding.getRoot().getContext().getColor(
                                com.and.apartmentmanager.R.color.text_primary
                        )
                );
            } else {
                binding.badgeUnread.setVisibility(View.GONE);
                binding.textLastMessage.setTextColor(
                        binding.getRoot().getContext().getColor(
                                com.and.apartmentmanager.R.color.text_secondary
                        )
                );
            }

            // Highlight nền nếu có tin chưa đọc
            binding.getRoot().setBackgroundColor(
                    item.unreadCount > 0
                            ? binding.getRoot().getContext().getColor(
                            com.and.apartmentmanager.R.color.primary_surface)
                            : binding.getRoot().getContext().getColor(
                            android.R.color.white)
            );

            // Click
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onChatClick(item);
            });
        }
    }

    // ── Data model ────────────────────────────────────────────────────────────
    public static class ChatItem {
        public long userId;
        public String userName;
        public String lastMessage;
        public String time;
        public int unreadCount;
        public boolean isOnline;

        public ChatItem(long userId, String userName, String lastMessage,
                        String time, int unreadCount, boolean isOnline) {
            this.userId = userId;
            this.userName = userName;
            this.lastMessage = lastMessage;
            this.time = time;
            this.unreadCount = unreadCount;
            this.isOnline = isOnline;
        }
    }
}