package com.and.apartmentmanager.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

/**
 * OTP giả lập local (không gọi server).
 * SC-04/SC-06 dùng chung helper này.
 */
public class AuthOtpManager {
    private static AuthOtpManager instance;
    private final SharedPreferences prefs;

    private static final String PREF_NAME = "auth_otp";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_OTP = "otp";
    private static final String KEY_EXPIRES_AT = "expires_at";

    // OTP valid trong 5 phút
    private static final long EXPIRES_MS = 5 * 60 * 1000L;

    private AuthOtpManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthOtpManager getInstance(Context ctx) {
        if (instance == null) instance = new AuthOtpManager(ctx.getApplicationContext());
        return instance;
    }

    public static class SendResult {
        public final String email;
        public final String otp;
        public final long expiresAt;

        private SendResult(String email, String otp, long expiresAt) {
            this.email = email;
            this.otp = otp;
            this.expiresAt = expiresAt;
        }
    }

    /**
     * Tạo OTP 6 số, lưu local + logcat.
     */
    public SendResult sendOtp(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String otp = generateOtp6();
        long now = System.currentTimeMillis();
        long expiresAt = now + EXPIRES_MS;

        prefs.edit()
                .putString(KEY_EMAIL, normalizedEmail)
                .putString(KEY_OTP, otp)
                .putLong(KEY_EXPIRES_AT, expiresAt)
                .apply();

        Log.d("AuthOtpManager", "OTP for " + normalizedEmail + " = " + otp);
        return new SendResult(normalizedEmail, otp, expiresAt);
    }

    public boolean verifyOtp(String email, String otp) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String storedEmail = prefs.getString(KEY_EMAIL, "");
        String storedOtp = prefs.getString(KEY_OTP, "");
        long expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L);

        if (System.currentTimeMillis() > expiresAt) return false;
        if (!storedEmail.equals(normalizedEmail)) return false;
        return storedOtp.equals(otp);
    }

    public String getPendingEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    private String generateOtp6() {
        int n = new Random().nextInt(1_000_000);
        return String.format(Locale.ROOT, "%06d", n);
    }
}

