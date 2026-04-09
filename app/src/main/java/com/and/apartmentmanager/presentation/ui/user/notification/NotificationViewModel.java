package com.and.apartmentmanager.presentation.ui.user.notification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.and.apartmentmanager.data.local.AppDatabase;
import com.and.apartmentmanager.data.local.entity.NotificationEntity;
import com.and.apartmentmanager.helper.SessionManager;
import com.and.apartmentmanager.presentation.adapter.NotificationAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final int userId;
    private final int apartmentId;

    public NotificationViewModel(@NonNull Application app) {
        super(app);
        db          = AppDatabase.getInstance(app);
        userId      = (int) SessionManager.getInstance(app).getUserId();
        apartmentId = (int) SessionManager.getInstance(app).getApartmentId();
//        userId      = 4;  // user có notification trong seed
//        apartmentId = 1;
    }

    public LiveData<List<NotificationAdapter.NotifItem>> getNotifications() {
        return Transformations.map(
                db.notificationDao().getForUser(apartmentId, userId),
                entities -> {
                    List<NotificationAdapter.NotifItem> items = new ArrayList<>();
                    for (NotificationEntity e : entities) {
                        items.add(new NotificationAdapter.NotifItem(
                                e.getTitle(),
                                e.getContent(),
                                formatTime(e.getCreatedAt()),
                                e.isRead(),          // ← dùng thẳng từ entity
                                mapType(e.getTitle())
                        ));
                    }
                    return items;
                }
        );
    }

    public LiveData<Integer> getUnreadCount() {
        return db.notificationDao().countUnread(apartmentId, userId);
    }

    public void markAllRead() {
        AppDatabase.DB_EXECUTOR.execute(() ->
                db.notificationDao().markAllRead(
                        apartmentId, userId, System.currentTimeMillis()
                )
        );
    }

    private NotificationAdapter.NotifType mapType(String title) {
        if (title == null) return NotificationAdapter.NotifType.GENERAL;
        String lower = title.toLowerCase();
        if (lower.contains("hóa đơn"))  return NotificationAdapter.NotifType.INVOICE;
        if (lower.contains("giá") || lower.contains("dịch vụ"))
            return NotificationAdapter.NotifType.PRICE_CHANGE;
        return NotificationAdapter.NotifType.GENERAL;
    }

    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < TimeUnit.MINUTES.toMillis(1))  return "Vừa xong";
        if (diff < TimeUnit.HOURS.toMillis(1))    return (diff / TimeUnit.MINUTES.toMillis(1)) + " phút trước";
        if (diff < TimeUnit.DAYS.toMillis(1))     return (diff / TimeUnit.HOURS.toMillis(1))   + " giờ trước";
        if (diff < TimeUnit.DAYS.toMillis(7))     return (diff / TimeUnit.DAYS.toMillis(1))    + " ngày trước";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp));
    }
}