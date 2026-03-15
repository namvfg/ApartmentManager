package com.and.apartmentmanager.helper;

import android.content.Context;

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
     * @param ctx          application context
     * @param type         loại event (xem enum Type)
     * @param targetUserId user nhận thông báo (-1 = toàn chung cư)
     * @param apartmentId  chung cư liên quan
     * @param refId        id tham chiếu (invoiceId, serviceId...)
     */
    public static void sendAuto(Context ctx, Type type,
                                long targetUserId, long apartmentId, long refId) {
        // TODO (Người 5): INSERT vào bảng notifications
        // Hiện tại rỗng — compile được, người khác gọi không bị lỗi
    }

}
