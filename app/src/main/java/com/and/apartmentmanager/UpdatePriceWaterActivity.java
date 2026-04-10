package com.and.apartmentmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ServicePriceHistoryEntity;

import java.util.Calendar;

public class UpdatePriceWaterActivity extends AppCompatActivity {

    EditText edtPrice, edtDate;
    CheckBox cbNextCycle;
    Button btnUpdate;
    TextView tvCurrentPrice;
    ImageView btnBack;

    AppDatabase db;

    private static final int SERVICE_WATER_ID = 2; // 👈 NƯỚC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_price_water);

        // ánh xạ
        edtPrice = findViewById(R.id.edtPrice);
        edtDate = findViewById(R.id.edtDate);
        cbNextCycle = findViewById(R.id.cbNextCycle);
        btnUpdate = findViewById(R.id.btnUpdate);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        btnBack = findViewById(R.id.btnBack);

        db = AppDatabase.getInstance(this);

        // back
        btnBack.setOnClickListener(v -> finish());

        // load giá hiện tại
        loadCurrentPrice();

        // chọn ngày
        edtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        edtDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        // update
        btnUpdate.setOnClickListener(v -> updatePrice());
    }

    // =========================
    // LOAD GIÁ HIỆN TẠI
    // =========================
    private void loadCurrentPrice() {
        new Thread(() -> {

            ServicePriceHistoryEntity current =
                    db.servicePriceHistoryDao().getCurrentPrice(SERVICE_WATER_ID);

            runOnUiThread(() -> {
                if (current != null) {
                    tvCurrentPrice.setText(
                            "Giá hiện tại: " + current.getPrice() + "đ/m³"
                    );
                } else {
                    tvCurrentPrice.setText("Chưa có giá");
                }
            });

        }).start();
    }

    // =========================
    // UPDATE GIÁ
    // =========================
    private void updatePrice() {
        String priceStr = edtPrice.getText().toString().trim();
        boolean nextCycle = cbNextCycle.isChecked();

        if (priceStr.isEmpty()) {
            edtPrice.setError("Nhập giá");
            return;
        }

        double newPrice;

        try {
            newPrice = Double.parseDouble(priceStr);
        } catch (Exception e) {
            edtPrice.setError("Giá không hợp lệ");
            return;
        }

        new Thread(() -> {

            // 1. tắt giá cũ
            ServicePriceHistoryEntity current =
                    db.servicePriceHistoryDao().getCurrentPrice(SERVICE_WATER_ID);

            if (current != null) {
                current.setActive(false);
                db.servicePriceHistoryDao().update(current);
            }

            // 2. thời gian hiệu lực
            long effectiveTime = System.currentTimeMillis();

            if (nextCycle) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                effectiveTime = cal.getTimeInMillis();
            }

            // 3. insert giá mới
            ServicePriceHistoryEntity newEntity =
                    ServicePriceHistoryEntity.builder()
                            .serviceId(SERVICE_WATER_ID)
                            .price(newPrice)
                            .effectiveFrom(effectiveTime)
                            .applyFromNextCycle(nextCycle)
                            .isActive(true)
                            .changedBy(1)
                            .changedAt(System.currentTimeMillis())
                            .build();

            db.servicePriceHistoryDao().insert(newEntity);

            // 4. update UI
            runOnUiThread(() -> {
                tvCurrentPrice.setText(
                        "Giá hiện tại: " + newPrice + "đ/m³"
                );

                edtPrice.setText("");
                edtDate.setText("");

                Toast.makeText(
                        UpdatePriceWaterActivity.this,
                        "Cập nhật giá nước thành công",
                        Toast.LENGTH_SHORT
                ).show();
            });

        }).start();
    }
}