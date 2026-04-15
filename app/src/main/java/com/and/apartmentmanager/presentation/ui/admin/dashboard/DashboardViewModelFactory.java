package com.and.apartmentmanager.presentation.ui.admin.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.data.local.dao.ContractDao;
import com.and.apartmentmanager.data.local.dao.InvoiceDao;
import com.and.apartmentmanager.data.local.dao.UnitDao;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    private final UnitDao unitDao;
    private final ContractDao contractDao;
    private final InvoiceDao invoiceDao;

    public DashboardViewModelFactory(UnitDao unitDao,
                                     ContractDao contractDao,
                                     InvoiceDao invoiceDao) {
        this.unitDao = unitDao;
        this.contractDao = contractDao;
        this.invoiceDao = invoiceDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DashboardViewModel(unitDao, contractDao, invoiceDao);
    }
}