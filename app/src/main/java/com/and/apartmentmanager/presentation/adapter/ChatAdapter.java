package com.and.apartmentmanager.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.databinding.ItemMessageRecvBinding;
import com.and.apartmentmanager.databinding.ItemMessageSentBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_RECV = 0;
    private static final int TYPE_SENT = 1;

    private final List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent ? TYPE_SENT : TYPE_RECV;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            return new SentViewHolder(
                    ItemMessageSentBinding.inflate(inflater, parent, false)
            );
        } else {
            return new RecvViewHolder(
                    ItemMessageRecvBinding.inflate(inflater, parent, false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).bind(msg);
        } else {
            ((RecvViewHolder) holder).bind(msg);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    // ── ViewHolders ───────────────────────────────────────────────────────────

    static class SentViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageSentBinding binding;
        SentViewHolder(ItemMessageSentBinding b) {
            super(b.getRoot());
            binding = b;
        }
        void bind(Message msg) {
            binding.textMessage.setText(msg.text);
            binding.textTime.setText(msg.time + " ✓✓");
        }
    }

    static class RecvViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageRecvBinding binding;
        RecvViewHolder(ItemMessageRecvBinding b) {
            super(b.getRoot());
            binding = b;
        }
        void bind(Message msg) {
            binding.textMessage.setText(msg.text);
            binding.textTime.setText(msg.time);
        }
    }

    // ── Data model ────────────────────────────────────────────────────────────

    public static class Message {
        public String text;
        public String time;
        public boolean isSent; // true = Admin gửi, false = User gửi

        public Message(String text, String time, boolean isSent) {
            this.text = text;
            this.time = time;
            this.isSent = isSent;
        }
    }
}