package com.and.apartmentmanager.presentation.ui.user.invoice;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.presentation.adapter.UserInvoiceAdapter;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class UserInvoiceListFragment extends Fragment {

    private RecyclerView rcvInvoices;
    private UserInvoiceAdapter adapter;
    private InvoiceViewModel invoiceViewModel;

    // UI của các Tab
    private MaterialCardView cardTabAll, cardTabPending, cardTabPaid, cardTabOverdue;
    private TextView tvTabAll, tvTabPending, tvTabPaid, tvTabOverdue;

    // Kho chứa danh sách hóa đơn hợp lệ của User
    private List<InvoiceEntity> validInvoicesList = new ArrayList<>();
    private String currentFilter = "all"; // Mặc định mở lên là tab "Tất cả"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_invoice_list, container, false);

        initViews(view);

        rcvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserInvoiceAdapter(getContext(), invoice -> {
            Bundle bundle = new Bundle();
            bundle.putInt("invoiceId", invoice.getId());

            UserInvoiceDetailFragment detailFragment = new UserInvoiceDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rcvInvoices.setAdapter(adapter);

        setupTabClickListeners();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // Lấy userId thực tế của người đang đăng nhập (Không cần unitId nữa)
        int currentUserId = (int) com.and.apartmentmanager.helper.SessionManager.getInstance(requireContext()).getUserId();

        // Dùng tạm userId = 2 nếu chạy test 1 mình chưa qua đăng nhập
        if (currentUserId == -1) currentUserId = 4;

        // 3. Gọi hàm JOIN mới viết để lấy hóa đơn
        invoiceViewModel.getInvoicesByUserId(currentUserId).observe(getViewLifecycleOwner(), invoices -> {
            if (invoices != null) {
                validInvoicesList.clear();
                for (InvoiceEntity invoice : invoices) {
                    String status = invoice.getStatus();
                    if ("confirmed".equals(status) || "paid".equals(status) || "overdue".equals(status)) {
                        validInvoicesList.add(invoice);
                    }
                }
                filterAndDisplay(currentFilter);
            }
        });

        return view;
    }

    private void initViews(View view) {
        rcvInvoices = view.findViewById(R.id.rcvUserInvoices);

        cardTabAll = view.findViewById(R.id.cardTabAll);
        tvTabAll = view.findViewById(R.id.tvTabAll);

        cardTabPending = view.findViewById(R.id.cardTabPending);
        tvTabPending = view.findViewById(R.id.tvTabPending);

        cardTabPaid = view.findViewById(R.id.cardTabPaid);
        tvTabPaid = view.findViewById(R.id.tvTabPaid);

        cardTabOverdue = view.findViewById(R.id.cardTabOverdue);
        tvTabOverdue = view.findViewById(R.id.tvTabOverdue);
    }

    private void setupTabClickListeners() {
        cardTabAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateTabUI(cardTabAll, tvTabAll);
            filterAndDisplay(currentFilter);
        });

        cardTabPending.setOnClickListener(v -> {
            currentFilter = "confirmed";
            updateTabUI(cardTabPending, tvTabPending);
            filterAndDisplay(currentFilter);
        });

        cardTabPaid.setOnClickListener(v -> {
            currentFilter = "paid";
            updateTabUI(cardTabPaid, tvTabPaid);
            filterAndDisplay(currentFilter);
        });

        cardTabOverdue.setOnClickListener(v -> {
            currentFilter = "overdue";
            updateTabUI(cardTabOverdue, tvTabOverdue);
            filterAndDisplay(currentFilter);
        });
    }

    // Hàm lọc dữ liệu theo trạng thái
    private void filterAndDisplay(String filterType) {
        List<InvoiceEntity> filteredList = new ArrayList<>();
        for (InvoiceEntity inv : validInvoicesList) {
            if ("all".equals(filterType)) {
                filteredList.add(inv);
            } else if (filterType.equals(inv.getStatus())) {
                filteredList.add(inv);
            }
        }
        adapter.setData(filteredList);
    }

    // Hàm đổi màu UI khi nhấn Tab
    private void updateTabUI(MaterialCardView selectedCard, TextView selectedText) {
        // 1. Reset tất cả về trạng thái chưa chọn
        resetTab(cardTabAll, tvTabAll);
        resetTab(cardTabPending, tvTabPending);
        resetTab(cardTabPaid, tvTabPaid);
        resetTab(cardTabOverdue, tvTabOverdue);

        // 2. Kích hoạt màu cho Tab được bấm
        selectedCard.setCardBackgroundColor(Color.parseColor("#E6F4EA"));
        selectedCard.setStrokeColor(Color.parseColor("#E6F4EA")); // Ẩn viền đi
        selectedText.setTextColor(Color.parseColor("#2E6F40"));
        selectedText.setTypeface(null, Typeface.BOLD);
    }

    // Hàm Reset màu Tab
    private void resetTab(MaterialCardView card, TextView text) {
        card.setCardBackgroundColor(Color.WHITE);
        card.setStrokeColor(Color.parseColor("#C8E8D0")); // Hiện lại viền xám
        text.setTextColor(Color.parseColor("#8BAF95"));
        text.setTypeface(null, Typeface.NORMAL);
    }
}