package com.and.apartmentmanager.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.ApartmentEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.repository.ApartmentRepository;
import com.and.apartmentmanager.viewmodel.InvoiceViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminStatisticsFragment extends Fragment {

    private ImageView btnBack;
    private LinearLayout layoutRevenueByApartment; // Layout danh sách mới
    private TextView btnFilterMonth, btnFilterQuarter, btnFilterYear;
    private TextView tvTotalRevenue, tvPaidRate, tvTotalUnits, tvPendingCount;

    private InvoiceViewModel invoiceViewModel;
    private ApartmentRepository apartmentRepository; // Kho lấy thông tin Chung cư

    private List<InvoiceEntity> allInvoices = new ArrayList<>();
    private Map<Integer, String> apartmentMap = new HashMap<>(); // ID Chung cư -> Tên

    private String currentMode = "month";
    private int selectedMonth = 3;
    private int selectedQuarter = 1;
    private int selectedYear = 2026;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_statistics, container, false);

        initViews(view);
        setupListeners();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        apartmentRepository = new ApartmentRepository(requireActivity().getApplication());

        // 1. Lấy tất cả tên Chung Cư bỏ vào Map
        apartmentRepository.getAll().observe(getViewLifecycleOwner(), apartments -> {
            if (apartments != null) {
                apartmentMap.clear();
                for (ApartmentEntity apt : apartments) {
                    apartmentMap.put(apt.getId(), apt.getName());
                }
                if (!allInvoices.isEmpty()) calculateAndDraw();
            }
        });

        // 2. Lấy dữ liệu Hóa đơn
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            if (invoices != null) {
                allInvoices = invoices;
                calculateAndDraw();
            }
        });

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        layoutRevenueByApartment = view.findViewById(R.id.layoutRevenueByApartment);
        btnFilterMonth = view.findViewById(R.id.btnFilterMonth);
        btnFilterQuarter = view.findViewById(R.id.btnFilterQuarter);
        btnFilterYear = view.findViewById(R.id.btnFilterYear);

        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvPaidRate = view.findViewById(R.id.tvPaidRate);
        tvTotalUnits = view.findViewById(R.id.tvTotalUnits);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);

        updateFilterTexts();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnFilterMonth.setOnClickListener(v -> {
            if (currentMode.equals("month")) { showPicker("month"); }
            currentMode = "month";
            updateTabUI();
            calculateAndDraw();
        });

        btnFilterQuarter.setOnClickListener(v -> {
            if (currentMode.equals("quarter")) { showPicker("quarter"); }
            currentMode = "quarter";
            updateTabUI();
            calculateAndDraw();
        });

        btnFilterYear.setOnClickListener(v -> {
            if (currentMode.equals("year")) { showPicker("year"); }
            currentMode = "year";
            updateTabUI();
            calculateAndDraw();
        });
    }

    private void showPicker(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String[] options;

        if (type.equals("month")) {
            builder.setTitle("Chọn Tháng");
            options = new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            builder.setItems(options, (dialog, which) -> {
                selectedMonth = which + 1;
                updateFilterTexts();
                calculateAndDraw();
            });
        } else if (type.equals("quarter")) {
            builder.setTitle("Chọn Quý");
            options = new String[]{"Quý 1", "Quý 2", "Quý 3", "Quý 4"};
            builder.setItems(options, (dialog, which) -> {
                selectedQuarter = which + 1;
                updateFilterTexts();
                calculateAndDraw();
            });
        } else {
            builder.setTitle("Chọn Năm");
            options = new String[]{"2025", "2026", "2027"};
            builder.setItems(options, (dialog, which) -> {
                selectedYear = Integer.parseInt(options[which]);
                updateFilterTexts();
                calculateAndDraw();
            });
        }
        builder.show();
    }

    private void updateFilterTexts() {
        btnFilterMonth.setText("Tháng " + selectedMonth);
        btnFilterQuarter.setText("Quý " + selectedQuarter);
        btnFilterYear.setText(String.valueOf(selectedYear));
    }

    private void calculateAndDraw() {
        if (allInvoices == null) return;

        double totalRevenue = 0;
        int paidCount = 0;
        int overdueCount = 0;
        int pendingCount = 0;

        // Lưu doanh thu theo ID Chung Cư
        Map<Integer, Double> revenueByApt = new HashMap<>();

        for (InvoiceEntity inv : allInvoices) {
            try {
                String[] parts = inv.getMonth().split("-");
                int invYear = Integer.parseInt(parts[0]);
                int invMonth = Integer.parseInt(parts[1]);

                boolean isMatch = false;
                if (currentMode.equals("month") && invYear == selectedYear && invMonth == selectedMonth) isMatch = true;
                if (currentMode.equals("quarter") && invYear == selectedYear && ((invMonth - 1) / 3 + 1) == selectedQuarter) isMatch = true;
                if (currentMode.equals("year") && invYear == selectedYear) isMatch = true;

                if (isMatch) {
                    String status = inv.getStatus();
                    if ("paid".equals(status)) {
                        totalRevenue += inv.getTotalAmount();
                        paidCount++;

                        // Cộng dồn doanh thu vào ID chung cư tương ứng
                        int aptId = inv.getApartmentId();
                        revenueByApt.put(aptId, revenueByApt.getOrDefault(aptId, 0.0) + inv.getTotalAmount());
                    } else if ("overdue".equals(status)) {
                        overdueCount++;
                        pendingCount++;
                    } else if ("confirmed".equals(status)) {
                        pendingCount++;
                    }
                }
            } catch (Exception e) {}
        }

        // Cập nhật 4 thẻ thông tin tổng quan
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        if (totalRevenue >= 1000000000) {
            tvTotalRevenue.setText(String.format(Locale.US, "%.2f Tỷ", totalRevenue / 1000000000.0));
        } else if (totalRevenue >= 1000000) {
            tvTotalRevenue.setText(String.format(Locale.US, "%.2f Tr", totalRevenue / 1000000.0));
        } else {
            tvTotalRevenue.setText(format.format(totalRevenue) + " đ");
        }

        int totalEvaluated = paidCount + overdueCount;
        double paidRate = totalEvaluated > 0 ? ((double) paidCount / totalEvaluated) * 100 : 0;
        tvPaidRate.setText(String.format(Locale.US, "%.1f%%", paidRate));

        tvTotalUnits.setText(String.valueOf(paidCount + pendingCount));
        tvPendingCount.setText(String.valueOf(pendingCount));

        // Vẽ danh sách mới
        drawRevenueList(revenueByApt);
    }

    // Hàm tạo giao diện Danh sách tự động y hệt thiết kế
    private void drawRevenueList(Map<Integer, Double> revenueByApt) {
        layoutRevenueByApartment.removeAllViews();
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (Map.Entry<Integer, Double> entry : revenueByApt.entrySet()) {
            String aptName = apartmentMap.getOrDefault(entry.getKey(), "Khác");
            double amount = entry.getValue();

            // 1. Tạo container cho 1 dòng (Row)
            LinearLayout row = new LinearLayout(getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 32, 0, 32); // Khoảng cách trên dưới cho thoáng

            // 2. Tạo Text Tên Chung cư (Căn trái, chữ xám)
            TextView tvName = new TextView(getContext());
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            tvName.setText(aptName);
            tvName.setTextColor(Color.parseColor("#6C757D"));
            tvName.setTextSize(14f);

            // 3. Tạo Text Số tiền (Căn phải, chữ màu xanh đậm như ảnh, in đậm)
            TextView tvAmount = new TextView(getContext());
            tvAmount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (amount >= 1000000) {
                tvAmount.setText(String.format(Locale.US, "%.2ftr", amount / 1000000.0));
            } else {
                tvAmount.setText(format.format(amount) + " đ");
            }
            tvAmount.setTextColor(Color.parseColor("#164E3B")); // Xanh rêu đậm
            tvAmount.setTextSize(14f);
            tvAmount.setTypeface(null, Typeface.BOLD);

            // Gắn vào dòng
            row.addView(tvName);
            row.addView(tvAmount);
            layoutRevenueByApartment.addView(row);

            // 4. Tạo đường kẻ ngang (Divider) mảnh màu xám nhạt
            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#F3F4F6"));
            layoutRevenueByApartment.addView(divider);
        }
    }

    private void updateTabUI() {
        resetTab(btnFilterMonth);
        resetTab(btnFilterQuarter);
        resetTab(btnFilterYear);

        TextView activeTab = currentMode.equals("month") ? btnFilterMonth : (currentMode.equals("quarter") ? btnFilterQuarter : btnFilterYear);
        activeTab.setBackgroundResource(R.drawable.shape_tab_active);
        activeTab.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E6F4EA")));
        activeTab.setTextColor(Color.parseColor("#2E6F40"));
        activeTab.setTypeface(null, Typeface.BOLD);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.shape_tab_inactive);
        tab.setBackgroundTintList(null);
        tab.setTextColor(Color.parseColor("#6C757D"));
        tab.setTypeface(null, Typeface.NORMAL);
    }
}