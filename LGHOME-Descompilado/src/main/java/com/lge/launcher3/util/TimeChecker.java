package com.lge.launcher3.util;

import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class TimeChecker {
    public static final boolean DEBUG = false;
    private static final String HEADER = "[" + TimeChecker.class.getSimpleName() + "] ";
    private static final String TAG = "TimeChecker";
    private static TimeChecker sInstance;
    private HashMap<String, Long> mStartTime = new HashMap<>();
    private HashMap<String, Long> mEndTime = new HashMap<>();

    public void end(String string) {
    }

    public void endAndResult(String string) {
    }

    public long result(String name) {
        return 0L;
    }

    public void start(String string) {
    }

    public static TimeChecker getInstance() {
        if (sInstance == null) {
            sInstance = new TimeChecker();
        }
        return sInstance;
    }

    private TimeChecker() {
    }
}
