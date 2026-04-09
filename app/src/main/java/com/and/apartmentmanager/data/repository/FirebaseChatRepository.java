package com.and.apartmentmanager.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.and.apartmentmanager.presentation.adapter.ChatAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseChatRepository {

    private final FirebaseDatabase db;

    // Chat key: /chats/{apartmentId}/{userId}/messages
    private static final String REF_CHATS = "chats";

    public FirebaseChatRepository() {
        db = FirebaseDatabase.getInstance();
    }

    /**
     * Lắng nghe tin nhắn real-time của 1 cuộc trò chuyện.
     * Trả về LiveData<Message> — mỗi khi có tin mới sẽ emit.
     */
    public LiveData<ChatAdapter.Message> listenMessages(long apartmentId, long userId) {
        MutableLiveData<ChatAdapter.Message> liveData = new MutableLiveData<>();

        DatabaseReference ref = db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(userId))
                .child("messages");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                FirebaseMessage msg = snapshot.getValue(FirebaseMessage.class);
                if (msg != null) {
                    liveData.postValue(new ChatAdapter.Message(
                            msg.text,
                            formatTime(msg.timestamp),
                            msg.isSentByAdmin
                    ));
                }
            }

            @Override public void onChildChanged(DataSnapshot s, String p) {}
            @Override public void onChildRemoved(DataSnapshot s) {}
            @Override public void onChildMoved(DataSnapshot s, String p) {}
            @Override public void onCancelled(DatabaseError error) {}
        });

        return liveData;
    }

    /**
     * Gửi tin nhắn lên Firebase.
     * @param isSentByAdmin true = Admin gửi, false = User gửi
     */
    public void sendMessage(long apartmentId, long userId,
                            String text, boolean isSentByAdmin) {
        DatabaseReference ref = db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(userId))
                .child("messages")
                .push(); // tự generate key

        FirebaseMessage msg = new FirebaseMessage(
                text,
                System.currentTimeMillis(),
                isSentByAdmin
        );

        ref.setValue(msg);

        // Cập nhật lastMessage để hiện trên danh sách chat
        db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(userId))
                .child("lastMessage")
                .setValue(text);

        db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(userId))
                .child("lastTime")
                .setValue(System.currentTimeMillis());
    }

    private String formatTime(long timestamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }

    // ── Firebase data model ───────────────────────────────────────────────────
    // Phải có constructor rỗng để Firebase deserialize được

    public static class FirebaseMessage {
        public String text;
        public long timestamp;
        public boolean isSentByAdmin;

        public FirebaseMessage() {} // bắt buộc

        public FirebaseMessage(String text, long timestamp, boolean isSentByAdmin) {
            this.text          = text;
            this.timestamp     = timestamp;
            this.isSentByAdmin = isSentByAdmin;
        }
    }
}