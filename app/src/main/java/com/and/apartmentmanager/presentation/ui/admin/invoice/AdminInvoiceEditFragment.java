package com.and.apartmentmanager.presentation.ui.admin.invoice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.presentation.ui.user.invoice.InvoiceViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminInvoiceEditFragment extends Fragment {

    private TextView tvRoomInfo, tvRentAmountEdit, tvElectricSubtotalEdit, tvWaterSubtotalEdit, tvNewTotalEdit, tvOldTotalEdit;
    private EditText edtElectricEdit, edtWaterEdit, edtNote;
    private View btnSaveEdit;
    private ImageView btnBack;

    private InvoiceViewModel invoiceViewModel;
    private InvoiceEntity currentInvoice;

    private final double PRICE_ELECTRIC = 3500;
    private final double PRICE_WATER = 15000;
    private double rentAmount = 0; // Tiền phòng cố định

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_invoice_edit, container, false);
        initViews(view);
        setupListeners();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        if (getArguments() != null) {
            int invoiceId = getArguments().getInt("invoiceId", -1);
            if (invoiceId != -1) {
                loadInvoiceData(invoiceId);
            }
        }
        return view;
    }

    private void initViews(View view) {
        tvRoomInfo = view.findViewById(R.id.tvRoomInfo);
        tvRentAmountEdit = view.findViewById(R.id.tvRentAmountEdit);
        tvElectricSubtotalEdit = view.findViewById(R.id.tvElectricSubtotalEdit);
        tvWaterSubtotalEdit = view.findViewById(R.id.tvWaterSubtotalEdit);
        tvNewTotalEdit = view.findViewById(R.id.tvNewTotalEdit);
        tvOldTotalEdit = view.findViewById(R.id.tvOldTotalEdit);
        edtElectricEdit = view.findViewById(R.id.edtElectricEdit);
        edtWaterEdit = view.findViewById(R.id.edtWaterEdit);
        edtNote = view.findViewById(R.id.edtNote);
        btnSaveEdit = view.findViewById(R.id.btnSaveEdit);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void loadInvoiceData(int invoiceId) {
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            for (InvoiceEntity invoice : invoices) {
                if (invoice.getId() == invoiceId) {
                    currentInvoice = invoice;
                    // Lấy ra giá trị tĩnh của tiền phòng (giả sử trước đó đã cộng dồn, tạm fix cứng 3 triệu hoặc lấy từ DB)
                    // Vì bản nháp tổng tiền có thể đã tính toán sai, ta cứ lấy lại tiền gốc.
                    rentAmount = 3000000; // Tạm thiết lập tiền thuê cố định là 3 triệu

                    tvRoomInfo.setText("Phòng " + invoice.getUnitId() + " - " + invoice.getMonth());
                    tvRentAmountEdit.setText(formatMoney(rentAmount));
                    tvOldTotalEdit.setText(formatMoney(invoice.getTotalAmount())); // Hiển thị số tiền cũ đã nhập sai

                    calculateTotal(); // Tính toán lại số mới
                    break;
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateTotal(); }
        };
        edtElectricEdit.addTextChangedListener(textWatcher);
        edtWaterEdit.addTextChangedListener(textWatcher);

        btnSaveEdit.setOnClickListener(v -> {
            if (edtNote.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập lý do chỉnh sửa!", Toast.LENGTH_SHORT).show();
                return; // Chặn lại không cho lưu
            }

            if (currentInvoice != null) {
                currentInvoice.setTotalAmount(calculateTotal());
                currentInvoice.setStatus("adjusted");
                invoiceViewModel.updateInvoice(currentInvoice);
                Toast.makeText(getContext(), "Đã cập nhật số liệu mới!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private double calculateTotal() {
        double electricIndex = 0, waterIndex = 0;
        try {
            if (!edtElectricEdit.getText().toString().isEmpty()) electricIndex = Double.parseDouble(edtElectricEdit.getText().toString());
            if (!edtWaterEdit.getText().toString().isEmpty()) waterIndex = Double.parseDouble(edtWaterEdit.getText().toString());
        } catch (Exception e) {}

        double electricTotal = electricIndex * PRICE_ELECTRIC;
        double waterTotal = waterIndex * PRICE_WATER;
        double grandTotal = rentAmount + electricTotal + waterTotal;

        tvElectricSubtotalEdit.setText("Thành tiền: " + formatMoney(electricTotal));
        tvWaterSubtotalEdit.setText("Thành tiền: " + formatMoney(waterTotal));
        tvNewTotalEdit.setText(formatMoney(grandTotal));

        return grandTotal;
    }

    private String formatMoney(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }
}