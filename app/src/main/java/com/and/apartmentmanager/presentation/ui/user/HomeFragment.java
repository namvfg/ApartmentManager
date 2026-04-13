package com.and.apartmentmanager.presentation.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.databinding.FragmentHomeBinding;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.ApartmentCardAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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

        // Greeting
        SessionManager sm = SessionManager.getInstance(requireContext());
        // TODO: lấy tên từ DB theo userId
        binding.textUserName.setText("Nguyễn Văn An");
        binding.avatarUser.setText("NA");

        // RecyclerView danh sách chung cư
        ApartmentCardAdapter adapter = new ApartmentCardAdapter(item -> {
            // Navigate sang WorkspaceFragment
            WorkspaceFragment workspace = WorkspaceFragment.newInstance(
                    item.apartmentId, item.unitId
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, workspace)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerApartments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerApartments.setAdapter(adapter);

        // TODO: load từ DB thật
        // viewModel.getApartments().observe(...)

        // Nhập mã mời
        binding.btnEnterInviteCode.setOnClickListener(v -> {
            // Navigate sang InviteCodeFragment
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}