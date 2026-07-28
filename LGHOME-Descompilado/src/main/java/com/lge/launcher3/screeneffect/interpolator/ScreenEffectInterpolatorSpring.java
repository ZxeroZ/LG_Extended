package com.lge.launcher3.screeneffect.interpolator;

import android.view.animation.Interpolator;
import com.lge.launcher3.util.MathFunctionUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectInterpolatorSpring implements Interpolator {
    private static final float ACCELERATE_CURVE_EXPONENT = 1.7f;
    private static final int BOUNCE_COUNT = 1;
    private static final float SIN_CURVE_DEFAULT_AMP = 0.001f;
    private static final float SIN_CURVE_NEXT_AMP_RATIO = 0.8f;
    private static final float SIN_CURVE_START_POINT = 0.7f;

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float f = 0.7f;
        if (t < 0.7f) {
            return getAccelerateInterpolation(MathFunctionUtils.normalize(t, 0.0f, 0.7f), ACCELERATE_CURVE_EXPONENT);
        }
        float f2 = SIN_CURVE_DEFAULT_AMP;
        int i = 0;
        while (i < 1) {
            int i2 = i + 1;
            float f3 = i2 < 1 ? 0.3f + f : 1.00001f;
            if (f <= t && t < f3) {
                return getSinInterpolation(MathFunctionUtils.normalize(t, f, f3) + (i % 2 != 0 ? -1.0f : 0.0f), f2);
            }
            f2 *= 0.8f;
            f = f3;
            i = i2;
        }
        return 0.0f;
    }

    private float getAccelerateInterpolation(float t, float exponent) {
        return (float) ((Math.pow((t * (-1.0f)) + 1.0f, exponent) * (-1.0d)) + 1.0d);
    }

    private float getSinInterpolation(float t, float amplitude) {
        return (float) ((Math.sin(((double) t) * 3.141592653589793d) * ((double) amplitude)) + 1.0d);
    }
}
