package com.android.launcher3.touch;

/* JADX INFO: loaded from: classes.dex */
public class OverScroll {
    public static final float OVERSCROLL_DAMP_FACTOR = 0.07f;

    private static float overScrollInfluenceCurve(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public static int dampedScroll(float amount, int max) {
        if (Float.compare(amount, 0.0f) == 0) {
            return 0;
        }
        float f = max;
        float f2 = amount / f;
        float fAbs = (f2 / Math.abs(f2)) * overScrollInfluenceCurve(Math.abs(f2));
        if (Math.abs(fAbs) >= 1.0f) {
            fAbs /= Math.abs(fAbs);
        }
        return Math.round(fAbs * 0.07f * f);
    }
}
