package com.and.apartmentmanager.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.InvoiceItemDao;
import com.and.apartmentmanager.data.local.dto.UnitBlockDTO;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceItemEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvoiceRepository {
    private final InvoiceDao invoiceDao;
    private final InvoiceItemDao itemDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public InvoiceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        invoiceDao = db.invoiceDao();
        itemDao = db.invoiceItemDao();
    }

    // Insert invoice + items cùng lúc trong 1 transaction
    public void insertWithItems(InvoiceEntity invoice, List<InvoiceItemEntity> items) {
        executor.execute(() -> {
            long invoiceId = invoiceDao.insert(invoice);
            for (InvoiceItemEntity item : items) {
                item.setInvoiceId((int) invoiceId);
            }
            itemDao.insertAll(items);
        });
    }
    // Lấy danh sách hóa đơn theo ID căn hộ
    public LiveData<List<InvoiceEntity>> getInvoicesByUnitId(int unitId) {
        return invoiceDao.getByUnitId(unitId);
    }

    // Blocking — dùng trong background thread
    public InvoiceEntity  getLatestByUnitIdBlocking(long unitId) {
        return invoiceDao.getLatestByUnitId(unitId);
    }

    // Lấy danh sách khoản thu của một hóa đơn
    public LiveData<List<InvoiceItemEntity>> getInvoiceItems(int invoiceId) {
        return itemDao.getByInvoiceId(invoiceId);
    }

    // Admin xác nhận hóa đơn
    public void confirmInvoice(long invoiceId, int adminId, Context ctx) {
        executor.execute(() -> {
            invoiceDao.confirm(invoiceId, System.currentTimeMillis(), adminId);

            // Chú ý: Phần gọi NotificationHelper (Người 5) đang được tạm ẩn.
            // Khi nào Người 5 code xong, bạn bỏ comment đoạn này ra nhé.
        });
    }

    public LiveData<List<InvoiceEntity>> getAllInvoices() {
        return invoiceDao.getAllInvoices();
    }

    public void update(InvoiceEntity invoice) {
        // Dùng executor để lưu dưới background thread, không làm đơ màn hình
        executor.execute(() -> invoiceDao.update(invoice));
    }

    public androidx.lifecycle.LiveData<java.util.List<UnitBlockDTO>> getUnitBlockMappings() {
        return invoiceDao.getUnitBlockMappings();
    }

    public androidx.lifecycle.LiveData<java.util.List<InvoiceEntity>> getInvoicesByUserId(int userId) {
        return invoiceDao.getInvoicesByUserId(userId);
    }

    public androidx.lifecycle.LiveData<String> getUnitNameById(int unitId) {
        return invoiceDao.getUnitNameById(unitId);
    }

    public androidx.lifecycle.LiveData<java.util.List<UnitBlockDTO>> getAllUnitNames() {
        return invoiceDao.getAllUnitNames();
    }
}
