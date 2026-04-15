package com.and.apartmentmanager.presentation.ui.admin.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.FragmentChatListBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ChatListAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private ChatListViewModel mViewModel;

    private FragmentChatListBinding binding;
    private ChatListAdapter adapter;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo ViewModel
        mViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        // 2. Setup Adapter
        adapter = new ChatListAdapter(item -> {
            long adminAptId = SessionManager.getInstance(requireContext()).getApartmentId();

            ChatFragment chatFragment = ChatFragment.newInstance(
                    adminAptId,
                    item.userId,
                    item.userName,
                    item.roomName // <--- Dùng roomName của item luôn
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerChatList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerChatList.setAdapter(adapter);
        binding.recyclerChatList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // 3. Quan sát dữ liệu từ ViewModel
        mViewModel.getChatList().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            // Hiển thị trạng thái trống nếu cần
            binding.textEmptyMessage.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // 4. Load dữ liệu lần đầu
        mViewModel.loadChats();

        // 5. Logic Search cực gọn
        binding.searchChat.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.filter(s.toString().trim());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}