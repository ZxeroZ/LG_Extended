package com.lge.launcher3.util;

import android.os.SystemClock;
import android.util.SparseLongArray;

/* JADX INFO: loaded from: classes.dex */
public class SystemClockUtils {
    public static final int KEY_DEFAULT = 0;
    public static final int KEY_LAUNCHER_BINDING = 2;
    public static final int KEY_LAUNCHER_CREATION = 1;
    private static SparseLongArray sStartElapsedRealtime = new SparseLongArray();

    public static final long startElapsedTime(int key) {
        if (sStartElapsedRealtime.get(key) != 0) {
            return -1L;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        sStartElapsedRealtime.put(key, jElapsedRealtime);
        return jElapsedRealtime;
    }

    public static final long endElapsedTime(int key) {
        long j = sStartElapsedRealtime.get(key);
        if (j == 0) {
            return -1L;
        }
        sStartElapsedRealtime.delete(key);
        return SystemClock.elapsedRealtime() - j;
    }

    public static final String endElapsedTimeToString(int key) {
        long j = sStartElapsedRealtime.get(key);
        long jEndElapsedTime = endElapsedTime(key);
        return String.format("%d ElapsedTime %d ms (%d ~ %d)", Integer.valueOf(key), Long.valueOf(jEndElapsedTime), Long.valueOf(j), Long.valueOf(j + jEndElapsedTime));
    }
}
