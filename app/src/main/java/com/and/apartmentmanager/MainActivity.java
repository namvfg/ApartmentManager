package com.and.apartmentmanager;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UserRepository userRepository = new UserRepository(getApplication());
        new Thread(() -> {
            int count = userRepository.count();
            Log.d("DB_TEST", "count = " + count);
        }).start();
    }
}
