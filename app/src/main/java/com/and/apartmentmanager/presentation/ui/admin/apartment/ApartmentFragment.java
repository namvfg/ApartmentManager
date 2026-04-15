package com.and.apartmentmanager.presentation.ui.admin.apartment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.adapter.ApartmentAdapter;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.repository.ApartmentRepository;
import com.and.apartmentmanager.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ApartmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApartmentAdapter adapter;
    private ApartmentRepository apartmentRepository;
    private Button btnAdd;
    private EditText edtSearch;

    private List<ApartmentEntity> originalList = new ArrayList<>();

    public ApartmentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_apartment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnAdd = view.findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TODO: dùng requireContext() thay vì getApplication()
        apartmentRepository = new ApartmentRepository(requireActivity().getApplication());
        adapter = new ApartmentAdapter(requireActivity().getApplication());
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showAddDialog());

        loadData();

        adapter.setOnItemClickListener(apartment -> {
            if (!apartment.isActive()) {
                Toast.makeText(requireContext(), "Chung cư chưa active", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), BlockUnitActivity.class);
            intent.putExtra("apartmentId", apartment.getId());
            startActivity(intent);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().toLowerCase().trim();
                List<ApartmentEntity> filtered = new ArrayList<>();

                for (ApartmentEntity a : originalList) {
                    if (a.getName() != null &&
                            a.getName().toLowerCase().contains(keyword)) {
                        filtered.add(a);
                    }
                }
                adapter.setData(filtered);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }

    // TODO: tách load data ra cho sạch
    private void loadData() {
        SessionManager sm = SessionManager.getInstance(requireContext());
        int adminId = (int) sm.getUserId();

        apartmentRepository.getByAdmin(adminId).observe(getViewLifecycleOwner(), list -> {
            originalList.clear();
            originalList.addAll(list);
            adapter.setData(list);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View view = getLayoutInflater().inflate(R.layout.add_apartment, null);
        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtAddress = view.findViewById(R.id.edtAddress);

        builder.setView(view)
                .setTitle("Thêm chung cư")
                .setPositiveButton("Thêm", (dialog, which) -> {

                    String name = edtName.getText().toString();
                    String address = edtAddress.getText().toString();

                    SessionManager sm = SessionManager.getInstance(requireContext());

                    ApartmentEntity apartment = new ApartmentEntity(
                            0,
                            name,
                            address,
                            true,
                            (int) sm.getUserId()
                    );

                    apartmentRepository.insert(apartment);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}