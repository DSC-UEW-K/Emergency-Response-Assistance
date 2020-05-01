package com.emperor95.era;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyDateUtils {
    private MyDateUtils() {
    }

    public static long getCurrentTimeMillisUTC() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }

    public static String formatTime(long j) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Long.valueOf(j));
    }

    public static String formatTimeWithMarker(long j) {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(Long.valueOf(j));
    }

    public static int getHourOfDay(long j) {
        return Integer.valueOf(new SimpleDateFormat("H", Locale.getDefault()).format(Long.valueOf(j))).intValue();
    }

    public static int getMinute(long j) {
        return Integer.valueOf(new SimpleDateFormat("m", Locale.getDefault()).format(Long.valueOf(j))).intValue();
    }

    public static String formatDateTime(long j) {
        if (isToday(j)) {
            return formatTime(j);
        }
        return formatDate(j);
    }

    public static String formatDate(long j) {
        return new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Long.valueOf(j));
    }

    public static boolean isToday(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return simpleDateFormat.format(Long.valueOf(j)).equals(simpleDateFormat.format(Long.valueOf(System.currentTimeMillis())));
    }

    public static boolean hasSameDate(long j, long j2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return simpleDateFormat.format(Long.valueOf(j)).equals(simpleDateFormat.format(Long.valueOf(j2)));
    }

    public static boolean checkOnlineTimestamp(long j) {
        long timeInMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - j;
        return TimeUnit.MILLISECONDS.toMinutes(timeInMillis) <= 1 && timeInMillis != 0;
    }
}
