package com.android.quickstep.util;

import android.animation.ValueAnimator;
import android.view.animation.Interpolator;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class MultiValueUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private final ArrayList<FloatProp> mAllProperties = new ArrayList<>();

    public abstract void onUpdate(float percent);

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public final void onAnimationUpdate(ValueAnimator animator) {
        float animatedFraction = animator.getAnimatedFraction();
        float duration = animator.getDuration() * animatedFraction;
        for (int size = this.mAllProperties.size() - 1; size >= 0; size--) {
            FloatProp floatProp = this.mAllProperties.get(size);
            float interpolation = floatProp.mInterpolator.getInterpolation(Math.min(1.0f, Math.max(0.0f, duration - floatProp.mDelay) / floatProp.mDuration));
            floatProp.value = (floatProp.mEnd * interpolation) + (floatProp.mStart * (1.0f - interpolation));
        }
        onUpdate(animatedFraction);
    }

    public final class FloatProp {
        private final float mDelay;
        private final float mDuration;
        private final float mEnd;
        private final Interpolator mInterpolator;
        private final float mStart;
        public float value;

        public FloatProp(float start, float end, float delay, float duration, Interpolator i) {
            this.mStart = start;
            this.value = start;
            this.mEnd = end;
            this.mDelay = delay;
            this.mDuration = duration;
            this.mInterpolator = i;
            MultiValueUpdateListener.this.mAllProperties.add(this);
        }
    }
}
