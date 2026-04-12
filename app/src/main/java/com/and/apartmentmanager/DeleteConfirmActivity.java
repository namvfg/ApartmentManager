package com.and.apartmentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.local.entity.BlockEntity;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;
import com.and.apartmentmanager.data.repository.ApartmentRepository;
import com.and.apartmentmanager.data.repository.BlockRepository;
import com.and.apartmentmanager.data.repository.ContractRepository;
import com.and.apartmentmanager.data.repository.UnitRepository;
import com.and.apartmentmanager.data.repository.UserApartmentRepository;
import com.and.apartmentmanager.data.repository.UserRepository;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteConfirmActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvRoom, tvApartmentName, tvRequestDate, tvDebt, tvContractStatus, tvAvatarPlaceholder;
    Button btnKeep, btnDelete;
    ImageView btnBack;
    UserRepository userRepository;
    UserApartmentRepository userApartmentRepository;
    UnitRepository unitRepository;
    BlockRepository blockRepository;
    ApartmentRepository apartmentRepository;
    ContractRepository contractRepository;
    int userId;
    UserEntity user;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_confirm);

        userRepository = new UserRepository(getApplication());
        userApartmentRepository = new UserApartmentRepository(getApplication());
        unitRepository = new UnitRepository(getApplication());
        blockRepository = new BlockRepository(getApplication());
        apartmentRepository = new ApartmentRepository(getApplication());
        contractRepository = new ContractRepository(getApplication());

        userId = getIntent().getIntExtra("userId", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Thiếu thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRoom = findViewById(R.id.tvRoom);
        tvApartmentName = findViewById(R.id.tvApartmentName);
        tvRequestDate = findViewById(R.id.tvRequestDate);
        tvDebt = findViewById(R.id.tvDebt);
        tvContractStatus = findViewById(R.id.tvContractStatus);
        tvAvatarPlaceholder = findViewById(R.id.tvAvatarPlaceholder);
        btnKeep = findViewById(R.id.btnKeep);
        btnDelete = findViewById(R.id.btnDelete);

        btnKeep.setEnabled(false);
        btnDelete.setEnabled(false);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadUser();

        btnKeep.setOnClickListener(v -> {
            if (user == null) return;
            io.execute(() -> {
                user.setUserDelete(false);
                userRepository.updateBlocking(user);
                runOnUiThread(this::finish);
            });
        });

        btnDelete.setOnClickListener(v -> {
            if (user == null) return;
            io.execute(() -> {
                user.setUserDelete(false);
                user.setDeleted(true);
                user.setDeletedAt(System.currentTimeMillis());
                userRepository.updateBlocking(user);
                runOnUiThread(this::finish);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }

    private void loadUser() {
        io.execute(() -> {

            user = userRepository.getByIdSync(userId);
            if (user == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy user", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }
            UserApartmentEntity ua = userApartmentRepository.findByUserId(userId);

            String roomText = "—";
            String apartmentName = "—";

            if (ua != null) {
                UnitEntity unit = unitRepository.getById(ua.getUnitId());

                if (unit != null) {
                    BlockEntity block = blockRepository.getByIdSync(unit.getBlockId());

                    if (block != null) {
                        roomText = "Block " + block.getName() + " · " + unit.getName();
                    } else {
                        roomText = unit.getName();
                    }
                }

                ApartmentEntity apartment = apartmentRepository.getByIdSync(ua.getApartmentId());
                if (apartment != null) {
                    apartmentName = apartment.getName();
                }
            }


            ContractEntity contract = contractRepository.getActiveByUser(userId);

            String contractText = "Chưa có";
            if (contract != null) {
                long days = (contract.getEndDate() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);

                if (days < 0) contractText = "Đã hết hạn";
                else if (days < 30) contractText = "Còn " + days + " ngày";
                else contractText = "Đang hiệu lực";
            }

            // ===== UI =====
            String finalRoom = roomText;
            String finalApartment = apartmentName;
            String finalContract = contractText;

            runOnUiThread(() -> {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvRoom.setText(finalRoom);
                tvApartmentName.setText(finalApartment);
                tvContractStatus.setText(finalContract);

                tvRequestDate.setText("—"); // TODO
                tvDebt.setText("0đ");       // TODO

                btnKeep.setEnabled(true);
                btnDelete.setEnabled(true);
            });
        });
    }
}
