package com.and.apartmentmanager.presentation.ui.admin.invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.and.apartmentmanager.helper.NotificationHelper; // THÊM IMPORT CỦA P5
import com.and.apartmentmanager.presentation.ui.user.invoice.InvoiceViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminInvoiceConfirmFragment extends Fragment {

    private TextView tvRoomName, tvMonth, tvStatusBadge, tvTotalAmount;
    private View btnConfirmSend, btnEditInvoice;
    private ImageView btnBack;

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
            if (invoiceId != -1) {
                loadInvoiceData(invoiceId);
            }
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
    }

    private void loadInvoiceData(int invoiceId) {
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            for (InvoiceEntity invoice : invoices) {
                if (invoice.getId() == invoiceId) {
                    currentInvoice = invoice;

                    // Tự động dịch unitId thành Tên phòng (VD: 2 -> A101) cho Admin
                    invoiceViewModel.getUnitNameById(invoice.getUnitId()).observe(getViewLifecycleOwner(), unitName -> {
                        if (unitName != null) {
                            tvRoomName.setText("Phòng " + unitName);
                        }
                    });

                    tvMonth.setText("Tháng " + invoice.getMonth());

                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvTotalAmount.setText(format.format(invoice.getTotalAmount()));

                    // Nếu hóa đơn là 'adjusted' thì cập nhật nhãn
                    if ("adjusted".equals(invoice.getStatus())) {
                        tvStatusBadge.setText("Đã sửa");
                        tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#4338CA"));
                        tvStatusBadge.setBackgroundColor(android.graphics.Color.parseColor("#E0E7FF"));
                    }
                    break;
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Nút "Xác nhận gửi"
        btnConfirmSend.setOnClickListener(v -> {
            if (currentInvoice != null) {
                // 1. Cập nhật trạng thái xuống Database
                currentInvoice.setStatus("confirmed");
                invoiceViewModel.updateInvoice(currentInvoice);

                // ==============================================================
                // 2. [TÍCH HỢP P5] Gửi tín hiệu gọi chuông thông báo cho Cư dân
                // ==============================================================
                try {
                    long targetUserId = -1L; // -1 = báo cho tất cả user (hoặc truyền ID chủ phòng)
                    long apartmentId = 1L;   // ID của chung cư hiện tại (tạm để 1L)

                    NotificationHelper.sendAuto(
                            requireContext(),
                            NotificationHelper.Type.INVOICE_CONFIRMED,
                            targetUserId,
                            apartmentId,
                            currentInvoice.getId() // Truyền đúng ID hóa đơn để P5 xử lý
                    );
                } catch (Exception e) {
                    android.util.Log.e("InvoiceIntegration", "Lỗi gửi thông báo: " + e.getMessage());
                }
                // ==============================================================

                // 3. Hiển thị thông báo và quay lại màn hình trước
                Toast.makeText(getContext(), "Đã duyệt và gửi hóa đơn cho cư dân!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        // Nút "Chỉnh sửa" -> Mở sang màn A-12b
        btnEditInvoice.setOnClickListener(v -> {
            if (currentInvoice != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("invoiceId", currentInvoice.getId());

                AdminInvoiceEditFragment editFragment = new AdminInvoiceEditFragment();
                editFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}