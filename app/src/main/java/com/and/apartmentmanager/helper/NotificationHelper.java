package com.and.apartmentmanager.helper;

import android.content.Context;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.NotificationEntity;

public class NotificationHelper {
    public enum Type {
        INVOICE_CONFIRMED,   // P4 gọi khi Admin confirm hóa đơn
        PRICE_CHANGED,       // P3 gọi khi đổi giá dịch vụ
        CONTRACT_EXPIRING,   // WorkManager gọi (30 ngày trước hết HĐ)
        USER_INACTIVATED,    // P2 gọi khi kick / inactive user
        USER_JOINED          // P2 gọi khi user join bằng mã mời
    }

    /**
     * Gửi thông báo tự động.
     *
     * @param context          application context
     * @param type         loại event (xem enum Type)
     * @param targetUserId user nhận thông báo (-1 = toàn chung cư)
     * @param apartmentId  chung cư liên quan
     * @param refId        id tham chiếu (invoiceId, serviceId...)
     */
    public static void sendAuto(Context context, Type type,
                                int targetUserId, int apartmentId, int refId) {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            NotificationEntity notif = new NotificationEntity();
            notif.setApartmentId((int) apartmentId);
            notif.setType("auto");
            notif.setTarget(targetUserId == -1 ? "all" : "user");
            notif.setTargetUserId(targetUserId == -1 ? null : (int) targetUserId);
            notif.setCreatedAt(System.currentTimeMillis());
            notif.setRead(false);  // ← mặc định chưa đọc
            notif.setReadAt(null);

            // Nội dung tùy theo type
            switch (type) {
                case INVOICE_CONFIRMED:
                    notif.setTitle("Hóa đơn mới đã được tạo");
                    notif.setContent("Hóa đơn tháng này đã được xác nhận. Vui lòng thanh toán đúng hạn.");
                    break;

                case PRICE_CHANGED:
                    notif.setTitle("Giá dịch vụ thay đổi");
                    notif.setContent("Một dịch vụ vừa được cập nhật giá mới. Xem chi tiết trong mục Dịch vụ.");
                    break;

                case CONTRACT_EXPIRING:
                    notif.setTitle("Hợp đồng sắp hết hạn");
                    notif.setContent("Hợp đồng của bạn còn 30 ngày nữa là hết hạn. Liên hệ Admin để gia hạn.");
                    break;

                case USER_INACTIVATED:
                    notif.setTitle("Tài khoản bị vô hiệu hóa");
                    notif.setContent("Tài khoản của bạn trong chung cư này đã bị vô hiệu hóa.");
                    break;

                case USER_JOINED:
                    notif.setTitle("Cư dân mới đã tham gia");
                    notif.setContent("Một cư dân mới vừa tham gia vào chung cư.");
                    break;
            }

            // Lấy admin_id từ apartment (cần để set created_by)
            // Tạm set 1 — sau lấy từ SessionManager
            notif.setCreatedBy(1);

            AppDatabase.getInstance(context)
                    .notificationDao()
                    .insert(notif);
        });
    }

}
