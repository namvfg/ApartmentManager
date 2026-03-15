package com.and.apartmentmanager.util;

import android.annotation.SuppressLint;

import java.util.Calendar;

public class DateTimeUtil {

    /** Timestamp ngày đầu tháng, offset so với tháng hiện tại */
    public static long monthOffset(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, offset);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /** String tháng dạng "yyyy-MM", offset so với tháng hiện tại */
    @SuppressLint("DefaultLocale")
    public static String monthString(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, offset);
        return String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
    }
}
