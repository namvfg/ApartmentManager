package com.and.apartmentmanager;

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

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ContractEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateContractActivity extends AppCompatActivity {

    private AppDatabase db;

    // Chọn file PDF (hiện chưa dùng để lưu)
    private final ActivityResultLauncher<Intent> pickPdfLauncher = registerForActivityResult(
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

        // Khởi tạo DB
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "apartment_db"
        ).allowMainThreadQueries().build();

        // Ánh xạ View
        EditText edtStartDate = findViewById(R.id.edtStartDate);
        EditText edtEndDate = findViewById(R.id.edtEndDate);
        EditText edtPrice = findViewById(R.id.edtPrice);
        Button btnCreate = findViewById(R.id.btnCreate);
        ImageButton btnBack = findViewById(R.id.btnBack);
        LinearLayout layoutUpload = findViewById(R.id.layoutUpload);

        // Chọn PDF (chưa dùng)
        if (layoutUpload != null) {
            layoutUpload.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                pickPdfLauncher.launch(intent);
            });
        }

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Chọn ngày
        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        // 👉 NÚT TẠO HỢP ĐỒNG
        btnCreate.setOnClickListener(v -> {
            try {
                // Lấy dữ liệu
                String priceStr = edtPrice.getText().toString().replace(".", "");
                double price = Double.parseDouble(priceStr);

                long startDate = convertToMillis(edtStartDate.getText().toString());
                long endDate = convertToMillis(edtEndDate.getText().toString());

                // Tạo object
                ContractEntity contract = ContractEntity.builder()
                        .userId(1)
                        .apartmentId(1)
                        .unitId(1)
                        .startDate(startDate)
                        .endDate(endDate)
                        .billingDay(1)
                        .rentPrice(price)
                        .status("active")
                        .contractUrl("")
                        .createdBy(1)
                        .createdAt(System.currentTimeMillis())
                        .build();

                // Lưu DB
                long id = db.contractDao().insert(contract);

                Toast.makeText(this, "Tạo thành công ID = " + id, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo hợp đồng");
        }
    }

    private void showDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), yearSelected);
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private long convertToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date.getTime();
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