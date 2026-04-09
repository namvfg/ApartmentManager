package com.and.apartmentmanager.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.helper.NotificationHelper;

import java.util.List;

public class ContractExpiryWorker extends Worker {

    public ContractExpiryWorker(@NonNull Context context,
                                @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // 30 ngày tính bằng ms
            long thirtyDays = 30L * 24 * 60 * 60 * 1000;
            long deadline   = System.currentTimeMillis() + thirtyDays;

            // Lấy tất cả contract active sắp hết hạn
            List<ContractEntity> expiring = AppDatabase.getInstance(getApplicationContext())
                    .contractDao()
                    .getExpiringSoon(deadline, System.currentTimeMillis());

            for (ContractEntity contract : expiring) {
                // Gửi thông báo cho User (chủ hợp đồng)
                NotificationHelper.sendAuto(
                        getApplicationContext(),
                        NotificationHelper.Type.CONTRACT_EXPIRING,
                        contract.getUserId(),        // gửi cho user cụ thể
                        contract.getApartmentId(),
                        contract.getId()
                );

                // Gửi thông báo cho Admin
                NotificationHelper.sendAuto(
                        getApplicationContext(),
                        NotificationHelper.Type.CONTRACT_EXPIRING,
                        -1,                    // -1 = toàn chung cư (Admin sẽ thấy)
                        contract.getApartmentId(),
                        contract.getId()
                );
            }

            return Result.success();

        } catch (Exception e) {
            return Result.failure();
        }
    }
}