package com.and.apartmentmanager.presentation.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ServiceEntity;

import java.util.List;

public class ServiceActivity extends AppCompatActivity {

    LinearLayout layoutFixed, layoutVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        layoutFixed = findViewById(R.id.layoutFixed);
        layoutVariable = findViewById(R.id.layoutVariable);

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {
            List<ServiceEntity> all = db.serviceDao().getAll();
            Log.d("CHECK_DB", "all size = " + all.size());
            List<ServiceEntity> services = db.serviceDao().getServicesByApartment(1);

            Log.d("SERVICE", "size = " + services.size());
            runOnUiThread(() -> {
                for (ServiceEntity s : services) {

                    View item = getLayoutInflater().inflate(R.layout.item_service, null);

                    TextView tvName = item.findViewById(R.id.tvName);
                    TextView tvDesc = item.findViewById(R.id.tvDesc);
                    TextView tvTag = item.findViewById(R.id.tvTag);

                    tvName.setText(s.getName());
                    tvDesc.setText(s.getDescription());

                    String name = s.getName().toLowerCase();

                    String pricing;

                    //  Điện + Nước
                    if (name.contains("điện") || name.contains("nước")) {
                        pricing = "Biến đổi";
                        layoutFixed.addView(item);
                    }
                    //  Rác + Gửi xe
                    else {
                        pricing = "Cố định";
                        layoutVariable.addView(item);
                    }

                    tvTag.setText(pricing);

                    item.setOnClickListener(v -> {

                        String nameLower = s.getName().toLowerCase().trim();

                        if (nameLower.contains("điện")) {
                            Intent intent = new Intent(ServiceActivity.this, UpdatePriceElectricActivity.class);
                            intent.putExtra("service_name", s.getName());
                            startActivity(intent);
                        }

                        else if (nameLower.contains("nước")) {
                            Intent intent = new Intent(ServiceActivity.this, UpdatePriceWaterActivity.class);
                            intent.putExtra("service_name", s.getName());
                            startActivity(intent);
                        }
                    });
                }
            });
        }).start();
    }
}