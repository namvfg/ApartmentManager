package com.and.apartmentmanager.presentation.ui.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.adapter.UserAdapter;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.UnitEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class UserActivity extends AppCompatActivity {
    ImageView btnBack;
    EditText edtSearch;
    RecyclerView rvUser;
    TextView tvCount;
    UserAdapter adapter;
    AppDatabase db;
    int apartmentId, unitId;

    List<UserEntity> originalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        rvUser = findViewById(R.id.rvUser);
        tvCount = findViewById(R.id.tvCount);

        btnBack.setOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);

        apartmentId = getIntent().getIntExtra("apartmentId", -1);
        unitId = getIntent().getIntExtra("unitId", -1);

        rvUser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(db, this,unitId,apartmentId);
        rvUser.setAdapter(adapter);

        if (unitId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                UnitEntity unit = db.unitDao().getById(unitId);

                runOnUiThread(() -> {
                    db.userDao().getByUnit(unitId).observe(this, list -> {

                        originalList.clear();
                        originalList.addAll(list);

                        adapter.setData(list);

                        String unitName = (unit != null) ? unit.getName() : "";
                        tvCount.setText(list.size() + " người - Phòng " + unitName);
                    });
                });
            });
        } else {
            db.apartmentDao().getById(apartmentId).observe(this, ap -> {
                String apName = (ap != null) ? ap.getName() : "";
                db.userDao().getByApartment(apartmentId).observe(this, list -> {
                    originalList.clear();
                    originalList.addAll(list);
                    adapter.setData(list);
                    tvCount.setText(list.size() + " cư dân - " + apName);
                });
            });
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().toLowerCase().trim();
                List<UserEntity> filtered = new ArrayList<>();
                for (UserEntity u : originalList) {
                    String name = u.getName() != null ? u.getName().toLowerCase() : "";
                    if (name.contains(keyword)) {
                        filtered.add(u);
                    }
                }
                adapter.setData(filtered);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }


}
