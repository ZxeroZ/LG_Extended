package com.lge.launcher3.wallpaperblur.adaptivecolorengine;

import android.content.res.Resources;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class AdaptiveColorParam {
    public static final String TAG = "AdaptiveColorParam";
    public static final int TO_MAX = -255;
    private static AdaptiveColorParam sThis;
    public int blurRadius;
    public int[][] litCoef;
    public int[] litStep;
    public int[] opacity;
    public int[][] satCoef;
    public int[] satStep;

    public static AdaptiveColorParam getParam() {
        if (sThis == null) {
            synchronized (AdaptiveColorParam.class) {
                if (sThis == null) {
                    AdaptiveColorParam adaptiveColorParam = new AdaptiveColorParam();
                    adaptiveColorParam.litStep = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 95};
                    adaptiveColorParam.litCoef = new int[][]{new int[]{60, 35, 35, 20, 20, 20, -10, TO_MAX, TO_MAX, TO_MAX, -15}, new int[]{30, 30, 20, 10, 10, -10, -10, -10, -15, -20, -20}, new int[]{20, 20, 10, 5, -5, -20, -20, -20, -35, -45, -45}};
                    adaptiveColorParam.satStep = new int[]{25, 50, 65, 80, 90};
                    adaptiveColorParam.satCoef = new int[][]{new int[]{0, -40, -40, -60, -70, -70}, new int[]{10, 10, 5, 5, -10, -20}, new int[]{35, 30, 20, 20, TO_MAX, TO_MAX}};
                    adaptiveColorParam.opacity = new int[]{15, 55, 70};
                    adaptiveColorParam.blurRadius = dpToPx(20);
                    sThis = adaptiveColorParam;
                }
            }
        }
        return sThis;
    }

    public static int[] getDefaultColors() {
        return new int[]{861561718, -1946143023, -1291785787};
    }

    private static int dpToPx(int dp) {
        float f = Resources.getSystem().getDisplayMetrics().density;
        LGLog.i(TAG, String.format("dpToPx() : dp(%d), density(%.2f)", Integer.valueOf(dp), Float.valueOf(f)));
        return (int) (dp * f);
    }
}
