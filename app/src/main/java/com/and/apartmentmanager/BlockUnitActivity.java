package com.and.apartmentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.Adapter.BlockAdapter;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;

import java.util.concurrent.Executors;

public class BlockUnitActivity extends AppCompatActivity {

    ImageView btnBack,btnAddBlock ;
    int apartmentId;

    RecyclerView rvBlock;
    BlockAdapter adapter;
     AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_block_unit);
        apartmentId = getIntent().getIntExtra("apartmentId", -1);

        db = AppDatabase.getInstance(this);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnAddBlock = findViewById(R.id.btnAddBlock);
        btnAddBlock.setOnClickListener(v -> showAddBlockDialog());
        Toast.makeText(this, "Apartment ID: " + apartmentId, Toast.LENGTH_SHORT).show();


        rvBlock = findViewById(R.id.rvBlock);
        rvBlock.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BlockAdapter(db,this);
        rvBlock.setAdapter(adapter);

        db.blockDao().getByApartment(apartmentId).observe(this, list -> {
            adapter.setData(list);
        });
        adapter.setOnAddUnitClick(block -> {
            showAddUnitDialog(block);
        });
    }
    private void showAddBlockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_add_block, null);
        EditText edtName = view.findViewById(R.id.edtBlockName);

        builder.setView(view)
                .setTitle("Thêm Block")
                .setPositiveButton("Thêm", (dialog, which) -> {

                    String name = edtName.getText().toString();

                    BlockEntity block = new BlockEntity(
                            0,
                            apartmentId,
                            name
                    );

                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.blockDao().insert(block);
                    });

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
                                db.unitDao().insert(unit);

                            }
                        }

                    });

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}