package com.android.quickstep.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.SysUINavigationMode;

/* JADX INFO: loaded from: classes.dex */
public class LayoutUtils {
    private static final float ASPECT_RATIO_WIDE_SCREEN = 1.78f;
    public static final boolean LAYOUT_DEBUG = false;

    public static float getDefaultSwipeHeight(Context context, DeviceProfile dp) {
        float f = dp.allAppsCellHeightPx - dp.allAppsIconTextSizePx;
        return SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.NO_BUTTON ? f - dp.getInsets().bottom : f;
    }

    public static int getShelfTrackingDistance(Context context, DeviceProfile dp, PagedOrientationHandler orientationHandler) {
        if (dp.isLandscape) {
            return (dp.heightPx * 3) / 4;
        }
        return dp.heightPx / 3;
    }

    public static void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (!(view instanceof ViewGroup)) {
            return;
        }
        int i = 0;
        while (true) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (i >= viewGroup.getChildCount()) {
                return;
            }
            setViewEnabled(viewGroup.getChildAt(i), enabled);
            i++;
        }
    }

    public static boolean isWideImage(int width, int height) {
        return Float.compare(((float) Math.max(width, height)) / ((float) Math.min(width, height)), ASPECT_RATIO_WIDE_SCREEN) > 0;
    }
}
