package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class ContextUtils {
    public static final String TAG = "ContextUtils";

    public static Context createPackageContext(Context context, String packageName, int flags) {
        try {
            return context.createPackageContext(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, String.format("createPackageContext() :: NameNotFoundException(%s)", e.getMessage()));
            return null;
        }
    }
}
