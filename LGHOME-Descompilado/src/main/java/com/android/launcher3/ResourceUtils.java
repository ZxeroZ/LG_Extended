package com.android.launcher3;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class ResourceUtils {
    public static final int DEFAULT_NAVBAR_VALUE = 48;
    public static final int INVALID_RESOURCE_HANDLE = -1;
    public static final String NAVBAR_BOTTOM_GESTURE_LARGER_SIZE = "navigation_bar_gesture_larger_height";
    public static final String NAVBAR_BOTTOM_GESTURE_SIZE = "navigation_bar_gesture_height";
    public static final String NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE = "navigation_bar_width";
    public static final String NAVBAR_SIZE = "navigation_bar_height";

    public static int getNavbarSize(String resName, Resources res) {
        return getDimenByName(resName, res, 48);
    }

    public static int getDimenByName(String resName, Resources res, int defaultValue) {
        int identifier = res.getIdentifier(resName, "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier != 0) {
            return res.getDimensionPixelSize(identifier);
        }
        return pxFromDp(defaultValue, res.getDisplayMetrics());
    }

    public static boolean getBoolByName(String resName, Resources res, boolean defaultValue) {
        int identifier = res.getIdentifier(resName, "bool", LauncherConst.PACKAGE_NAME_NATIVE);
        return identifier != 0 ? res.getBoolean(identifier) : defaultValue;
    }

    public static int getIntegerByName(String resName, Resources res, int defaultValue) {
        int identifier = res.getIdentifier(resName, LauncherConst.RESOURCE_INTEGER_TYPE, LauncherConst.PACKAGE_NAME_NATIVE);
        return identifier != 0 ? res.getInteger(identifier) : defaultValue;
    }

    public static int pxFromDp(float size, DisplayMetrics metrics) {
        return pxFromDp(size, metrics, 1.0f);
    }

    public static int pxFromDp(float size, DisplayMetrics metrics, float scale) {
        if (size < 0.0f) {
            return -1;
        }
        return Math.round(scale * TypedValue.applyDimension(1, size, metrics));
    }
}
