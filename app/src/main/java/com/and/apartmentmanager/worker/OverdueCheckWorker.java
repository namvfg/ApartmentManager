package com.and.apartmentmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.and.apartmentmanager.data.local.AppDatabase;

import java.util.Calendar;

public class OverdueCheckWorker extends Worker {

    public OverdueCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Worker", "🤖 Robot 2: Bắt đầu đi tuần tra đòi nợ...");

        // Mở kết nối Database
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        try {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            // NGHIỆP VỤ: Quá ngày 15 hàng tháng là múc!
            if (currentDay > 15) {
                Log.d("Worker", "🤖 Đã qua hạn 15. Tiến hành quét và khóa hóa đơn chưa đóng...");

                // Gọi lệnh cập nhật 1 phát ăn ngay xuống Database
                db.invoiceDao().updateStatusToOverdue();

                Log.d("Worker", "🤖 Đã cập nhật xong các hóa đơn Quá hạn!");
            } else {
                Log.d("Worker", "🤖 Hôm nay ngày " + currentDay + ", cư dân vẫn còn hạn đóng tiền.");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("Worker", "🤖 Robot 2 bị lỗi: " + e.getMessage());
            return Result.retry();
        }
    }
}