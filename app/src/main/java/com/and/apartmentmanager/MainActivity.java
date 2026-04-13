package com.and.apartmentmanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.AppDatabase;
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

        // Click listener cho nút mở fragment
        // MainActivity.java
        Button btnOpenFragment = findViewById(R.id.btn_open_fragment);
        btnOpenFragment.setOnClickListener(v -> {
            // Lệnh chuyển sang màn hình CreateContractActivity
//            Intent intent = new Intent(MainActivity.this, CreateContractActivity.class);
//            startActivity(intent);
            // // Lệnh chuyển sang màn hình ServiceActivity
            Intent intent = new Intent(MainActivity.this, ContractDetailActivity.class);
            startActivity(intent);
        });

    }
}
