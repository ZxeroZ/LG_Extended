package com.lge.launcher3.util;

import android.view.MotionEvent;
import android.view.VelocityTracker;

/* JADX INFO: loaded from: classes.dex */
public class TouchPrediction {
    private static final int MAXIMUM_VELOCITY = 10;
    private static final float PREDICTION_RATIO = 0.3f;
    private static final float VSYNCTIMING = 16.6f;
    private float mAlphaValue;
    private float mLastVelocity;
    private VelocityTracker mVelocityTracker;

    public TouchPrediction() {
        predictionInit();
    }

    private void predictionInit() {
        recycleVelocityTracker();
        this.mAlphaValue = 0.0f;
        this.mLastVelocity = 0.0f;
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    public float computePredictionLocation(MotionEvent ev) {
        this.mVelocityTracker.computeCurrentVelocity(1, 10.0f);
        float x = ev.getX();
        float xVelocity = this.mVelocityTracker.getXVelocity();
        float f = xVelocity - this.mLastVelocity;
        this.mLastVelocity = xVelocity;
        float f2 = ((xVelocity + (f * 0.5f)) * VSYNCTIMING * 0.3f) + (this.mAlphaValue * 0.7f);
        this.mAlphaValue = f2;
        return x + f2;
    }

    public void observedEvent(MotionEvent ev) {
        acquireVelocityTrackerAndAddMovement(ev);
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            this.mVelocityTracker.clear();
        } else if (actionMasked == 1 || actionMasked == 3) {
            predictionInit();
        }
    }
}
