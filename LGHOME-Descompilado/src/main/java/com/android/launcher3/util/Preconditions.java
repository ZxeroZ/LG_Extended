package com.android.launcher3.util;

import android.os.Looper;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.config.ProviderConfig;

/* JADX INFO: loaded from: classes.dex */
public class Preconditions {
    public static void assertNotNull(Object o) {
        if (ProviderConfig.IS_DOGFOOD_BUILD && o == null) {
            throw new IllegalStateException();
        }
    }

    public static void assertWorkerThread() {
        if (ProviderConfig.IS_DOGFOOD_BUILD && !isSameLooper(LauncherModel.getWorkerLooper())) {
            throw new IllegalStateException();
        }
    }

    public static void assertUIThread() {
        if (ProviderConfig.IS_DOGFOOD_BUILD && !isSameLooper(Looper.getMainLooper())) {
            throw new IllegalStateException();
        }
    }

    public static void assertNonUiThread() {
        if (ProviderConfig.IS_DOGFOOD_BUILD && isSameLooper(Looper.getMainLooper())) {
            throw new IllegalStateException();
        }
    }

    private static boolean isSameLooper(Looper looper) {
        return Looper.myLooper() == looper;
    }
}
