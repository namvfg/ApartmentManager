package com.and.apartmentmanager.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferences prefs;
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_APARTMENT_ID = "apartment_id";

    private SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance(Context ctx) {
        if (instance == null) instance = new SessionManager(ctx);
        return instance;
    }

    // Gọi ngay sau khi login thành công
    public void saveSession(long userId, String role, long apartmentId) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role)
                .putLong(KEY_APARTMENT_ID, apartmentId)
                .apply();
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public long getApartmentId() {
        return prefs.getLong(KEY_APARTMENT_ID, -1);
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    // Gọi khi logout
    public void clear() {
        prefs.edit().clear().apply();
    }

}
