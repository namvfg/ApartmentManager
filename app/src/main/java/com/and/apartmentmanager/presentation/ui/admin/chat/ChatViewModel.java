package com.and.apartmentmanager.presentation.ui.admin.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.and.apartmentmanager.data.repository.FirebaseChatRepository;
import com.and.apartmentmanager.presentation.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final FirebaseChatRepository repository;
    private final MutableLiveData<List<ChatAdapter.Message>> _messages = new MutableLiveData<>(new ArrayList<>());
    private final List<ChatAdapter.Message> messageList = new ArrayList<>();

    private long apartmentId;
    private long userId;
    private boolean isAdminRole;

    private String residentName;

    public ChatViewModel() {
        this.repository = new FirebaseChatRepository();
    }

    public void init(long apartmentId, long userId, String userName, boolean isAdmin) {
        this.apartmentId = apartmentId;
        this.userId = userId;
        this.residentName = userName;
        this.isAdminRole = isAdmin;

        // Bắt đầu lắng nghe tin nhắn từ Repo
        messageList.clear();

        repository.listenMessages(apartmentId, userId).observeForever(message -> {
            if (message != null) {
                messageList.add(message);
                _messages.setValue(new ArrayList<>(messageList));
            }
        });
    }

    public LiveData<List<ChatAdapter.Message>> getMessages() {
        return _messages;
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        repository.sendMessage(apartmentId, userId, residentName, text, isAdminRole);
    }
}