package com.lge.lgewidgetlib;

import android.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class LgeWidgetFeature {
    private static final int API_LEVEL = 2;
    private static final String VERSION = "5.3";
    public static boolean sFEATURE_ENABLE_LOG;

    public static int getApiLevel() {
        return 2;
    }

    public static String getLgeWidgetLibVersion() {
        return VERSION;
    }

    static {
        sFEATURE_ENABLE_LOG = "userdebug".equals(Build.TYPE) || "eng".equals(Build.TYPE);
    }

    public static boolean isCustomClassLoaderSupportPackage(String packageName) {
        return packageName.equals("com.lge.concierge");
    }

    public static boolean isPackageWithExtView(String packageName) {
        return packageName.equals("com.lge.concierge");
    }
}
