package com.and.apartmentmanager.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.viewmodel.InvoiceViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserStatisticsFragment extends Fragment {

    private ImageView btnBack;
    private BarChart barChart;
    private TextView btnFilterMonth, btnFilterQuarter, btnFilterYear;
    private TextView tvSummaryPeriod, tvUserTotalPaid, tvUserPaidCount;
    private TextView tvChartTitle, tvBreakdownTitle;
    private TextView tvRentFee, tvElectricFee, tvWaterFee, tvOtherFee;

    private InvoiceViewModel invoiceViewModel;
    private List<InvoiceEntity> userInvoices = new ArrayList<>();

    private String currentMode = "month";
    private int selectedMonth = 3;
    private int selectedQuarter = 1;
    private int selectedYear = 2026;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);

        initViews(view);
        setupListeners();
        setupBarChartBase();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // ========================================================
        // DÙNG LỆNH JOIN: Lấy userId thực tế từ SessionManager
        int currentUserId = (int) com.and.apartmentmanager.helper.SessionManager.getInstance(requireContext()).getUserId();

        // TẤM KHIÊN: Dùng tạm userId = 1 nếu chạy test
        if (currentUserId == -1) {
            currentUserId = 4;
        }
        // ========================================================

        // Gọi hàm JOIN lấy hóa đơn để vẽ biểu đồ
        invoiceViewModel.getInvoicesByUserId(currentUserId).observe(getViewLifecycleOwner(), invoices -> {
            if (invoices != null) {
                userInvoices = invoices;
                calculateAndDraw();
            }
        });

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        barChart = view.findViewById(R.id.barChartUserExpenses);
        btnFilterMonth = view.findViewById(R.id.btnFilterMonth);
        btnFilterQuarter = view.findViewById(R.id.btnFilterQuarter);
        btnFilterYear = view.findViewById(R.id.btnFilterYear);

        tvSummaryPeriod = view.findViewById(R.id.tvSummaryPeriod);
        tvUserTotalPaid = view.findViewById(R.id.tvUserTotalPaid);
        tvUserPaidCount = view.findViewById(R.id.tvUserPaidCount);
        tvChartTitle = view.findViewById(R.id.tvChartTitle);
        tvBreakdownTitle = view.findViewById(R.id.tvBreakdownTitle);

        tvRentFee = view.findViewById(R.id.tvRentFee);
        tvElectricFee = view.findViewById(R.id.tvElectricFee);
        tvWaterFee = view.findViewById(R.id.tvWaterFee);
        tvOtherFee = view.findViewById(R.id.tvOtherFee);

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
        if (userInvoices == null) return;

        double totalPaid = 0;
        int paidCount = 0;

        // Mảng doanh thu 6 kỳ gần nhất cho biểu đồ
        float[] chartData = new float[6];
        String[] chartLabels;

        // KHẮC PHỤC LỖI: Khởi tạo nhãn (Labels) ngay từ đầu để tránh lỗi Null khi Phòng không có dữ liệu
        if (currentMode.equals("month")) {
            tvChartTitle.setText("Chi tiêu 6 tháng đầu năm");
            chartLabels = new String[]{"T1", "T2", "T3", "T4", "T5", "T6"};
        } else if (currentMode.equals("quarter")) {
            tvChartTitle.setText("Chi tiêu các Quý trong năm " + selectedYear);
            chartLabels = new String[]{"Q1", "Q2", "Q3", "Q4", "", ""};
        } else {
            tvChartTitle.setText("Chi tiêu theo Năm");
            chartLabels = new String[]{"2025", "2026", "2027", "", "", ""};
        }

        for (InvoiceEntity inv : userInvoices) {
            try {
                String[] parts = inv.getMonth().split("-");
                int invYear = Integer.parseInt(parts[0]);
                int invMonth = Integer.parseInt(parts[1]);

                boolean isMatch = false;
                if (currentMode.equals("month") && invYear == selectedYear && invMonth == selectedMonth) isMatch = true;
                if (currentMode.equals("quarter") && invYear == selectedYear && ((invMonth - 1) / 3 + 1) == selectedQuarter) isMatch = true;
                if (currentMode.equals("year") && invYear == selectedYear) isMatch = true;

                // Tính TỔNG TIỀN ĐÃ ĐÓNG (Chỉ tính Paid)
                if (isMatch && "paid".equals(inv.getStatus())) {
                    totalPaid += inv.getTotalAmount();
                    paidCount++;
                }

                // Dữ liệu cho biểu đồ (Lấy cả Paid, Confirmed và Overdue)
                if ("paid".equals(inv.getStatus()) || "confirmed".equals(inv.getStatus()) || "overdue".equals(inv.getStatus())) {
                    if (currentMode.equals("month") && invYear == selectedYear) {
                        if (invMonth >= 1 && invMonth <= 6) {
                            chartData[invMonth - 1] += (float) (inv.getTotalAmount() / 1000000.0);
                        }
                    } else if (currentMode.equals("quarter") && invYear == selectedYear) {
                        int qIdx = (invMonth - 1) / 3;
                        chartData[qIdx] += (float) (inv.getTotalAmount() / 1000000.0);
                    } else if (currentMode.equals("year")) {
                        // BỔ SUNG LOGIC TAB NĂM
                        if (invYear >= 2025 && invYear <= 2027) {
                            chartData[invYear - 2025] += (float) (inv.getTotalAmount() / 1000000.0);
                        }
                    }
                }
            } catch (Exception e) {}
        }

        // 1. Cập nhật Tổng tiền
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvUserTotalPaid.setText(format.format(totalPaid) + " đ");
        tvUserPaidCount.setText(paidCount + " hóa đơn đã thanh toán");

        if(currentMode.equals("month")) {
            tvSummaryPeriod.setText("Tổng đã đóng (T" + selectedMonth + "/" + selectedYear + ")");
            tvBreakdownTitle.setText("Phân bổ Tháng " + selectedMonth + "/" + selectedYear);
        } else if(currentMode.equals("quarter")) {
            tvSummaryPeriod.setText("Tổng đã đóng (Quý " + selectedQuarter + "/" + selectedYear + ")");
            tvBreakdownTitle.setText("Phân bổ Quý " + selectedQuarter + "/" + selectedYear);
        } else {
            tvSummaryPeriod.setText("Tổng đã đóng (Năm " + selectedYear + ")");
            tvBreakdownTitle.setText("Phân bổ Năm " + selectedYear);
        }

        // 2. Giả lập Phân bổ chi tiết
        if (totalPaid > 0) {
            tvRentFee.setText(format.format(totalPaid * 0.7) + " đ");
            tvElectricFee.setText(format.format(totalPaid * 0.15) + " đ");
            tvWaterFee.setText(format.format(totalPaid * 0.05) + " đ");
            tvOtherFee.setText(format.format(totalPaid * 0.1) + " đ");
        } else {
            tvRentFee.setText("0 đ"); tvElectricFee.setText("0 đ"); tvWaterFee.setText("0 đ"); tvOtherFee.setText("0 đ");
        }

        // 3. Vẽ Biểu đồ
        drawChart(chartData, chartLabels);
    }

    private void drawChart(float[] data, String[] labels) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int count = currentMode.equals("quarter") ? 4 : 6;
        for (int i = 0; i < count; i++) {
            entries.add(new BarEntry(i, data[i]));
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(count);

        BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu (Triệu VNĐ)");
        dataSet.setColor(Color.parseColor("#388E3C")); // Màu xanh lá cây
        dataSet.setValueTextColor(Color.parseColor("#1F3325"));
        dataSet.setValueTextSize(11f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);
        barChart.animateY(800);
        barChart.invalidate();
    }

    private void setupBarChartBase() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setGranularity(1f);
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