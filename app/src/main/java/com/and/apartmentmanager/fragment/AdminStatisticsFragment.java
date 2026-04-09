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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminStatisticsFragment extends Fragment {

    private ImageView btnBack;
    private BarChart barChart;
    private TextView btnFilterMonth, btnFilterQuarter, btnFilterYear, tvChartTitle;
    private TextView tvTotalRevenue, tvPaidRate, tvTotalUnits, tvPendingCount;
    private InvoiceViewModel invoiceViewModel;

    private List<InvoiceEntity> allInvoices = new ArrayList<>();

    // Bản đồ lưu trữ Mã Phòng -> Tên Tòa (Lấy từ Database)
    private Map<Integer, String> unitToBlockMap = new HashMap<>();

    // Trạng thái bộ lọc thời gian
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
        setupBarChartBase();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // 1. Lắng nghe dữ liệu truy vấn JOIN (Lấy Tên Tòa từ Mã Phòng)
        invoiceViewModel.getUnitBlockMappings().observe(getViewLifecycleOwner(), mappings -> {
            if (mappings != null) {
                unitToBlockMap.clear();
                // Đổ dữ liệu vào Map để dùng nhanh
                for (com.and.apartmentmanager.data.local.entity.UnitBlockDTO dto : mappings) {
                    unitToBlockMap.put(dto.unitId, dto.blockName);
                }
                if (!allInvoices.isEmpty()) calculateAndDraw();
            }
        });

        // 2. Lắng nghe dữ liệu Hóa đơn
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
        barChart = view.findViewById(R.id.barChartStatistics);
        btnFilterMonth = view.findViewById(R.id.btnFilterMonth);
        btnFilterQuarter = view.findViewById(R.id.btnFilterQuarter);
        btnFilterYear = view.findViewById(R.id.btnFilterYear);
        tvChartTitle = view.findViewById(R.id.tvChartTitle);

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

        // Cốt lõi: Gom doanh thu theo tên Tòa (từ Database)
        Map<String, Double> revenueByBlock = new HashMap<>();

        for (InvoiceEntity inv : allInvoices) {
            try {
                String[] parts = inv.getMonth().split("-");
                int invYear = Integer.parseInt(parts[0]);
                int invMonth = Integer.parseInt(parts[1]);

                // Kiểm tra xem hóa đơn này có khớp với thời gian đang lọc không
                boolean isMatch = false;
                if (currentMode.equals("month") && invYear == selectedYear && invMonth == selectedMonth) isMatch = true;
                if (currentMode.equals("quarter") && invYear == selectedYear && ((invMonth - 1) / 3 + 1) == selectedQuarter) isMatch = true;
                if (currentMode.equals("year") && invYear == selectedYear) isMatch = true;

                if (isMatch) {
                    String status = inv.getStatus();

                    // Lấy tên Tòa từ Map (Database). Nếu trống, xếp vào "Chưa phân bổ"
                    String blockName = unitToBlockMap.getOrDefault(inv.getUnitId(), "Khác");

                    if ("paid".equals(status)) {
                        totalRevenue += inv.getTotalAmount();
                        paidCount++;
                        // Cộng tiền vào Tòa tương ứng
                        revenueByBlock.put(blockName, revenueByBlock.getOrDefault(blockName, 0.0) + inv.getTotalAmount());
                    } else if ("overdue".equals(status)) {
                        overdueCount++;
                        pendingCount++;
                    } else if ("confirmed".equals(status)) {
                        pendingCount++;
                    }
                }
            } catch (Exception e) {}
        }

        // Đổ dữ liệu lên 4 thẻ chỉ số
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        if (totalRevenue >= 1000000) {
            tvTotalRevenue.setText(String.format(Locale.US, "%.2f Tr", totalRevenue / 1000000.0));
        } else {
            tvTotalRevenue.setText(format.format(totalRevenue) + " đ");
        }

        int totalEvaluated = paidCount + overdueCount;
        double paidRate = totalEvaluated > 0 ? ((double) paidCount / totalEvaluated) * 100 : 0;
        tvPaidRate.setText(String.format(Locale.US, "%.1f%%", paidRate));

        tvTotalUnits.setText(String.valueOf(paidCount + pendingCount));
        tvPendingCount.setText(String.valueOf(pendingCount));

        // Gọi hàm vẽ biểu đồ
        drawChart(revenueByBlock);
    }

    private void drawChart(Map<String, Double> revenueByBlock) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : revenueByBlock.entrySet()) {
            entries.add(new BarEntry(index, (float) (entry.getValue() / 1000000.0))); // Vẽ bằng đơn vị Triệu VNĐ
            labels.add(entry.getKey());
            index++;
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size());

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (Triệu VNĐ)");
        dataSet.setColor(Color.parseColor("#2E6F40"));
        dataSet.setValueTextColor(Color.parseColor("#1F3325"));
        dataSet.setValueTextSize(11f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.4f);
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