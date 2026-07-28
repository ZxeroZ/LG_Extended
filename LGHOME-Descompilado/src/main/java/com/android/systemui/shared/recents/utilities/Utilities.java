package com.android.systemui.shared.recents.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
public class Utilities {
    private static final float TABLET_MIN_DPS = 600.0f;

    /* JADX WARN: Removed duplicated region for block: B:10:0x000c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int calculateBackDispositionHints(int r2, int r3, boolean r4, boolean r5) {
        /*
            r0 = 2
            if (r3 == 0) goto Lf
            r1 = 1
            if (r3 == r1) goto Lf
            if (r3 == r0) goto Lf
            r1 = 3
            if (r3 == r1) goto Lc
            goto L13
        Lc:
            r2 = r2 & (-2)
            goto L13
        Lf:
            if (r4 == 0) goto Lc
            r2 = r2 | 1
        L13:
            if (r4 == 0) goto L17
            r2 = r2 | r0
            goto L19
        L17:
            r2 = r2 & (-3)
        L19:
            if (r5 == 0) goto L1e
            r2 = r2 | 4
            goto L20
        L1e:
            r2 = r2 & (-5)
        L20:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.recents.utilities.Utilities.calculateBackDispositionHints(int, int, boolean, boolean):int");
    }

    public static float dpiFromPx(float f, int i) {
        return f / (i / 160.0f);
    }

    public static boolean isRotationAnimationCCW(int i, int i2) {
        if (i == 0 && i2 == 1) {
            return false;
        }
        if (i == 0 && i2 == 2) {
            return true;
        }
        if (i == 0 && i2 == 3) {
            return true;
        }
        if (i == 1 && i2 == 0) {
            return true;
        }
        if (i == 1 && i2 == 2) {
            return false;
        }
        if (i == 1 && i2 == 3) {
            return true;
        }
        if (i == 2 && i2 == 0) {
            return true;
        }
        if (i == 2 && i2 == 1) {
            return true;
        }
        if (i == 2 && i2 == 3) {
            return false;
        }
        if (i == 3 && i2 == 0) {
            return false;
        }
        if (i == 3 && i2 == 1) {
            return true;
        }
        return i == 3 && i2 == 2;
    }

    public static void postAtFrontOfQueueAsynchronously(Handler handler, Runnable runnable) {
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage().setCallback(runnable));
    }

    public static float computeContrastBetweenColors(int i, int i2) {
        float fRed = Color.red(i) / 255.0f;
        float fGreen = Color.green(i) / 255.0f;
        float fBlue = Color.blue(i) / 255.0f;
        float fPow = ((fRed < 0.03928f ? fRed / 12.92f : (float) Math.pow((fRed + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.2126f) + ((fGreen < 0.03928f ? fGreen / 12.92f : (float) Math.pow((fGreen + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.7152f) + ((fBlue < 0.03928f ? fBlue / 12.92f : (float) Math.pow((fBlue + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.0722f);
        float fRed2 = Color.red(i2) / 255.0f;
        float fGreen2 = Color.green(i2) / 255.0f;
        float fBlue2 = Color.blue(i2) / 255.0f;
        return Math.abs((((((fRed2 < 0.03928f ? fRed2 / 12.92f : (float) Math.pow((fRed2 + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.2126f) + ((fGreen2 < 0.03928f ? fGreen2 / 12.92f : (float) Math.pow((fGreen2 + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.7152f)) + ((fBlue2 < 0.03928f ? fBlue2 / 12.92f : (float) Math.pow((fBlue2 + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.0722f)) + 0.05f) / (fPow + 0.05f));
    }

    public static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static boolean isTablet(Context context) {
        Rect bounds = ((WindowManager) context.getSystemService(WindowManager.class)).getCurrentWindowMetrics().getBounds();
        dpiFromPx(Math.min(bounds.width(), bounds.height()), context.getResources().getConfiguration().densityDpi);
        return false;
    }
}
