package com.and.apartmentmanager.presentation.ui.admin.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.and.apartmentmanager.presentation.adapter.ChatListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatListViewModel extends ViewModel {
    private final MutableLiveData<List<ChatListAdapter.ChatItem>> _chatList = new MutableLiveData<>();
    private List<ChatListAdapter.ChatItem> fullList = new ArrayList<>();

    public LiveData<List<ChatListAdapter.ChatItem>> getChatList() {
        return _chatList;
    }

    // Sau này hàm này sẽ gọi Repo để lấy dữ liệu từ Firebase
    public void loadChats() {
        List<ChatListAdapter.ChatItem> testData = new ArrayList<>();
        fullList = testData;
        _chatList.setValue(testData);
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            _chatList.setValue(fullList);
            return;
        }

        List<ChatListAdapter.ChatItem> filtered = new ArrayList<>();
        for (ChatListAdapter.ChatItem item : fullList) {
            if (item.userName.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        _chatList.setValue(filtered);
    }
}