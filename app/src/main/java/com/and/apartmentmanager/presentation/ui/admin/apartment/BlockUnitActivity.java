package com.and.apartmentmanager.presentation.ui.admin.apartment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.adapter.BlockAdapter;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.repository.BlockRepository;
import com.and.apartmentmanager.data.repository.UnitRepository;
import com.and.apartmentmanager.helper.SessionManager;

import java.util.concurrent.Executors;

public class BlockUnitActivity extends AppCompatActivity {

    ImageView btnBack, btnAddBlock;
    int apartmentId;

    RecyclerView rvBlock;
    BlockAdapter adapter;
    BlockRepository blockRepository;
    UnitRepository unitRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_block_unit);
        apartmentId = getIntent().getIntExtra("apartmentId", -1);

        blockRepository = new BlockRepository(getApplication());
        unitRepository = new UnitRepository(getApplication());

        int adminId = (int) SessionManager.getInstance(this).getUserId();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnAddBlock = findViewById(R.id.btnAddBlock);
        btnAddBlock.setOnClickListener(v -> showAddBlockDialog());

        rvBlock = findViewById(R.id.rvBlock);
        rvBlock.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BlockAdapter(getApplication(), this, adminId);
        rvBlock.setAdapter(adapter);

        blockRepository.getByApartment(apartmentId).observe(this, list -> adapter.setData(list));
        adapter.setOnAddUnitClick(this::showAddUnitDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showAddBlockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_add_block, null);
        EditText edtName = view.findViewById(R.id.edtBlockName);

        builder.setView(view)
                .setTitle("Thêm Block")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtName.getText().toString();
                    BlockEntity block = new BlockEntity(0, apartmentId, name);
                    blockRepository.insert(block);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddUnitDialog(BlockEntity block) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_unit, null);

        EditText edtStart = view.findViewById(R.id.edtStartFloor);
        EditText edtEnd = view.findViewById(R.id.edtEndFloor);
        EditText edtPer = view.findViewById(R.id.edtPerFloor);

        builder.setView(view)
                .setTitle("Thêm phòng - " + block.getName())
                .setPositiveButton("Thêm", (dialog, which) -> {
                    int start = Integer.parseInt(edtStart.getText().toString());
                    int end = Integer.parseInt(edtEnd.getText().toString());
                    int per = Integer.parseInt(edtPer.getText().toString());

                    Executors.newSingleThreadExecutor().execute(() -> {
                        for (int floor = start; floor <= end; floor++) {
                            for (int i = 1; i <= per; i++) {
                                String name = floor + String.format("%02d", i);
                                UnitEntity unit = new UnitEntity();
                                unit.setBlockId(block.getId());
                                unit.setName(name);
                                unitRepository.insert(unit);
                            }
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
