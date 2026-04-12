package com.and.apartmentmanager.presentation.ui.admin.apartment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.InviteCodeEntity;

import java.util.concurrent.Executors;

public class InviteActivity extends AppCompatActivity {


    TextView tvUnit, tvCode, tvExpire;
    Button btnGenerate, btnCopy, btnShare;
    ImageView btnBack;
    AppDatabase db;

    int unitId, apartmentId, adminId;
    String currentCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invite);

        db = AppDatabase.getInstance(this);
        unitId = getIntent().getIntExtra("unitId", -1);
        apartmentId = getIntent().getIntExtra("apartmentId", -1);
        adminId = getIntent().getIntExtra("adminId", -1);

        unitId = getIntent().getIntExtra("unitId", -1);
        apartmentId = getIntent().getIntExtra("apartmentId", -1);
        adminId = getIntent().getIntExtra("adminId", -1);

        tvUnit = findViewById(R.id.tvUnit);
        tvCode = findViewById(R.id.tvCode);
        tvExpire = findViewById(R.id.tvExpire);

        btnGenerate = findViewById(R.id.btnGenerate);
        btnCopy = findViewById(R.id.btnCopy);
        btnShare = findViewById(R.id.btnShare);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnGenerate.setOnClickListener(v -> generateInviteCode());
        btnCopy.setOnClickListener(v -> copyCode());
        btnShare.setOnClickListener(v -> shareCode());

        loadLatestCode();
    }
    private void generateInviteCode() {

        Executors.newSingleThreadExecutor().execute(() -> {

            String codeStr = generateCode();

            InviteCodeEntity code = new InviteCodeEntity();
            code.setCode(codeStr);
            code.setUnitId(unitId);
            code.setApartmentId(apartmentId);
            code.setAdminId(adminId);

            long now = System.currentTimeMillis();
            code.setExpiresAt(now + 7L * 24 * 60 * 60 * 1000);
            code.setUsed(false);

            db.inviteCodeDao().insert(code);

            runOnUiThread(() -> {
                loadLatestCode();
            });

        });
    }

    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = (int)(Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void copyCode() {
        if (currentCode.isEmpty()) return;

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("code", currentCode);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Đã copy", Toast.LENGTH_SHORT).show();
    }
    private void shareCode() {
        if (currentCode.isEmpty()) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Mã mời: " + currentCode);

        startActivity(Intent.createChooser(intent, "Chia sẻ"));
    }

    private void loadLatestCode() {

        Executors.newSingleThreadExecutor().execute(() -> {

            InviteCodeEntity code = db.inviteCodeDao()
                    .getLatestValidCode(unitId, System.currentTimeMillis());

            runOnUiThread(() -> {

                if (code != null) {
                    currentCode = code.getCode();
                    tvCode.setText(currentCode);
                    tvExpire.setText("Còn hiệu lực");
                } else {
                    tvCode.setText("------");
                    tvExpire.setText("Chưa có mã");
                }

            });

        });
    }
}