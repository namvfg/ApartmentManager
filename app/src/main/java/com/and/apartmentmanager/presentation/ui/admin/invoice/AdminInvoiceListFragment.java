package com.and.apartmentmanager.presentation.ui.admin.invoice;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.adapter.AdminInvoiceAdapter;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.presentation.ui.user.invoice.InvoiceViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminInvoiceListFragment extends Fragment {

    private RecyclerView rcvInvoices;
    private AdminInvoiceAdapter adapter; // Đã chuyển sang dùng Adapter của Admin
    private InvoiceViewModel invoiceViewModel;

    // Các thành phần UI của Tab
    private TextView tabDraft, tabConfirmed, tabPaid, tabOverdue;

    // Lưu trữ danh sách gốc (chứa tất cả hóa đơn)
    private List<InvoiceEntity> allInvoicesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_invoice_list, container, false);

        // Ánh xạ UI
        rcvInvoices = view.findViewById(R.id.rcvInvoices);
        tabDraft = view.findViewById(R.id.tabDraft);
        tabConfirmed = view.findViewById(R.id.tabConfirmed);
        tabPaid = view.findViewById(R.id.tabPaid);
        tabOverdue = view.findViewById(R.id.tabOverdue);

        rcvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdminInvoiceAdapter(getContext(), invoice -> {
            String status = invoice.getStatus();

            if ("draft".equals(status) || "adjusted".equals(status)) {
                // 1. Nếu là Bản nháp / Đã sửa -> Mở màn hình Xác nhận gửi (A-12)
                Bundle bundle = new Bundle();
                bundle.putInt("invoiceId", invoice.getId());

                AdminInvoiceConfirmFragment confirmFragment = new AdminInvoiceConfirmFragment();
                confirmFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, confirmFragment)
                        .addToBackStack(null)
                        .commit();

            } else if ("confirmed".equals(status) || "overdue".equals(status)) {
                // 2. UC32: NẾU CHỜ TT HOẶC QUÁ HẠN -> HIỆN POPUP THU TIỀN MẶT
                java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
                String amountStr = format.format(invoice.getTotalAmount());

                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Xác nhận thu tiền mặt")
                        .setMessage("Xác nhận thu tiền mặt " + amountStr + " cho hóa đơn tháng " + invoice.getMonth() + "?")
                        .setPositiveButton("Xác nhận Đã Thu", (dialog, which) -> {
                            // Đổi trạng thái thành paid và lưu DB
                            invoice.setStatus("paid");
                            invoiceViewModel.updateInvoice(invoice);
                            Toast.makeText(getContext(), "Đã cập nhật thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();

            } else if ("paid".equals(status)) {
                // 3. Nếu đã thanh toán rồi thì báo lỗi
                Toast.makeText(getContext(), "Hóa đơn này đã được thanh toán xong.", Toast.LENGTH_SHORT).show();
            }
        });
        rcvInvoices.setAdapter(adapter);

        // Setup sự kiện bấm Tab
        setupTabClickListeners();

        // Nối Database
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            if (invoices != null) {
                allInvoicesList = invoices; // Cất vào kho dự trữ
                filterAndDisplay("draft"); // Mặc định mở lên là lọc tab Draft trước
            }
        });

        // -----------------------------------------------------
        // TẢI TỪ ĐIỂN TÊN PHÒNG VÀ NÉM CHO ADAPTER
        // -----------------------------------------------------
        java.util.Map<Integer, String> unitNameMap = new java.util.HashMap<>();
        invoiceViewModel.getAllUnitNames().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                unitNameMap.clear();
                for (com.and.apartmentmanager.data.local.entity.UnitBlockDTO dto : list) {
                    // Mặc dù biến tên là blockName nhưng do câu Query ở Dao, nó đang chứa Tên Phòng
                    unitNameMap.put(dto.unitId, dto.blockName);
                }
                adapter.setUnitNameMap(unitNameMap); // Bơm cho Adapter
            }
        });

        return view;
    }

    private void setupTabClickListeners() {
        tabDraft.setOnClickListener(v -> {
            updateTabUI(tabDraft);
            filterAndDisplay("draft");
        });

        tabConfirmed.setOnClickListener(v -> {
            updateTabUI(tabConfirmed);
            filterAndDisplay("confirmed");
        });

        tabPaid.setOnClickListener(v -> {
            updateTabUI(tabPaid);
            filterAndDisplay("paid");
        });
        tabOverdue.setOnClickListener(v -> {
            updateTabUI(tabOverdue);
            filterAndDisplay("overdue");
        });
    }

    // Hàm Lọc dữ liệu
    private void filterAndDisplay(String status) {
        List<InvoiceEntity> filteredList = new ArrayList<>();
        for (InvoiceEntity inv : allInvoicesList) {
            if (status.equals(inv.getStatus())) {
                filteredList.add(inv);
            }
        }
        adapter.setData(filteredList);

        // Cập nhật số lượng trên Tab Draft (Ví dụ: "Draft (2)")
        if ("draft".equals(status)) {
            tabDraft.setText("Draft (" + filteredList.size() + ")");
        }
    }

    // Hàm Đổi màu UI khi bấm Tab
    private void updateTabUI(TextView selectedTab) {
        resetTab(tabDraft, "Draft");
        resetTab(tabConfirmed, "Confirmed");
        resetTab(tabPaid, "Paid");
        resetTab(tabOverdue, "Overdue");

        selectedTab.setBackgroundResource(R.drawable.shape_tab_active);
        selectedTab.setTextColor(Color.parseColor("#92400E"));
    }

    private void resetTab(TextView tab, String originalText) {
        tab.setText(originalText);
        tab.setBackgroundResource(R.drawable.shape_tab_inactive);
        tab.setTextColor(Color.parseColor("#6C757D")); // Màu xám
    }
}