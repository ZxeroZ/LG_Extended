package com.lge.launcher3.operator;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.android.launcher3.PageIndicator;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class GoogleNowManager {
    private static final PageIndicator.PageMarkerResources MARKER = new PageIndicator.PageMarkerResources(R.drawable.ic_pageindicator_current, R.drawable.ic_pageindicator_default);
    private static boolean sIsAppEnabled = false;
    public static String sPackageName = "com.google.android.googlequicksearchbox";

    public static boolean isAppEnabled() {
        return sIsAppEnabled;
    }

    public static boolean isAvailable(Context context) {
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
            return false;
        }
        if (!SharedPreferencesManager.contains(context, 0, SharedPreferencesConst.GoogleNowKey.IS_ENABLED)) {
            HomeSettingsSharedPreferences.setGoogleNowEnabled(context, LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW_INIT_VALUE.getValue());
        }
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.GoogleNowKey.IS_ENABLED, false);
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
