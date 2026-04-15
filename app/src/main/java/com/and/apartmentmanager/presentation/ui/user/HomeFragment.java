package com.and.apartmentmanager.presentation.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.databinding.FragmentHomeBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ApartmentCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private UserRepository userRepository;
    private UserEntity currentUser;
    private FragmentHomeBinding binding;

    private ApartmentCardAdapter adapter;
    private long userId;

    // Launcher để nhận kết quả từ JoinActivity
    private final ActivityResultLauncher<Intent> joinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            loadApartments(); // reload data
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sm = SessionManager.getInstance(requireContext());
        userId = sm.getUserId();

        userRepository = new UserRepository(requireActivity().getApplication());

        setupRecyclerView();
        loadUser();
        loadApartments();

        // ✅ TODO đã làm: mở JoinActivity
        binding.btnEnterInviteCode.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), JoinActivity.class);
            joinLauncher.launch(intent);
        });
    }

    // ===============================
    // Load user
    // ===============================
    private void loadUser() {
        new Thread(() -> {
            UserEntity user = userRepository.getByIdBlocking(userId);
            if (user == null || !isAdded()) return;

            currentUser = user;

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                binding.textUserName.setText(currentUser.getName());
                binding.avatarUser.setText(getInitials(currentUser.getName()));
            });
        }).start();
    }

    // ===============================
    // 🏢 RecyclerView
    // ===============================
    private void setupRecyclerView() {
        adapter = new ApartmentCardAdapter(item -> {
            WorkspaceFragment workspace = WorkspaceFragment.newInstance(
                    item.apartmentId, item.unitId
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, workspace)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerApartments.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.recyclerApartments.setAdapter(adapter);
    }

    // ===============================
    // Load apartments
    // ===============================
    private void loadApartments() {
        new Thread(() -> {
            List<ApartmentCardAdapter.ApartmentItem> list =
                    userRepository.getApartmentsByUserIdBlocking((int) userId);

            final List<ApartmentCardAdapter.ApartmentItem> finalList =
                    (list == null) ? new ArrayList<>() : list;

            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                adapter.setItems(finalList);
            });
        }).start();
    }

    // ===============================
    // Avatar initials
    // ===============================
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "NA";

        String[] parts = name.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < Math.min(2, parts.length); i++) {
            result.append(parts[i].charAt(0));
        }

        return result.toString().toUpperCase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}