package com.and.apartmentmanager.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lưu màu avatar theo userId (đơn giản: chỉ đổi màu nền, text vẫn là initials).
 * Không đụng tới schema Room.
 */
public class UserAvatarManager {
    private static final String PREF_NAME = "user_avatar_prefs";
    private static final String KEY_PREFIX_COLOR = "avatar_color_user_";

    public static void saveColorIndex(Context ctx, long userId, int index) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(KEY_PREFIX_COLOR + userId, index).apply();
    }

    public static int getColorIndex(Context ctx, long userId, int defaultIndex) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEY_PREFIX_COLOR + userId, defaultIndex);
    }
}

