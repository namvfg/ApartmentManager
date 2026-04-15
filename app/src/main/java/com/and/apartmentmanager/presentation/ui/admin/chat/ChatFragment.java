package com.and.apartmentmanager.presentation.ui.admin.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.databinding.FragmentChatBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ChatAdapter;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_USER_NAME = "user_name";
    private static final String ARG_USER_ROOM = "user_room";
    private static final String ARG_APARTMENT_ID = "apartment_id";

    public static ChatFragment newInstance(long apartmentId, long userId, String userName, String userRoom) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_APARTMENT_ID, apartmentId); // TRUYỀN VÀO BUNDLE
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

        // 1. Lấy thông tin từ SessionManager và Arguments
        SessionManager session = SessionManager.getInstance(requireContext());
        boolean isAdmin = session.getRole().equals("admin"); // QUAN TRỌNG: Lấy vai trò người dùng hiện tại
        // SỬA DÒNG NÀY: Lấy apartmentId từ Arguments thay vì Session
        long apartmentId = getArguments() != null ? getArguments().getLong(ARG_APARTMENT_ID) : -1;

        long targetUserId = getArguments() != null ? getArguments().getLong(ARG_USER_ID) : -1;
        // ============ THÊM ĐOẠN NÀY ĐỂ BẮT LỖI ============
        android.util.Log.e("DEBUG_CHAT", "============== KIỂM TRA PHÒNG CHAT ==============");
        android.util.Log.e("DEBUG_CHAT", "Tôi là Admin? : " + isAdmin);
        android.util.Log.e("DEBUG_CHAT", "Apartment ID  : " + apartmentId);
        android.util.Log.e("DEBUG_CHAT", "User ID (phòng): " + targetUserId);
        android.util.Log.e("DEBUG_CHAT", "Đường dẫn FB  : chats/" + apartmentId + "/" + targetUserId);
        android.util.Log.e("DEBUG_CHAT", "=================================================");
        // ==================================================
        String name = getArguments() != null ? getArguments().getString(ARG_USER_NAME, "") : "";
        String room = getArguments() != null ? getArguments().getString(ARG_USER_ROOM, "") : "";

        // 2. Setup Header UI
        binding.textChatName.setText(name);
        binding.textChatRoom.setText(room);
        binding.textChatAvatar.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase());

        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // 3. Setup RecyclerView & Adapter (Truyền isAdmin vào đây)
        ChatAdapter adapter = new ChatAdapter(isAdmin);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // Luôn bắt đầu từ dưới cùng
        binding.recyclerMessages.setLayoutManager(layoutManager);
        binding.recyclerMessages.setAdapter(adapter);

        binding.recyclerMessages.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                binding.recyclerMessages.postDelayed(() -> {
                    if (adapter.getItemCount() > 0) {
                        binding.recyclerMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 100);
            }
        });

        // 4. Khởi tạo và Init ViewModel
        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        if (!isAdmin) {
            // NẾU LÀ CƯ DÂN: Lấy tên từ DB trong Background Thread
            long myUserId = session.getUserId();
            new Thread(() -> {
                com.and.apartmentmanager.data.local.entity.UserEntity myUser =
                        new com.and.apartmentmanager.data.repository.UserRepository(requireActivity().getApplication())
                                .getByIdSync((int) myUserId);

                String myName = (myUser != null && myUser.getName() != null) ? myUser.getName() : "Cư dân";

                // Sau khi có tên, gọi hàm init trên UI Thread MỘT LẦN DUY NHẤT
                requireActivity().runOnUiThread(() -> {
                    viewModel.init(apartmentId, targetUserId, myName, false);
                });
            }).start();

        } else {
            // NẾU LÀ ADMIN: 'name' truyền vào từ arguments đã là tên của Cư dân rồi
            // Gọi hàm init MỘT LẦN DUY NHẤT
            viewModel.init(apartmentId, targetUserId, name, true);
        }

    // DÒNG THỪA ĐÃ ĐƯỢC XÓA Ở ĐÂY

    // 5. Quan sát danh sách tin nhắn
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                binding.recyclerMessages.post(() ->
                        binding.recyclerMessages.smoothScrollToPosition(messages.size() - 1));
            }
        });

        // 6. Xử lý sự kiện gửi tin
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.editMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                binding.editMessage.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}