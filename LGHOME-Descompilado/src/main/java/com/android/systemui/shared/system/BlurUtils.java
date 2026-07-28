package com.android.systemui.shared.system;

import android.app.ActivityManager;
import android.os.SystemProperties;

/* JADX INFO: loaded from: classes.dex */
public abstract class BlurUtils {
    private static boolean mLGBlurDisabledSysProp = "1".equals(SystemProperties.get("ro.vendor.lge.feature.blur_level"));

    public static boolean supportsBlursOnWindows() {
        return mLGBlurDisabledSysProp && ActivityManager.isHighEndGfx();
    }
}
