package com.and.apartmentmanager.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.and.apartmentmanager.presentation.adapter.ChatAdapter;
import com.and.apartmentmanager.presentation.adapter.ChatListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                    // SỬA CHỮ postValue THÀNH setValue Ở ĐÂY
                    liveData.setValue(new ChatAdapter.Message(
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

    public LiveData<List<ChatListAdapter.ChatItem>> getAllChatSessions(long apartmentId) {
        MutableLiveData<List<ChatListAdapter.ChatItem>> liveData = new MutableLiveData<>();

        db.getReference(REF_CHATS).child(String.valueOf(apartmentId))
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<ChatListAdapter.ChatItem> list = new ArrayList<>();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String name = userSnap.child("userName").getValue(String.class);
                            String lastMsg = userSnap.child("lastMessage").getValue(String.class);
                            Long time = userSnap.child("lastTime").getValue(Long.class);

                            // Đọc thêm tên phòng từ Firebase (nếu có)
                            String room = userSnap.child("roomName").getValue(String.class);

                            if (name != null) {
                                list.add(new ChatListAdapter.ChatItem(
                                        Long.parseLong(userSnap.getKey()),
                                        name,
                                        // Truyền room vào đây, nếu Firebase chưa có thì để "Cư dân"
                                        room != null ? room : "Cư dân",
                                        lastMsg != null ? lastMsg : "",
                                        time != null ? formatTime(time) : "",
                                        0, false
                                ));
                            }
                        }
                        liveData.postValue(list);
                    }

                    @Override public void onCancelled(DatabaseError error) {}
                });

        return liveData;
    }

    /**
     * Gửi tin nhắn lên Firebase.
     * @param isSentByAdmin true = Admin gửi, false = User gửi
     */
    // Trong FirebaseChatRepository.java
    public void sendMessage(long apartmentId, long residentId, String residentName, String text, boolean isSentByAdmin) {
        DatabaseReference ref = db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(residentId))
                .child("messages")
                .push();

        FirebaseMessage msg = new FirebaseMessage(text, System.currentTimeMillis(), isSentByAdmin);
        ref.setValue(msg);

        // Cập nhật thông tin bổ trợ để Admin thấy trong danh sách tổng
        DatabaseReference root = db.getReference(REF_CHATS)
                .child(String.valueOf(apartmentId))
                .child(String.valueOf(residentId));

        root.child("lastMessage").setValue(text);
        root.child("lastTime").setValue(System.currentTimeMillis());

        // THÊM DÒNG NÀY: Chỉ cập nhật userName nếu đây là tin nhắn đầu tiên hoặc muốn ghi đè
        // Để an toàn, cứ lưu mỗi khi nhắn tin
        if (residentName != null && !residentName.isEmpty()) {
            root.child("userName").setValue(residentName);
        }
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