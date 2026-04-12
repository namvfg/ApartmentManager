package com.and.apartmentmanager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class UserDetailActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvRoom, tvStatus, tvDuration, tvPrice, tvDaysLeft;
    ImageView btnBack;
    UserRepository userRepository;
    UserApartmentRepository userApartmentRepository;
    UnitRepository unitRepository;
    BlockRepository blockRepository;
    ContractRepository contractRepository;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userRepository = new UserRepository(getApplication());
        userApartmentRepository = new UserApartmentRepository(getApplication());
        unitRepository = new UnitRepository(getApplication());
        blockRepository = new BlockRepository(getApplication());
        contractRepository = new ContractRepository(getApplication());

        userId = getIntent().getIntExtra("userId", -1);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRoom = findViewById(R.id.tvRoom);
        tvStatus = findViewById(R.id.tvStatus);

        tvDuration = findViewById(R.id.tvDuration);
        tvPrice = findViewById(R.id.tvPrice);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        loadUser();
        loadContract(userId);
    }

    private void loadUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = userRepository.getByIdSync(userId);
            UserApartmentEntity ua = userApartmentRepository.findByUserId(userId);
            if (user == null || ua == null) return;
            UnitEntity unit = unitRepository.getById(ua.getUnitId());
            BlockEntity block = blockRepository.getByIdSync(unit.getBlockId());
            runOnUiThread(() -> {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvPhone.setText(user.getPhone());
                tvStatus.setText(ua.getStatus());
                tvRoom.setText("Block " + block.getName() + " - " + unit.getName());
            });
        });
    }

    private void loadContract(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ContractEntity c = contractRepository.getActiveByUser(userId);
            runOnUiThread(() -> {
                if (c == null) {
                    tvDuration.setText("Chưa có HĐ");
                    tvPrice.setText("--");
                    tvDaysLeft.setText("--");
                    return;
                }
                String duration = formatDate(c.getStartDate()) + " - " + formatDate(c.getEndDate());
                tvDuration.setText(duration);
                tvPrice.setText(formatMoney(c.getRentPrice()) + "đ/th");
                long now = System.currentTimeMillis();
                long diff = c.getEndDate() - now;
                long days = diff / (1000 * 60 * 60 * 24);
                if (days < 0) days = 0;
                tvDaysLeft.setText(days + " ngày");
            });
        });
    }

    private String formatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return sdf.format(new Date(time));
    }

    private String formatMoney(double money) {
        return String.format("%,.0f", money);
    }
}
