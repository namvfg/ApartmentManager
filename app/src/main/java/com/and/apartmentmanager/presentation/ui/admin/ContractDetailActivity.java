package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ContractDetailActivity extends AppCompatActivity {

    TextView tvOwner, tvRentPrice, tvRoom, tvOwnerwith;
    TextView tvStartDate, tvEndDate, tvRemainDays;
    TextView btnViewContract;

    String contractUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);

        tvOwner = findViewById(R.id.tvOwner);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvRemainDays = findViewById(R.id.tvRemainDays);
        tvRentPrice = findViewById(R.id.tvRentPrice);
        tvRoom = findViewById(R.id.tvRoom);
        btnViewContract = findViewById(R.id.btnViewContract);

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {

            UserEntity user = db.userDao().getByIdSync(3);
            ContractEntity contract = db.contractDao().getContract(3, 1, 1);

            if (contract == null) return;

            //  LẤY URL PDF
            contractUrl = contract.getContractUrl();

            // Unit
            UnitEntity unit = db.unitDao().getById(contract.getUnitId());

            // Block
            BlockEntity block = null;
            if (unit != null) {
                block = db.blockDao().getById(unit.getBlockId());
            }

            BlockEntity finalBlock = block;
            UnitEntity finalUnit = unit;

            runOnUiThread(() -> {

                // ===== OWNER =====
                if (user != null) {
                    tvOwner.setText("Chủ hợp đồng    " + user.getName());

                } else {
                    tvOwner.setText("Chủ hợp đồng    Không có dữ liệu");
                }

                // ===== DATE =====
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                String start = sdf.format(new Date(contract.getStartDate()));
                String end = sdf.format(new Date(contract.getEndDate()));

                tvStartDate.setText(start);
                tvEndDate.setText(end);

                // ===== REMAIN DAYS =====
                long diff = contract.getEndDate() - System.currentTimeMillis();
                long days = TimeUnit.MILLISECONDS.toDays(diff);

                tvRemainDays.setText(days + " ngày");

                // ===== PRICE =====
                tvRentPrice.setText("Tiền thuê:       " +
                        String.format(Locale.getDefault(), "%,.0fđ/tháng", contract.getRentPrice()));

                // ===== ROOM =====
                if (finalBlock != null && finalUnit != null) {
                    tvRoom.setText("Phòng        " +
                            finalBlock.getName() + " - " + finalUnit.getName());
                } else {
                    tvRoom.setText("Phòng        Không có dữ liệu");
                }

                // ===== CLICK PDF =====
                btnViewContract.setOnClickListener(v -> {

                    if (contractUrl == null || contractUrl.isEmpty()) {
                        Toast.makeText(this, "Không có file hợp đồng", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(contractUrl), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Không mở được PDF", Toast.LENGTH_SHORT).show();
                    }
                });

            });

        }).start();
    }
}