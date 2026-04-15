package com.and.apartmentmanager.presentation.ui.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.repository.UserRepository;
import com.and.apartmentmanager.presentation.adapter.UserAdapter;

public class DeleteUserActivity extends AppCompatActivity {

    RecyclerView rvUser;
    UserAdapter adapter;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        userRepository = new UserRepository(getApplication());

        rvUser = findViewById(R.id.rvUser);
        rvUser.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(getApplication());
        rvUser.setAdapter(adapter);

        userRepository.getUserRequestDelete().observe(this, list -> adapter.setData(list));

        adapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(this, DeleteConfirmActivity.class);
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });
    }
}
