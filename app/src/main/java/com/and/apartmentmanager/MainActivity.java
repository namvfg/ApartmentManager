package com.and.apartmentmanager;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.AppDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Thêm dòng này để khởi tạo DB và chạy seed

        new Thread(() -> {
            int count = AppDatabase
                    .getInstance(getApplicationContext())
                    .userDao()
                    .count();
            Log.d("DB_TEST", "count = " + count);
        }).start();



    }
}