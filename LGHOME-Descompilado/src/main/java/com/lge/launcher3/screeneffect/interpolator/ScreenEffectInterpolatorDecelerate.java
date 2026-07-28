package com.lge.launcher3.screeneffect.interpolator;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectInterpolatorDecelerate implements Interpolator {
    private float mBase = 300.0f;
    private float mDraft = 0.0f;

    static float computeLog(float t, float base, float drift) {
        return ((float) (-Math.pow(base, -t))) + 1.0f + (drift * t);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return (computeLog(t, this.mBase, this.mDraft) * 1.0f) / computeLog(1.0f, this.mBase, this.mDraft);
    }
}
