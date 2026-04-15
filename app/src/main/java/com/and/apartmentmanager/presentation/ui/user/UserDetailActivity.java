package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.BlockRepository;
import com.and.apartmentmanager.data.repository.ContractRepository;
import com.and.apartmentmanager.data.repository.UnitRepository;
import com.and.apartmentmanager.data.repository.UserApartmentRepository;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.presentation.ui.admin.CreateContractActivity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class UserDetailActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvRoom, tvStatus, tvDuration, tvPrice, tvDaysLeft;
    ImageView btnBack;
    MaterialButton btnCreateContract, btnChat;

    UserRepository userRepository;
    UserApartmentRepository userApartmentRepository;
    UnitRepository unitRepository;
    BlockRepository blockRepository;
    ContractRepository contractRepository;

    int userId;
    int unitId = -1;
    int apartmentId = -1;

    ContractEntity currentContract; // lưu contract hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // init repository
        userRepository = new UserRepository(getApplication());
        userApartmentRepository = new UserApartmentRepository(getApplication());
        unitRepository = new UnitRepository(getApplication());
        blockRepository = new BlockRepository(getApplication());
        contractRepository = new ContractRepository(getApplication());

        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // bind view
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRoom = findViewById(R.id.tvRoom);
        tvStatus = findViewById(R.id.tvStatus);
        tvDuration = findViewById(R.id.tvDuration);
        tvPrice = findViewById(R.id.tvPrice);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);

        btnBack = findViewById(R.id.btnBack);
        btnCreateContract = findViewById(R.id.btnCreateContract);

        btnBack.setOnClickListener(v -> finish());

        btnCreateContract.setOnClickListener(v -> goToCreateContract());

        loadUser();
        loadContract();

        btnChat = findViewById(R.id.btnChat); // Ánh xạ từ XML
        btnChat.setOnClickListener(v -> goToChat());
    }

    // ✅ chuyển màn + truyền đủ data
    private void goToCreateContract() {
        if (unitId == -1) {
            Toast.makeText(this, "User chưa có phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentContract != null) {
            Toast.makeText(this, "User đã có hợp đồng", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateContractActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("unitId", unitId);
        intent.putExtra("apartmentId", apartmentId);

        startActivity(intent);
    }

    private void goToChat() {
        String name = tvName.getText().toString();
        String room = tvRoom.getText().toString();

        Toast.makeText(this, "Đang kết nối với " + name, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.and.apartmentmanager.presentation.ui.admin.AdminMainActivity.class);
        intent.putExtra("OPEN_CHAT", true);
        intent.putExtra("userId", (long) userId);
        intent.putExtra("userName", name);
        intent.putExtra("userRoom", room);

        // THÊM DÒNG NÀY: Truyền apartmentId qua Intent
        intent.putExtra("apartmentId", (long) apartmentId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // ✅ load user + unit + apartment
    private void loadUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = userRepository.getByIdSync(userId);
            UserApartmentEntity ua = userApartmentRepository.findByUserId(userId);

            if (user == null || ua == null) return;

            UnitEntity unit = unitRepository.getById(ua.getUnitId());
            BlockEntity block = blockRepository.getByIdSync(unit.getBlockId());

            unitId = unit.getId();

            // ✅ FIX: lấy apartmentId từ block
            apartmentId = block.getApartmentId();

            runOnUiThread(() -> {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvPhone.setText(user.getPhone());
                tvStatus.setText(ua.getStatus());
                tvRoom.setText(block.getName() + " - " + unit.getName());
            });
        });
    }

    // ✅ load contract + disable nút nếu có
    private void loadContract() {
        Executors.newSingleThreadExecutor().execute(() -> {
            ContractEntity c = contractRepository.getActiveByUser(userId);
            currentContract = c;

            runOnUiThread(() -> {
                if (c == null) {
                    tvDuration.setText("Chưa có HĐ");
                    tvPrice.setText("--");
                    tvDaysLeft.setText("--");

                    btnCreateContract.setEnabled(true);
                    return;
                }

                String duration = formatDate(c.getStartDate()) + " - " + formatDate(c.getEndDate());
                tvDuration.setText(duration);
                tvPrice.setText(formatMoney(c.getRentPrice()) + "đ/th");

                long days = (c.getEndDate() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
                if (days < 0) days = 0;

                tvDaysLeft.setText(days + " ngày");

//                // ❗ disable nút
//                btnCreateContract.setEnabled(false);
            });
        });
    }

    private String formatDate(long time) {
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                .format(new Date(time));
    }

    private String formatMoney(double money) {
        return String.format("%,.0f", money);
    }
}