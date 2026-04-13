package com.and.apartmentmanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.presentation.ui.user.invoice.UserInvoiceListFragment;
import com.and.apartmentmanager.presentation.ui.admin.ContractDetailActivity;

import android.content.Intent;
import com.and.apartmentmanager.data.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UserRepository userRepository = new UserRepository(getApplication());
        // Khởi tạo DB và chạy seed
        new Thread(() -> {
            int count = userRepository.count();
            Log.d("DB_TEST", "count = " + count);
        }).start();

        // Padding EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new UserInvoiceListFragment())
                    .commit();
        }

        // =========================================================
        // [P4 - HÓA ĐƠN] KHỞI ĐỘNG ROBOT CHẠY NGẦM BẰNG WORKMANAGER
        // =========================================================
        try {
            // 1. Robot sinh hóa đơn: Chạy lặp lại mỗi 24 tiếng
            androidx.work.PeriodicWorkRequest generateWork =
                    new androidx.work.PeriodicWorkRequest.Builder(
                            com.and.apartmentmanager.worker.GenerateInvoiceWorker.class,
                            24, java.util.concurrent.TimeUnit.HOURS)
                            .build();

            // 2. Robot kiểm tra quá hạn: Chạy lặp lại mỗi 24 tiếng
            androidx.work.PeriodicWorkRequest overdueWork =
                    new androidx.work.PeriodicWorkRequest.Builder(
                            com.and.apartmentmanager.worker.OverdueCheckWorker.class,
                            24, java.util.concurrent.TimeUnit.HOURS)
                            .build();

            // 3. Xếp lịch vào hệ thống Android (KEEP: Đảm bảo không bị tạo trùng lặp nếu user tắt mở app)
            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "GenerateInvoiceJob",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    generateWork
            );

            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "OverdueCheckJob",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    overdueWork
            );

            Log.d("WorkManager", "Đã đặt báo thức thành công cho 2 Robot!");
        } catch (Exception e) {
            Log.e("WorkManager", "Lỗi khởi động Robot: " + e.getMessage());
        }
        // =========================================================
    }
    // Bắt buộc phải có hàm này để cập nhật đường link MoMo gửi về
    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Ghi đè Intent cũ bằng Intent mới chứa kết quả của MoMo
    }
}