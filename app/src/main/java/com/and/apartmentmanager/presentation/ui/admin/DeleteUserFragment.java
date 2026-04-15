package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.presentation.adapter.UserAdapter;
import com.and.apartmentmanager.presentation.ui.user.DeleteConfirmActivity;

public class DeleteUserFragment extends Fragment {

    private RecyclerView rvUser;
    private UserAdapter adapter;
    private UserRepository userRepository;

    public DeleteUserFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_user, container, false);

        rvUser = view.findViewById(R.id.rvUser);
        rvUser.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TODO: Repository cần Application
        userRepository = new UserRepository(requireActivity().getApplication());

        // TODO: Adapter dùng context, KHÔNG dùng application
        adapter = new UserAdapter(requireActivity().getApplication());
        rvUser.setAdapter(adapter);

        // TODO: observe theo lifecycle của Fragment
        userRepository.getUserRequestDelete()
                .observe(getViewLifecycleOwner(), list -> adapter.setData(list));

        adapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(requireContext(), DeleteConfirmActivity.class);
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });

        return view;
    }
}