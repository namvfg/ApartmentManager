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

        // Setup RecyclerView
        // Setup RecyclerView
        adapter = new ChatListAdapter(item -> {
            ChatFragment chatFragment = ChatFragment.newInstance(
                    item.userId,
                    item.userName,
                    "Phòng A-101" // sau lấy từ DB thật
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerChatList.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.recyclerChatList.setAdapter(adapter);

        // Thêm divider giữa các item
        binding.recyclerChatList.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        // Test data tạm — sau thay bằng Firebase thật
        List<ChatListAdapter.ChatItem> testData = new ArrayList<>();
        testData.add(new ChatListAdapter.ChatItem(1, "Nguyễn Văn A", "Cảm ơn bạn nhiều!", "09:34", 2, true));
        testData.add(new ChatListAdapter.ChatItem(2, "Lê Văn C", "Tôi muốn hỏi về hóa đơn tháng 3...", "Hôm qua", 0, false));
        testData.add(new ChatListAdapter.ChatItem(3, "Trần Thị B", "Bạn: Dạ, đã được duyệt rồi ạ.", "3 ngày trước", 0, false));
        adapter.setItems(testData);

        // Search
        binding.searchChat.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: filter danh sách theo tên
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}