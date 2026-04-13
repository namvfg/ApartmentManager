package com.and.apartmentmanager.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.and.apartmentmanager.R;
import com.and.apartmentmanager.data.local.entity.InvoiceEntity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserInvoiceAdapter extends RecyclerView.Adapter<UserInvoiceAdapter.InvoiceViewHolder> {

    private Context context;
    private List<InvoiceEntity> invoiceList;
    private OnInvoiceClickListener listener;

    // Interface để bắt sự kiện khi User bấm vào 1 hóa đơn để xem chi tiết
    public interface OnInvoiceClickListener {
        void onInvoiceClick(InvoiceEntity invoice);
    }

    public UserInvoiceAdapter(Context context, OnInvoiceClickListener listener) {
        this.context = context;
        this.invoiceList = new ArrayList<>();
        this.listener = listener;
    }

    public void setData(List<InvoiceEntity> list) {
        this.invoiceList = list;
        notifyDataSetChanged(); // Cập nhật lại giao diện khi có data mới
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gắn giao diện item_user_invoice.xml vào Adapter
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        InvoiceEntity invoice = invoiceList.get(position);
        if (invoice == null) return;

        // 1. Xử lý Tháng (Database lưu format "2026-03", ta sẽ cắt ra hiển thị "Tháng 3/2026")
        try {
            String[] parts = invoice.getMonth().split("-");
            holder.tvInvoiceMonth.setText("Tháng " + Integer.parseInt(parts[1]) + "/" + parts[0]);
        } catch (Exception e) {
            holder.tvInvoiceMonth.setText("Hóa đơn " + invoice.getMonth());
        }

        // 2. Format Tiền tệ (VND)
        NumberFormat formatVND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvTotalAmount.setText(formatVND.format(invoice.getTotalAmount()));

        // 3. Tạm tính Hạn thu (Giả sử hạn thu luôn là ngày 15 của tháng đó)
        try {
            String[] parts = invoice.getMonth().split("-");
            holder.tvDueDate.setText("Hạn: 15/" + parts[1] + "/" + parts[0]);
        } catch (Exception e) {
            holder.tvDueDate.setText("Hạn: Đang cập nhật");
        }

        // 4. Xử lý Trạng thái (Đổi màu viền và màu chữ dựa vào status)
        String status = invoice.getStatus() != null ? invoice.getStatus().toLowerCase() : "";
        switch (status) {
            case "confirmed": // Chờ thanh toán
                holder.tvStatusBadge.setText("Chờ TT");
                holder.tvStatusBadge.setTextColor(Color.parseColor("#B45309")); // Chữ Cam đậm
                holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#FEF3C7")); // Nền Cam nhạt
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#F59E0B")); // Vạch Cam
                break;

            case "paid": // Đã thanh toán
                holder.tvStatusBadge.setText("Đã TT");
                holder.tvStatusBadge.setTextColor(Color.parseColor("#2E6F40")); // Chữ Xanh lá
                holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#E6F4EA")); // Nền Xanh nhạt
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#27AE60")); // Vạch Xanh lá
                break;

            case "overdue": // Quá hạn
                holder.tvStatusBadge.setText("Quá hạn");
                holder.tvStatusBadge.setTextColor(Color.parseColor("#B91C1C")); // Chữ Đỏ đậm
                holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#FEE2E2")); // Nền Đỏ nhạt
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#EF4444")); // Vạch Đỏ
                break;

            default: // Draft hoặc các trạng thái khác
                holder.tvStatusBadge.setText(status);
                holder.tvStatusBadge.setTextColor(Color.parseColor("#6C757D"));
                holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#F3F4F6"));
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#9CA3AF"));
                break;
        }

        // 5. Sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInvoiceClick(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoiceList != null ? invoiceList.size() : 0;
    }

    // Class ViewHolder để ánh xạ các view trong item_user_invoice.xml
    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvInvoiceMonth, tvStatusBadge, tvDueDate, tvTotalAmount;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            tvInvoiceMonth = itemView.findViewById(R.id.tvInvoiceMonth);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}