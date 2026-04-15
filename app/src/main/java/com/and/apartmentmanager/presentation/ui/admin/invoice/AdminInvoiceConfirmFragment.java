package com.and.apartmentmanager.presentation.ui.admin.invoice;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.data.local.entity.InvoiceItemEntity;
import com.and.apartmentmanager.helper.NotificationHelper; // THÊM IMPORT CỦA P5
import com.and.apartmentmanager.presentation.ui.user.invoice.InvoiceViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminInvoiceConfirmFragment extends Fragment {

    private TextView tvRoomName, tvMonth, tvStatusBadge, tvTotalAmount;
    private View btnConfirmSend, btnEditInvoice;
    private ImageView btnBack;

    private LinearLayout layoutInvoiceItemsAdmin;

    private InvoiceViewModel invoiceViewModel;
    private InvoiceEntity currentInvoice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_invoice_confirm, container, false);
        initViews(view);
        setupListeners();
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        if (getArguments() != null) {
            int invoiceId = getArguments().getInt("invoiceId", -1);
            if (invoiceId != -1) loadInvoiceData(invoiceId);
        }
        return view;
    }

    private void initViews(View view) {
        tvRoomName = view.findViewById(R.id.tvRoomName);
        tvMonth = view.findViewById(R.id.tvMonth);
        tvStatusBadge = view.findViewById(R.id.tvStatusBadge);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnConfirmSend = view.findViewById(R.id.btnConfirmSend);
        btnEditInvoice = view.findViewById(R.id.btnEditInvoice);
        btnBack = view.findViewById(R.id.btnBack);
        layoutInvoiceItemsAdmin = view.findViewById(R.id.layoutInvoiceItemsAdmin);
    }

    private void loadInvoiceData(int invoiceId) {
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            for (InvoiceEntity invoice : invoices) {
                if (invoice.getId() == invoiceId) {
                    currentInvoice = invoice;

                    invoiceViewModel.getUnitNameById(invoice.getUnitId()).observe(getViewLifecycleOwner(), unitName -> {
                        if (unitName != null) tvRoomName.setText("Phòng " + unitName);
                    });

                    tvMonth.setText("Tháng " + invoice.getMonth());
                    NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                    tvTotalAmount.setText(format.format(invoice.getTotalAmount()) + " đ");

                    if ("adjusted".equals(invoice.getStatus())) {
                        tvStatusBadge.setText("Đã sửa");
                        tvStatusBadge.setTextColor(Color.parseColor("#4338CA"));
                        tvStatusBadge.setBackgroundColor(Color.parseColor("#E0E7FF"));
                    }
                    break;
                }
            }
        });

        // Tải chi tiết động thay vì fix cứng
        invoiceViewModel.getInvoiceItems(invoiceId).observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                layoutInvoiceItemsAdmin.removeAllViews();
                for (InvoiceItemEntity item : items) {
                    addDynamicItemRow(item.getServiceName(), item.getTotal());
                }
            }
        });
    }

    private void addDynamicItemRow(String name, double amount) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        row.setLayoutParams(params);

        TextView tvName = new TextView(getContext());
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#6C757D"));
        tvName.setTextSize(13f);

        TextView tvPrice = new TextView(getContext());
        NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvPrice.setText(vnFormat.format(amount) + " đ");
        tvPrice.setTextSize(14f);
        tvPrice.setTypeface(null, Typeface.BOLD);
        tvPrice.setTextColor(Color.parseColor("#1A1A1A"));

        row.addView(tvName);
        row.addView(tvPrice);
        layoutInvoiceItemsAdmin.addView(row);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnConfirmSend.setOnClickListener(v -> {
            if (currentInvoice != null) {
                currentInvoice.setStatus("confirmed");
                invoiceViewModel.updateInvoice(currentInvoice);

                try {
                    NotificationHelper.sendAuto(requireContext(), NotificationHelper.Type.INVOICE_CONFIRMED, -1L, 1L, currentInvoice.getId());
                } catch (Exception e) {}

                Toast.makeText(getContext(), "Đã duyệt và gửi hóa đơn cho cư dân!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        btnEditInvoice.setOnClickListener(v -> {
            if (currentInvoice != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("invoiceId", currentInvoice.getId());
                AdminInvoiceEditFragment editFragment = new AdminInvoiceEditFragment();
                editFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, editFragment).addToBackStack(null).commit();
            }
        });
    }
}