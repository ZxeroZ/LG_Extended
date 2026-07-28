package com.android.launcher3.util;

import android.os.Trace;
import android.util.ArrayMap;
import android.util.MutableLong;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class TraceHelper {
    private static final boolean ENABLED = false;
    public static final int FLAG_ALLOW_BINDER_TRACKING = 1;
    public static final int FLAG_CHECK_FOR_RACE_CONDITIONS = 4;
    public static final int FLAG_IGNORE_BINDERS = 2;
    public static TraceHelper INSTANCE = new TraceHelper();
    private static final boolean SYSTEM_TRACE = false;
    private static final ArrayMap<String, MutableLong> sUpTimes = null;

    public static void partitionSection(String sectionName, String partition) {
    }

    public Object beginFlagsOverride(int flags) {
        return null;
    }

    public void endFlagsOverride(Object token) {
    }

    public Object beginSection(String sectionName) {
        return beginSection(sectionName, 0);
    }

    public Object beginSection(String sectionName, int flags) {
        Trace.beginSection(sectionName);
        return null;
    }

    public void endSection(Object token) {
        Trace.endSection();
    }

    public static <T> T whitelistIpcs(String rpcName, Supplier<T> supplier) {
        Object objBeginSection = INSTANCE.beginSection(rpcName, 2);
        try {
            return supplier.get();
        } finally {
            INSTANCE.endSection(objBeginSection);
        }
    }
}
