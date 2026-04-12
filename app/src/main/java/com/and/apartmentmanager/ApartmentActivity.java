package com.and.apartmentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.Adapter.ApartmentAdapter;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.repository.ApartmentRepository;
import com.and.apartmentmanager.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ApartmentActivity extends AppCompatActivity {
     RecyclerView recyclerView;
     ApartmentAdapter adapter;
     ApartmentRepository apartmentRepository;
    Button btnAdd;
    EditText edtSearch;
    List<ApartmentEntity> originalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apartmentRepository = new ApartmentRepository(getApplication());
        adapter = new ApartmentAdapter(getApplication());
        recyclerView.setAdapter(adapter);

        edtSearch= findViewById(R.id.edtSearch);
        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> showAddDialog());
        SessionManager sm = SessionManager.getInstance(this);
        int adminId = (int) sm.getUserId();
        SessionManager.getInstance(this)
                .saveSession(1, "admin", 1);

        apartmentRepository.getByAdmin(adminId).observe(this, list -> {
            originalList.clear();
            originalList.addAll(list);
            adapter.setData(list);
        });
        adapter.setOnItemClickListener(apartment -> {
            if (!apartment.isActive()) {
                Toast.makeText(this, "Chung cư chưa active", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, BlockUnitActivity.class);
            intent.putExtra("apartmentId", apartment.getId());
            startActivity(intent);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().toLowerCase().trim();
                List<ApartmentEntity> filtered = new ArrayList<>();

                for (ApartmentEntity a : originalList) {
                    if (a.getName() != null && a.getName().toLowerCase().contains(keyword)) {
                        filtered.add(a);
                    }
                }
                adapter.setData(filtered);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SessionManager sm = SessionManager.getInstance(this);
        int adminId = (int) sm.getUserId();

        apartmentRepository.getByAdmin(adminId).observe(this, list -> {
            originalList.clear();
            originalList.addAll(list);
            adapter.setData(list);
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.add_apartment, null);
        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtAddress = view.findViewById(R.id.edtAddress);

        builder.setView(view)
                .setTitle("Thêm chung cư")
                .setPositiveButton("Thêm", (dialog, which) -> {

                    String name = edtName.getText().toString();
                    String address = edtAddress.getText().toString();

                    SessionManager sm = SessionManager.getInstance(this);

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
