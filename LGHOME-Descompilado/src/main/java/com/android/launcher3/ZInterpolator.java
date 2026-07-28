package com.android.launcher3;

import android.animation.TimeInterpolator;

/* JADX INFO: compiled from: WorkspaceStateTransitionAnimation.java */
/* JADX INFO: loaded from: classes.dex */
class ZInterpolator implements TimeInterpolator {
    private float focalLength;

    public ZInterpolator(float foc) {
        this.focalLength = foc;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        float f = this.focalLength;
        return (1.0f - (f / (input + f))) / (1.0f - (f / (f + 1.0f)));
    }
}
