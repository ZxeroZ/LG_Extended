package com.lge.contextenginelibrary.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Utility {
    public static final String UNKNOWN = "unknown";

    public static String getTimeString(long j) {
        return j > 0 ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date(j)) : "unknown";
    }

    public static long resetTime(Calendar calendar, int i) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        if (i != 0) {
            calendar.add(5, i);
        }
        return calendar.getTimeInMillis();
    }
}
