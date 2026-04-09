package com.and.apartmentmanager.fragment;

import android.graphics.Color;
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
import com.and.apartmentmanager.viewmodel.InvoiceViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class UserInvoiceDetailFragment extends Fragment {

    private TextView tvHeaderTitle, tvUserTotalAmount, tvUserStatus;
    private ImageView btnBack;
    private View btnPayNow; // ĐÃ XÓA btnDispute hoàn toàn

    private InvoiceViewModel invoiceViewModel;
    private InvoiceEntity currentInvoice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_invoice_detail, container, false);

        initViews(view);
        setupListeners();

        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // Lấy ID hóa đơn được truyền sang
        if (getArguments() != null) {
            int invoiceId = getArguments().getInt("invoiceId", -1);
            if (invoiceId != -1) {
                loadInvoiceData(invoiceId);
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
    }

    private void loadInvoiceData(int invoiceId) {
        // Lấy hóa đơn từ Database lên
        invoiceViewModel.getAllInvoices().observe(getViewLifecycleOwner(), invoices -> {
            for (InvoiceEntity invoice : invoices) {
                if (invoice.getId() == invoiceId) {
                    currentInvoice = invoice;

                    // Cập nhật giao diện
                    // Tự động dịch unitId thành Tên phòng (VD: 2 -> A102)
                    invoiceViewModel.getUnitNameById(invoice.getUnitId()).observe(getViewLifecycleOwner(), unitName -> {
                        if (unitName != null) {
                            tvHeaderTitle.setText("Phòng " + unitName + " - Hóa đơn " + invoice.getMonth());
                        }
                    });

                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvUserTotalAmount.setText(format.format(invoice.getTotalAmount()));

                    // Set màu trạng thái (Bao gồm cả Overdue)
                    String status = invoice.getStatus();
                    if ("confirmed".equals(status)) {
                        tvUserStatus.setText("Chờ TT");
                        tvUserStatus.setTextColor(Color.parseColor("#B45309")); // Cam
                        ((View)tvUserStatus.getParent()).setBackgroundColor(Color.parseColor("#FEF3C7"));
                        btnPayNow.setVisibility(View.VISIBLE); // Hiện nút thanh toán
                    } else if ("overdue".equals(status)) {
                        tvUserStatus.setText("Quá hạn");
                        tvUserStatus.setTextColor(Color.parseColor("#DC2626")); // Đỏ
                        ((View)tvUserStatus.getParent()).setBackgroundColor(Color.parseColor("#FEE2E2"));
                        btnPayNow.setVisibility(View.VISIBLE); // Quá hạn vẫn phải cho thanh toán
                    } else if ("paid".equals(status)) {
                        tvUserStatus.setText("Đã TT");
                        tvUserStatus.setTextColor(Color.parseColor("#15803D")); // Xanh lá
                        ((View)tvUserStatus.getParent()).setBackgroundColor(Color.parseColor("#DCFCE7"));
                        btnPayNow.setVisibility(View.GONE); // Ẩn nút thanh toán
                    }
                    break;
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnPayNow.setOnClickListener(v -> {
            if (currentInvoice != null) {
                // Thay vì tự động đổi trạng thái thành công, ta gọi MoMo
                payWithMoMoSandbox(currentInvoice.getTotalAmount());
            }
        });


    }

    // =========================================================
    // TÍCH HỢP MOMO SANDBOX (PUBLIC KEYS)
    // =========================================================
    private final String PARTNER_CODE = "MOMOBKUN20180529";
    private final String ACCESS_KEY = "klm05TvNBzhg7h7j";
    private final String SECRET_KEY = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";
    private void payWithMoMoSandbox(double amount) {
        Toast.makeText(getContext(), "Đang kết nối cổng thanh toán MoMo...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
                String orderId = "HD_" + System.currentTimeMillis();
                String requestId = orderId;
                String strAmount = String.valueOf((long) amount);
                String orderInfo = "Thanh toan hoa don " + currentInvoice.getMonth();

                // Deep link đón User về app
                String returnUrl = "apartmentmanager://momosuccess";
                String notifyUrl = "https://google.com";

                // 1. Dựng chuỗi dữ liệu gốc
                String rawSignature = "accessKey=" + ACCESS_KEY +
                        "&amount=" + strAmount +
                        "&extraData=" +
                        "&ipnUrl=" + notifyUrl +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + PARTNER_CODE +
                        "&redirectUrl=" + returnUrl +
                        "&requestId=" + requestId +
                        "&requestType=payWithATM";

                // 2. Ký tên (Gọi hàm ở file mới tạo)
                String signature = com.and.apartmentmanager.helper.MoMoSecurity.signHmacSHA256(rawSignature, SECRET_KEY);

                // 3. Đóng gói JSON
                org.json.JSONObject jsonRequest = new org.json.JSONObject();
                jsonRequest.put("partnerCode", PARTNER_CODE);
                jsonRequest.put("partnerName", "Chung Cư Sunrise");
                jsonRequest.put("storeId", "Sunrise_01");
                jsonRequest.put("requestId", requestId);

                // SỬA Ở ĐÂY: Ép kiểu thành số Long thay vì dùng strAmount (chuỗi)
                jsonRequest.put("amount", (long) amount);

                jsonRequest.put("orderId", orderId);
                jsonRequest.put("orderInfo", orderInfo);
                jsonRequest.put("redirectUrl", returnUrl);
                jsonRequest.put("ipnUrl", notifyUrl);
                jsonRequest.put("lang", "vi");
                jsonRequest.put("extraData", "");
                jsonRequest.put("requestType", "payWithATM");
                jsonRequest.put("signature", signature);

                // 4. Bắn HTTP POST lên MoMo
                java.net.URL url = new java.net.URL(endpoint);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                java.io.OutputStream os = conn.getOutputStream();
                os.write(jsonRequest.toString().getBytes("UTF-8"));
                os.close();

                // 5. Đọc kết quả MoMo trả về (Có bắt lỗi 400/500 chuyên nghiệp)
                int responseCode = conn.getResponseCode();
                java.io.InputStream is;
                if (responseCode >= 400) {
                    is = conn.getErrorStream(); // Nếu MoMo từ chối, đọc luồng báo lỗi
                } else {
                    is = conn.getInputStream(); // Nếu thành công, đọc luồng bình thường
                }

                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) { response.append(line); }
                br.close();

                org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());

                // Kiểm tra xem MoMo có báo lỗi hệ thống không (resultCode != 0 là có lỗi)
                if (responseCode >= 400 || jsonResponse.getInt("resultCode") != 0) {
                    String errorMsg = jsonResponse.optString("message", "Lỗi không xác định");
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "MoMo từ chối: " + errorMsg, Toast.LENGTH_LONG).show()
                    );
                    return; // Dừng lại, không mở app MoMo nữa
                }

                String payUrl = jsonResponse.getString("payUrl");

                // 6. Quay lại Luồng chính (UI Thread) để mở App MoMo
                requireActivity().runOnUiThread(() -> {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(payUrl));
                    startActivity(intent);
                });

            } catch (Exception e) {
                android.util.Log.e("MOMO_ERROR", "Lỗi: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi kết nối MoMo: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 1. Chặn cửa, kiểm tra xem có ai mang theo Data (Đường link) về app không?
        android.content.Intent intent = requireActivity().getIntent();
        if (intent != null && intent.getData() != null) {
            String uriString = intent.getData().toString();

            // 2. Nếu đúng là đi từ cửa MoMo về
            if (uriString.startsWith("apartmentmanager://momosuccess")) {

                // 3. Đọc mã kết quả MoMo trả về (0 = Thành công)
                String resultCode = intent.getData().getQueryParameter("resultCode");

                if ("0".equals(resultCode)) {
                    if (currentInvoice != null) {
                        // 4A. Đổi trạng thái trên Database
                        currentInvoice.setStatus("paid");
                        invoiceViewModel.updateInvoice(currentInvoice);

                        Toast.makeText(getContext(), "Thanh toán MoMo thành công!", Toast.LENGTH_LONG).show();

                        // 4B. Đổi trạng thái trên Giao diện (UI) ngay lập tức
                        tvUserStatus.setText("Đã TT");
                        tvUserStatus.setTextColor(android.graphics.Color.parseColor("#15803D"));
                        ((android.view.View)tvUserStatus.getParent()).setBackgroundColor(android.graphics.Color.parseColor("#DCFCE7"));
                        btnPayNow.setVisibility(android.view.View.GONE); // Giấu luôn nút thanh toán
                    }
                } else {
                    Toast.makeText(getContext(), "Khách hàng đã hủy giao dịch hoặc thất bại!", Toast.LENGTH_SHORT).show();
                }

                // 5. Xóa URL cũ để tránh việc xoay màn hình bị gọi lại hàm này
                requireActivity().setIntent(new android.content.Intent());
            }
        }
    }
}