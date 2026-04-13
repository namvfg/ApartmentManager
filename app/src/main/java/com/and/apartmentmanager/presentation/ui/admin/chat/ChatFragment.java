package com.and.apartmentmanager.presentation.ui.admin.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.data.repository.FirebaseChatRepository;
import com.and.apartmentmanager.databinding.FragmentChatBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ChatAdapter;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    // Nhận userId và tên từ màn hình trước
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_USER_NAME = "user_name";
    private static final String ARG_USER_ROOM = "user_room";

    public static ChatFragment newInstance(long userId, String userName, String userRoom) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        args.putString(ARG_USER_NAME, userName);
        args.putString(ARG_USER_ROOM, userRoom);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy args
        long userId = getArguments() != null ? getArguments().getLong(ARG_USER_ID) : -1;
        String name = getArguments() != null ? getArguments().getString(ARG_USER_NAME, "") : "";
        String room = getArguments() != null ? getArguments().getString(ARG_USER_ROOM, "") : "";

        // Setup header
        binding.textChatName.setText(name);
        binding.textChatRoom.setText(room);
        binding.textChatAvatar.setText(
                name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase()
        );

        // Back button
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Setup RecyclerView
        ChatAdapter adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // tin nhắn mới nhất ở dưới
        binding.recyclerMessages.setLayoutManager(layoutManager);
        binding.recyclerMessages.setAdapter(adapter);

        FirebaseChatRepository chatRepo = new FirebaseChatRepository();
        long apartmentId = SessionManager.getInstance(requireContext()).getApartmentId();


        // Lắng nghe tin nhắn real-time
        chatRepo.listenMessages(apartmentId, userId)
                .observe(getViewLifecycleOwner(), message -> {
                    adapter.addMessage(message);
                    binding.recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
                });

        // Gửi tin nhắn
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.editMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            chatRepo.sendMessage(apartmentId, userId, text, true); // true = Admin gửi
            binding.editMessage.setText("");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}