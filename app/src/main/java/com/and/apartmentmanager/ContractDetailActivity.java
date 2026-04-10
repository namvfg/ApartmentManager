package com.and.apartmentmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.UserApartmentEntity;
import com.and.apartmentmanager.data.local.entity.UserEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class ContractDetailActivity extends AppCompatActivity {

    TextView tvStartDate, tvEndDate, tvRemainDays;
    TextView tvRoom, tvRentPrice, tvFeeDay, tvOwner;
    TextView tvStatus;

    LinearLayout layoutRoommates;

    AppDatabase db;

    int userId = 3; //  test cư dân Lê Văn Cường

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);

        db = AppDatabase.getInstance(this);

        initView();
        loadData();
    }

    private void initView() {
        tvStatus = findViewById(R.id.tvStatus);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvRemainDays = findViewById(R.id.tvRemainDays);

        tvRoom = findViewById(R.id.tvRoom);
        tvRentPrice = findViewById(R.id.tvRentPrice);
        tvFeeDay = findViewById(R.id.tvFeeDay);
        tvOwner = findViewById(R.id.tvOwner);

        layoutRoommates = findViewById(R.id.layoutRoommates);
    }

    private void loadData() {

        Executors.newSingleThreadExecutor().execute(() -> {

            ContractEntity contract =
                    db.contractDao().getByUserId(userId);

            UserApartmentEntity ua =
                    db.userApartmentDao().getActiveByUserId(userId);

            List<UserApartmentEntity> list =
                    db.userApartmentDao().getByApartmentId(ua.getApartmentId());

            runOnUiThread(() -> {

                // STATUS
                tvStatus.setText(contract.getStatus());
                tvStartDate.setText(String.valueOf(contract.getStartDate()));
                tvEndDate.setText(String.valueOf(contract.getEndDate()));
                tvRemainDays.setText("...");

                // RENT INFO
                tvRoom.setText("Unit: " + ua.getUnitId());
                tvRentPrice.setText(contract.getRentPrice() + " đ/tháng");
                tvFeeDay.setText("Ngày thu: " + contract.getBillingDay());
                tvOwner.setText("User ID: " + userId);

                // ROOMMATES
                layoutRoommates.removeAllViews();

                for (UserApartmentEntity item : list) {

                    UserEntity user = db.userDao().getByIdSync(item.getUserId());

                    View v = LayoutInflater.from(this)
                            .inflate(android.R.layout.simple_list_item_2, layoutRoommates, false);

                    TextView t1 = v.findViewById(android.R.id.text1);
                    TextView t2 = v.findViewById(android.R.id.text2);

                    t1.setText(user.getName());
                    t2.setText(item.getUserId() == userId ? "Chủ hợp đồng" : "Cư dân");

                    layoutRoommates.addView(v);
                }
            });
        });
    }
}