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
import com.and.apartmentmanager.presentation.ui.admin.invoice.AdminInvoiceEditFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminInvoiceAdapter extends RecyclerView.Adapter<AdminInvoiceAdapter.ViewHolder> {

    private Context context;
    private List<InvoiceEntity> list = new ArrayList<>();
    private OnInvoiceClickListener listener;

    // Kho chứa từ điển dịch Mã Phòng -> Tên Phòng
    private java.util.Map<Integer, String> unitNameMap = new java.util.HashMap<>();

    public void setUnitNameMap(java.util.Map<Integer, String> map) {
        this.unitNameMap = map;
        notifyDataSetChanged();
    }

    public interface OnInvoiceClickListener {
        void onInvoiceClick(InvoiceEntity invoice);
    }

    public AdminInvoiceAdapter(Context context, OnInvoiceClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<InvoiceEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Vẫn dùng lại giao diện thẻ của User cho tiết kiệm
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceEntity invoice = list.get(position);

        // Tra cứu tên phòng từ Từ điển. Nếu chưa có kịp thì hiển thị tạm ID
        String roomName = unitNameMap.containsKey(invoice.getUnitId()) ? unitNameMap.get(invoice.getUnitId()) : String.valueOf(invoice.getUnitId());
        holder.tvMonth.setText("Phòng " + roomName + " | " + invoice.getMonth());

        // Format tiền tệ
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(format.format(invoice.getTotalAmount()));

        // Xử lý màu sắc trạng thái
        String status = invoice.getStatus();
        if ("draft".equals(status)) {
            holder.tvStatus.setText("Bản nháp");
            holder.tvStatus.setTextColor(Color.parseColor("#92400E"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FEF3C7"));
        } else if ("confirmed".equals(status)) {
            holder.tvStatus.setText("Chờ TT");
            holder.tvStatus.setTextColor(Color.parseColor("#B45309"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FEF3C7")); // Màu cam
        } else if ("paid".equals(status)) {
            holder.tvStatus.setText("Đã TT");
            holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#DCFCE7")); // Màu xanh lá
        } else if ("overdue".equals(status)) {
            // THÊM ĐOẠN NÀY ĐỂ XỬ LÝ TRẠNG THÁI QUÁ HẠN
            holder.tvStatus.setText("Quá hạn");
            holder.tvStatus.setTextColor(Color.parseColor("#DC2626")); // Chữ Đỏ
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FEE2E2")); // Nền Đỏ nhạt
        }

        // 1. CLICK BÌNH THƯỜNG -> MỞ CONFIRM / XEM CHI TIẾT
        holder.itemView.setOnClickListener(v -> listener.onInvoiceClick(invoice));

        // 2. CLICK GIỮ LÂU -> MỞ EDIT (ĐÃ THÊM BỐT GÁC BẢO MẬT)
        holder.itemView.setOnLongClickListener(v -> {
            // Kiểm tra trạng thái: CHỈ DRAFT MỚI ĐƯỢC SỬA
            if ("draft".equals(invoice.getStatus())) {
                androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) v.getContext();
                android.os.Bundle bundle = new android.os.Bundle();
                bundle.putInt("invoiceId", invoice.getId());

                AdminInvoiceEditFragment editFragment = new AdminInvoiceEditFragment();
                editFragment.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, editFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Nếu là Confirmed hoặc Paid thì hiển thị cảnh báo từ chối
                android.widget.Toast.makeText(context, "LỖI: Chỉ được phép chỉnh sửa hóa đơn Bản Nháp (Draft)!", android.widget.Toast.LENGTH_LONG).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvAmount, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvInvoiceMonth);
            tvAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}