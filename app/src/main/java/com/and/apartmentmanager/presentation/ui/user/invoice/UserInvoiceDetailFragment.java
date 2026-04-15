package com.and.apartmentmanager.presentation.ui.user.invoice;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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

import java.text.NumberFormat;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UserInvoiceDetailFragment extends Fragment {

    private TextView tvHeaderTitle, tvUserTotalAmount, tvUserStatus;
    private ImageView btnBack;
    private View btnPayNow; // ĐÃ XÓA btnDispute hoàn toàn

    private LinearLayout layoutInvoiceItems;

    private InvoiceViewModel invoiceViewModel;
    private InvoiceEntity currentInvoice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_invoice_detail, container, false);

        initViews(view);
        setupListeners();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // Lấy hóa đơn từ Bundle truyền sang (Hỗ trợ cả Object và ID)
        if (getArguments() != null) {
            if (getArguments().getSerializable("invoice") != null) {
                loadInvoiceData((InvoiceEntity) getArguments().getSerializable("invoice"));
            } else {
                int invoiceId = getArguments().getInt("invoice_id", getArguments().getInt("invoiceId", -1));
                if (invoiceId != -1) {
                    invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
                        for (InvoiceEntity inv : invoices) {
                            if (inv.getId() == invoiceId) {
                                loadInvoiceData(inv);
                                break;
                            }
                        }
                    });
                }
            }
        }

        return view;
    }

    private void initViews(View view) {
        tvHeaderTitle = view.findViewById(R.id.tvHeaderTitle);
        tvUserTotalAmount = view.findViewById(R.id.tvUserTotalAmount);
        tvUserStatus = view.findViewById(R.id.tvUserStatus);
        btnBack = view.findViewById(R.id.btnBack);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        layoutInvoiceItems = view.findViewById(R.id.layoutInvoiceItems);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnPayNow.setOnClickListener(v -> {
            if (currentInvoice != null) {
                // Khởi động luồng MoMo App-to-App chuẩn captureWallet
                requestMoMoPayment(currentInvoice);
            }
        });
    }

    private void loadInvoiceData(InvoiceEntity invoice) {
        if (invoice == null) return;
        currentInvoice = invoice;

        // 1. Cập nhật tiêu đề (Sửa lỗi hiện chữ null)
        invoiceViewModel.getUnitNameById(invoice.getUnitId()).observe(getViewLifecycleOwner(), name -> {
            if (name != null) {
                tvHeaderTitle.setText("Phòng " + name + " - Hóa đơn " + invoice.getMonth());
            }
        });

        // 2. Định dạng tổng tiền
        NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvUserTotalAmount.setText(vnFormat.format(invoice.getTotalAmount()) + " đ");

        // 3. Trạng thái & Màu sắc (Sửa lỗi bo tròn viền)
        com.google.android.material.card.MaterialCardView badgeCard = (com.google.android.material.card.MaterialCardView) tvUserStatus.getParent();
        String status = invoice.getStatus();

        if ("confirmed".equals(status)) {
            tvUserStatus.setText("Chờ TT");
            tvUserStatus.setTextColor(Color.parseColor("#B45309"));
            badgeCard.setCardBackgroundColor(Color.parseColor("#FEF3C7"));
            btnPayNow.setVisibility(View.VISIBLE);
        } else if ("paid".equals(status)) {
            tvUserStatus.setText("Đã TT");
            tvUserStatus.setTextColor(Color.parseColor("#15803D"));
            badgeCard.setCardBackgroundColor(Color.parseColor("#DCFCE7"));
            btnPayNow.setVisibility(View.GONE);
        } else if ("overdue".equals(status)) {
            tvUserStatus.setText("Quá hạn");
            tvUserStatus.setTextColor(Color.parseColor("#DC2626"));
            badgeCard.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
            btnPayNow.setVisibility(View.VISIBLE);
        }

        // 4. Lấy chi tiết khoản thu (Điện, nước, tiền phòng) từ Database
        invoiceViewModel.getInvoiceItems(invoice.getId()).observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                layoutInvoiceItems.removeAllViews();
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
        params.setMargins(0, 0, 0, 32);
        row.setLayoutParams(params);

        TextView tvName = new TextView(getContext());
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#6C757D"));
        tvName.setTextSize(14f);

        TextView tvPrice = new TextView(getContext());
        NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvPrice.setText(vnFormat.format(amount) + " đ");
        tvPrice.setTextSize(14f);
        tvPrice.setTypeface(null, Typeface.BOLD);
        tvPrice.setTextColor(amount < 0 ? Color.parseColor("#EF4444") : Color.parseColor("#1F3325"));

        row.addView(tvName);
        row.addView(tvPrice);
        layoutInvoiceItems.addView(row);
    }

    // --- LUỒNG THANH TOÁN MOMO ---

    private void requestMoMoPayment(InvoiceEntity invoice) {
        // Thông số Sandbox MoMo
        String partnerCode = "MOMOBKUN20180810";
        String accessKey = "klm0568887013313";
        String secretKey = "at67abHswmBc013";
        String orderInfo = "Thanh toán hóa đơn phòng " + invoice.getUnitId();
        String redirectUrl = "apartmentmanager://momosuccess";
        String ipnUrl = "https://momo.vn";
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderId = String.valueOf(invoice.getId()) + "_" + requestId;
        long amount = (long) invoice.getTotalAmount();
        String requestType = "captureWallet"; // captureWallet = Văng thẳng vào App MoMo
        String extraData = "";

        // Tạo chuỗi Raw Hash để ký tên
        String rawHash = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        try {
            String signature = hmacSHA256(rawHash, secretKey);
            // Trong thực tế, bạn sẽ gửi JSON lên Endpoint MoMo để lấy payUrl.
            // Ở đồ án, ta mô phỏng việc mở App MoMo Tester với URL scheme chuẩn:
            String momoUrl = "momo://?action=pay&partnerCode=" + partnerCode +
                    "&orderId=" + orderId + "&amount=" + amount +
                    "&signature=" + signature + "&requestType=" + requestType;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(momoUrl));
            startActivity(intent);
            Toast.makeText(getContext(), "Đang mở MoMo App...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] bytes = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = requireActivity().getIntent();
        if (intent != null && intent.getData() != null) {
            String uriString = intent.getData().toString();
            if (uriString.startsWith("apartmentmanager://momosuccess")) {
                String resultCode = intent.getData().getQueryParameter("resultCode");
                if ("0".equals(resultCode) && currentInvoice != null) {
                    currentInvoice.setStatus("paid");
                    invoiceViewModel.updateInvoice(currentInvoice);
                    Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    loadInvoiceData(currentInvoice);
                }
                requireActivity().setIntent(new Intent());
            }
        }
    }
}