package com.and.apartmentmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.ContractEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceItemEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GenerateInvoiceWorker extends Worker {

    public GenerateInvoiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Mở kết nối đến Database
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        try {
            // --- BƯỚC 1: XEM HÔM NAY LÀ NGÀY MẤY ---
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Java đếm tháng từ 0 nên phải +1
            int year = calendar.get(Calendar.YEAR);
            String invoiceMonth = year + "-" + String.format("%02d", month); // VD: "2026-04"

            Log.d("Worker", "Bắt đầu quét Hợp đồng để đẻ hóa đơn cho ngày: " + today);

            List<ContractEntity> allContracts = db.contractDao().getAllContracts();

            if (allContracts == null || allContracts.isEmpty()) {
                Log.d("Worker", "Chưa có hợp đồng nào trong Database!");
                return Result.success();
            }

            // --- BƯỚC 3: DUYỆT TỪNG HỢP ĐỒNG VÀ ĐẺ HÓA ĐƠN ---
            for (ContractEntity contract : allContracts) {

                // Kiểm tra: Hợp đồng còn hạn VÀ ngày thu tiền (billingDay) bằng hôm nay
                if ("active".equals(contract.getStatus()) && contract.getBillingDay() == today) {

                    Log.d("Worker", "Phát hiện hợp đồng đến hạn! Đang tạo cho phòng: " + contract.getUnitId());

                    // ===============================================================
                    // ===============================================================
                    // [NGHIỆP VỤ XỊN]: KIỂM TRA XEM CÓ PHẢI THÁNG CUỐI HỢP ĐỒNG KHÔNG?
                    // ===============================================================
                    boolean isLastMonth = false;
                    try {
                        // Vì endDate là kiểu 'long' (timestamp), ta kiểm tra lớn hơn 0
                        if (contract.getEndDate() > 0) {
                            // Dùng SimpleDateFormat để chuyển số long thành chuỗi "yyyy-MM"
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault());
                            String contractEndMonth = sdf.format(new java.util.Date(contract.getEndDate()));

                            // So sánh tháng của hợp đồng với tháng đang tạo hóa đơn
                            if (invoiceMonth.equals(contractEndMonth)) {
                                isLastMonth = true;
                                Log.d("Worker", "🤖 CHÚ Ý: Đây là tháng CUỐI CÙNG của hợp đồng phòng " + contract.getUnitId() + ". MIỄN PHÍ TIỀN THUÊ!");
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Worker", "Lỗi kiểm tra ngày kết thúc hợp đồng", e);
                    }

                    // Nếu là tháng cuối, tiền thuê = 0. Ngược lại, lấy giá gốc.
                    double finalRentPrice = isLastMonth ? 0 : contract.getRentPrice();
                    String noteText = isLastMonth ? "Hóa đơn tháng cuối (Miễn phí tiền phòng)" : "Hệ thống tạo tự động";

                    // Đẻ Hóa đơn chính (Nháp - draft)
                    InvoiceEntity newInvoice = new InvoiceEntity(
                            0, // ID để 0 cho SQLite tự tăng
                            contract.getApartmentId(),
                            contract.getUnitId(),
                            invoiceMonth,
                            finalRentPrice, // Chèn tiền thuê đã được xử lý (0đ nếu là tháng cuối)
                            "draft",
                            System.currentTimeMillis(),
                            null,
                            null,
                            noteText
                    );

                    // Gọi lệnh Insert để lưu Invoice
                    long invoiceId = db.invoiceDao().insert(newInvoice);

                    // CHỈ TẠO INVOICE ITEM TIỀN THUÊ NẾU KHÔNG PHẢI THÁNG CUỐI
                    if (!isLastMonth) {
                        List<InvoiceItemEntity> items = new ArrayList<>();
                        items.add(new InvoiceItemEntity(
                                0, (int) invoiceId, 1,
                                "Tiền thuê phòng cố định",
                                finalRentPrice,
                                null,
                                finalRentPrice,
                                "rent"
                        ));
                        db.invoiceItemDao().insertAll(items);
                    }
                }
            }

            Log.d("Worker", "Quá trình quét và đẻ hóa đơn đã HOÀN TẤT!");
            return Result.success();

        } catch (Exception e) {
            Log.e("Worker", "Lỗi trong quá trình tạo hóa đơn tự động", e);
            return Result.failure();
        }
    }
}