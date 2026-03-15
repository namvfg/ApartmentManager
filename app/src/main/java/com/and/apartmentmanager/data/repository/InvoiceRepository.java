package com.and.apartmentmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.InvoiceItemDao;
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
}
