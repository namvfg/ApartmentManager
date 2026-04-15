package com.and.apartmentmanager.presentation.ui.user.invoice;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.dto.UnitBlockDTO;
import com.and.apartmentmanager.data.repository.InvoiceRepository;
import java.util.List;

public class InvoiceViewModel extends AndroidViewModel {
    private InvoiceRepository repository;

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        repository = new InvoiceRepository(application);
    }

    public LiveData<List<InvoiceEntity>> getInvoicesForUser(int unitId) {
        return repository.getInvoicesByUnitId(unitId);
    }

    // Gọi kho lấy toàn bộ hóa đơn cho Admin
    public LiveData<List<InvoiceEntity>> getAllInvoices() {
        return repository.getAllInvoices();
    }

    public void updateInvoice(InvoiceEntity invoice) {
        repository.update(invoice);
    }

    public androidx.lifecycle.LiveData<java.util.List<UnitBlockDTO>> getUnitBlockMappings() {
        return repository.getUnitBlockMappings();
    }

    // Dùng cho User: Lấy hóa đơn bằng phép JOIN qua userId
    public LiveData<List<InvoiceEntity>> getInvoicesByUserId(int userId) {
        return repository.getInvoicesByUserId(userId);
    }

    // Lấy tên phòng hiển thị lên UI
    public LiveData<String> getUnitNameById(int unitId) {
        return repository.getUnitNameById(unitId);
    }

    public LiveData<List<UnitBlockDTO>> getAllUnitNames() {
        return repository.getAllUnitNames();
    }
}