package com.lge.launcher3.screeneffect.interpolator;

import android.content.Context;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectInterpolatorOvershoot implements Interpolator {
    private static final float DEFAULT_TENSION = 2.0f;
    private final float mDefaultTension;
    private float mTension;

    public ScreenEffectInterpolatorOvershoot(Context context) {
        this(context, 2.0f);
    }

    public ScreenEffectInterpolatorOvershoot(Context context, float defaultTension) {
        this.mTension = 0.0f;
        this.mDefaultTension = defaultTension < 0.0f ? 2.0f : defaultTension;
    }

    public void computeTension(float tensionRatio) {
        this.mTension = this.mDefaultTension * tensionRatio;
    }

    public void resetTension() {
        this.mTension = this.mDefaultTension;
    }

    public void disableTension() {
        this.mTension = 0.0f;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float f = t - 1.0f;
        float f2 = this.mTension;
        return (f * f * f * f * (((f2 + 1.0f) * f) + f2)) + 1.0f;
    }
}
