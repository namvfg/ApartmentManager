package com.and.apartmentmanager.presentation.ui.user;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;

import java.util.concurrent.Executors;

public class JoinActivity extends AppCompatActivity {

    EditText edtCode;
    Button btnJoin;
    AppDatabase db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        db = AppDatabase.getInstance(this);

        edtCode = findViewById(R.id.edtCode);
        btnJoin = findViewById(R.id.btnJoin);

        // test user
        userId = 3;

        btnJoin.setOnClickListener(v -> joinByCode());

    }
    private void joinByCode() {

        String inputCode = edtCode.getText().toString().trim();

        if (inputCode.isEmpty()) {
            Toast.makeText(this, "Nhập mã trước", Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {

            InviteCodeEntity code = db.inviteCodeDao().findByCode(inputCode);

            if (code == null) {
                show("Mã không tồn tại");
                return;
            }

            if (code.isUsed()) {
                show("Mã đã được sử dụng");
                return;
            }

            if (System.currentTimeMillis() > code.getExpiresAt()) {
                show("Mã đã hết hạn");
                return;
            }

            UserApartmentEntity existing =
                    db.userApartmentDao().findByUserId(userId);

            if (existing != null) {
                show("Bạn đã tham gia rồi");
                return;
            }

            // 👉 INSERT
            UserApartmentEntity ua = new UserApartmentEntity();
            ua.setUserId(userId);
            ua.setApartmentId(code.getApartmentId());
            ua.setUnitId(code.getUnitId());
            ua.setStatus("active");

            db.userApartmentDao().insert(ua);

            // 👉 UPDATE CODE
            code.setUsed(true);
            code.setUsedBy(userId);
            db.inviteCodeDao().update(code);

            show("Tham gia thành công");

            runOnUiThread(() -> finish());
        });
    }

    private void show(String msg) {
        runOnUiThread(() ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }
}