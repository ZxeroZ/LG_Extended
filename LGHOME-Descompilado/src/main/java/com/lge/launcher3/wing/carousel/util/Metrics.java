package com.lge.launcher3.wing.carousel.util;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
public class Metrics {
    public static float convertDpToPixel(float dp, Context context) {
        return dp * getDensity(context);
    }

    public static float convertPixelToDp(float px, Context context) {
        return px / getDensity(context);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
