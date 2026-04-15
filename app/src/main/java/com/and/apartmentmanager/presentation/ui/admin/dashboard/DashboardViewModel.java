package com.and.apartmentmanager.presentation.ui.admin.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.UnitDao;

import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {

    private final UnitDao unitDao;
    private final ContractDao contractDao;
    private final InvoiceDao invoiceDao;

    public final MutableLiveData<Integer> totalUnits = new MutableLiveData<>();
    public final MutableLiveData<Integer> occupiedUnits = new MutableLiveData<>();
    public final MutableLiveData<Integer> expiringContracts = new MutableLiveData<>();

    public final MutableLiveData<Integer> draftInvoices = new MutableLiveData<>();
    public final MutableLiveData<Integer> overdueInvoices = new MutableLiveData<>();

    public DashboardViewModel(UnitDao unitDao,
                              ContractDao contractDao,
                              InvoiceDao invoiceDao) {
        this.unitDao = unitDao;
        this.contractDao = contractDao;
        this.invoiceDao = invoiceDao;

        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {

            int total = unitDao.getTotalUnit();
            int occupied = unitDao.getOccupiedUnit();

            long now = System.currentTimeMillis();
            long future = now + (30L * 24 * 60 * 60 * 1000);

            int expiring = contractDao.getExpiringContracts(now, future);

            int draft = invoiceDao.getDraftInvoices();

            int overdue = contractDao.getExpiredContracts(now);

            totalUnits.postValue(total);
            occupiedUnits.postValue(occupied);
            expiringContracts.postValue(expiring);
            draftInvoices.postValue(draft);
            overdueInvoices.postValue(overdue);
        });
    }
}

