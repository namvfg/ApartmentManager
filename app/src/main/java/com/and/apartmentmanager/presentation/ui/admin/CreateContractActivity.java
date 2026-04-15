package com.and.apartmentmanager.presentation.ui.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ContractEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateContractActivity extends AppCompatActivity {

    private AppDatabase db;

    private int userId;
    private int unitId;
    private int apartmentId;

    private final ActivityResultLauncher<Intent> pickPdfLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            Toast.makeText(this, "Đã chọn file PDF!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contract);

        // ✅ NHẬN DATA TỪ INTENT
        userId = getIntent().getIntExtra("userId", -1);
        unitId = getIntent().getIntExtra("unitId", -1);
        apartmentId = getIntent().getIntExtra("apartmentId", -1);

        // ❗ CHECK TRÁNH CRASH
        if (userId == -1 || unitId == -1 || apartmentId == -1) {
            Toast.makeText(this, "Thiếu dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // DB
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "apartment_db"
        ).allowMainThreadQueries().build();

        // View
        EditText edtStartDate = findViewById(R.id.edtStartDate);
        EditText edtEndDate = findViewById(R.id.edtEndDate);
        EditText edtPrice = findViewById(R.id.edtPrice);
        Button btnCreate = findViewById(R.id.btnCreate);
        ImageButton btnBack = findViewById(R.id.btnBack);
        LinearLayout layoutUpload = findViewById(R.id.layoutUpload);

        // Upload PDF
        if (layoutUpload != null) {
            layoutUpload.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                pickPdfLauncher.launch(intent);
            });
        }

        btnBack.setOnClickListener(v -> finish());

        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        // ✅ CREATE CONTRACT
        btnCreate.setOnClickListener(v -> {
            try {
                String startStr = edtStartDate.getText().toString();
                String endStr = edtEndDate.getText().toString();
                String priceStr = edtPrice.getText().toString().replace(".", "");

                // ❗ VALIDATE
                if (startStr.isEmpty() || endStr.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceStr);

                long startDate = convertToMillis(startStr);
                long endDate = convertToMillis(endStr);

                if (endDate <= startDate) {
                    Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ OBJECT ĐÚNG
                ContractEntity contract = ContractEntity.builder()
                        .userId(userId)
                        .apartmentId(apartmentId)
                        .unitId(unitId)
                        .startDate(startDate)
                        .endDate(endDate)
                        .billingDay(1)
                        .rentPrice(price)
                        .status("active")
                        .contractUrl("")
                        .createdBy(userId) // TODO: nếu có adminId thì dùng adminId
                        .createdAt(System.currentTimeMillis())
                        .build();

                long id = db.contractDao().insert(contract);

                Toast.makeText(this, "Tạo thành công!", Toast.LENGTH_SHORT).show();

                finish(); // ✅ quay lại màn trước

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo hợp đồng");
        }
    }

    private void showDatePicker(EditText editText) {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String date = String.format("%02d/%02d/%d", day, month + 1, year);
                    editText.setText(date);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private long convertToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}