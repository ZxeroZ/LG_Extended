package com.android.launcher3;

import android.animation.TimeInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class LogAccelerateInterpolator implements TimeInterpolator {
    int mBase;
    int mDrift;
    final float mLogScale;

    public LogAccelerateInterpolator(int base, int drift) {
        this.mBase = base;
        this.mDrift = drift;
        this.mLogScale = 1.0f / computeLog(1.0f, base, drift);
    }

    static float computeLog(float t, int base, int drift) {
        return ((float) (-Math.pow(base, -t))) + 1.0f + (drift * t);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return 1.0f - (computeLog(1.0f - t, this.mBase, this.mDrift) * this.mLogScale);
    }
}
