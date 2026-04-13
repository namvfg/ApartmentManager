package com.and.apartmentmanager.presentation.ui.user.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.data.local.entity.NotificationEntity;
import com.and.apartmentmanager.databinding.FragmentNotificationListBinding;
import com.and.apartmentmanager.presentation.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationListFragment extends Fragment {

    private FragmentNotificationListBinding binding;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView
        adapter = new NotificationAdapter();
        binding.recyclerNotifications.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.recyclerNotifications.setAdapter(adapter);

        // ViewModel đã map sẵn sang NotifItem rồi, chỉ cần setItems
        NotificationViewModel viewModel = new ViewModelProvider(this)
                .get(NotificationViewModel.class);

        viewModel.getNotifications().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items); // items đã là List<NotificationAdapter.NotifItem>
        });

        binding.btnMarkAllRead.setOnClickListener(v ->
                viewModel.markAllRead()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}