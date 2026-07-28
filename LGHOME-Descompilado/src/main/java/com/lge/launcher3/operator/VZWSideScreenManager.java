package com.lge.launcher3.operator;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.android.launcher3.PageIndicator;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class VZWSideScreenManager {
    private static final PageIndicator.PageMarkerResources MARKER = new PageIndicator.PageMarkerResources(R.drawable.ic_homescreen_pageindicator_appflash_select, R.drawable.ic_homescreen_pageindicator_appflash);
    private static boolean sIsAppEnabled = false;
    private static boolean sIsInitialized = false;
    private static boolean sIsServiceAttached = false;
    public static String sPackageName = "com.discoveryscreen";

    public static void setInitialized(boolean initialized) {
        sIsInitialized = initialized;
    }

    public static void setServiceAttached(boolean serviceAttached) {
        sIsServiceAttached = serviceAttached;
    }

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    public static boolean isServiceAttached() {
        return sIsServiceAttached;
    }

    public static boolean isAvailable() {
        return sIsInitialized;
    }

    public static boolean isAppEnabled() {
        return sIsAppEnabled;
    }

    public static void setAppEnabled(PackageManager pm) {
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(sPackageName, 0);
            sIsAppEnabled = applicationInfo != null && applicationInfo.enabled;
        } catch (PackageManager.NameNotFoundException unused) {
            sIsAppEnabled = false;
        }
    }

    public static PageIndicator.PageMarkerResources getMarker() {
        return MARKER;
    }

    public static void setMarkerColor(int activeColor, int inactiveColor) {
        PageIndicator.PageMarkerResources pageMarkerResources = MARKER;
        pageMarkerResources.mActiveColor = activeColor;
        pageMarkerResources.mInactiveColor = inactiveColor;
    }
}
