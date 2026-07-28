package com.android.launcher3.notification;

import android.animation.Animator;
import android.content.Context;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class FlingAnimationUtils {
    private static final float HIGH_VELOCITY_DP_PER_SECOND = 3000.0f;
    private static final float LINEAR_OUT_FASTER_IN_X2 = 0.5f;
    private static final float LINEAR_OUT_FASTER_IN_Y2_MAX = 0.5f;
    private static final float LINEAR_OUT_FASTER_IN_Y2_MIN = 0.4f;
    private static final float LINEAR_OUT_SLOW_IN_START_GRADIENT = 0.75f;
    private static final float LINEAR_OUT_SLOW_IN_X2 = 0.35f;
    private static final float LINEAR_OUT_SLOW_IN_X2_MAX = 0.68f;
    private static final float MIN_VELOCITY_DP_PER_SECOND = 250.0f;
    private AnimatorProperties mAnimatorProperties;
    private float mCachedStartGradient;
    private float mCachedVelocityFactor;
    private float mHighVelocityPxPerSecond;
    private PathInterpolator mInterpolator;
    private float mLinearOutSlowInX2;
    private float mMaxLengthSeconds;
    private float mMinVelocityPxPerSecond;
    private final float mSpeedUpFactor;
    private final float mY2;

    private static float interpolate(float start, float end, float amount) {
        return (start * (1.0f - amount)) + (end * amount);
    }

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds) {
        this(ctx, maxLengthSeconds, 0.0f);
    }

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds, float speedUpFactor) {
        this(ctx, maxLengthSeconds, speedUpFactor, -1.0f, 1.0f);
    }

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds, float speedUpFactor, float x2, float y2) {
        this.mAnimatorProperties = new AnimatorProperties();
        this.mCachedStartGradient = -1.0f;
        this.mCachedVelocityFactor = -1.0f;
        this.mMaxLengthSeconds = maxLengthSeconds;
        this.mSpeedUpFactor = speedUpFactor;
        if (x2 < 0.0f) {
            this.mLinearOutSlowInX2 = interpolate(LINEAR_OUT_SLOW_IN_X2, LINEAR_OUT_SLOW_IN_X2_MAX, speedUpFactor);
        } else {
            this.mLinearOutSlowInX2 = x2;
        }
        this.mY2 = y2;
        this.mMinVelocityPxPerSecond = ctx.getResources().getDisplayMetrics().density * MIN_VELOCITY_DP_PER_SECOND;
        this.mHighVelocityPxPerSecond = ctx.getResources().getDisplayMetrics().density * HIGH_VELOCITY_DP_PER_SECOND;
    }

    public void apply(Animator animator, float currValue, float endValue, float velocity) {
        apply(animator, currValue, endValue, velocity, Math.abs(endValue - currValue));
    }

    public void apply(ViewPropertyAnimator animator, float currValue, float endValue, float velocity) {
        apply(animator, currValue, endValue, velocity, Math.abs(endValue - currValue));
    }

    public void apply(Animator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    public void apply(ViewPropertyAnimator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    private AnimatorProperties getProperties(float currValue, float endValue, float velocity, float maxDistance) {
        float f = endValue - currValue;
        float fSqrt = (float) (((double) this.mMaxLengthSeconds) * Math.sqrt(Math.abs(f) / maxDistance));
        float fAbs = Math.abs(f);
        float fAbs2 = Math.abs(velocity);
        float fMin = this.mSpeedUpFactor != 0.0f ? Math.min(fAbs2 / HIGH_VELOCITY_DP_PER_SECOND, 1.0f) : 1.0f;
        float fInterpolate = interpolate(0.75f, this.mY2 / this.mLinearOutSlowInX2, fMin);
        float f2 = (fInterpolate * fAbs) / fAbs2;
        Interpolator interpolator = getInterpolator(fInterpolate, fMin);
        if (f2 <= fSqrt) {
            this.mAnimatorProperties.interpolator = interpolator;
            fSqrt = f2;
        } else if (fAbs2 >= this.mMinVelocityPxPerSecond) {
            this.mAnimatorProperties.interpolator = new InterpolatorInterpolator(new VelocityInterpolator(fSqrt, fAbs2, fAbs), interpolator, Interpolators.LINEAR_OUT_SLOW_IN);
        } else {
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        this.mAnimatorProperties.duration = (long) (fSqrt * 1000.0f);
        return this.mAnimatorProperties;
    }

    private Interpolator getInterpolator(float startGradient, float velocityFactor) {
        if (startGradient != this.mCachedStartGradient || velocityFactor != this.mCachedVelocityFactor) {
            float f = this.mSpeedUpFactor * (1.0f - velocityFactor);
            this.mInterpolator = new PathInterpolator(f, f * startGradient, this.mLinearOutSlowInX2, this.mY2);
            this.mCachedStartGradient = startGradient;
            this.mCachedVelocityFactor = velocityFactor;
        }
        return this.mInterpolator;
    }

    public void applyDismissing(Animator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties dismissingProperties = getDismissingProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(dismissingProperties.duration);
        animator.setInterpolator(dismissingProperties.interpolator);
    }

    public void applyDismissing(ViewPropertyAnimator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties dismissingProperties = getDismissingProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(dismissingProperties.duration);
        animator.setInterpolator(dismissingProperties.interpolator);
    }

    private AnimatorProperties getDismissingProperties(float currValue, float endValue, float velocity, float maxDistance) {
        float f = endValue - currValue;
        float fPow = (float) (((double) this.mMaxLengthSeconds) * Math.pow(Math.abs(f) / maxDistance, 0.5d));
        float fAbs = Math.abs(f);
        float fAbs2 = Math.abs(velocity);
        float fCalculateLinearOutFasterInY2 = calculateLinearOutFasterInY2(fAbs2);
        PathInterpolator pathInterpolator = new PathInterpolator(0.0f, 0.0f, 0.5f, fCalculateLinearOutFasterInY2);
        float f2 = ((fCalculateLinearOutFasterInY2 / 0.5f) * fAbs) / fAbs2;
        if (f2 <= fPow) {
            this.mAnimatorProperties.interpolator = pathInterpolator;
            fPow = f2;
        } else if (fAbs2 >= this.mMinVelocityPxPerSecond) {
            this.mAnimatorProperties.interpolator = new InterpolatorInterpolator(new VelocityInterpolator(fPow, fAbs2, fAbs), pathInterpolator, Interpolators.LINEAR_OUT_SLOW_IN);
        } else {
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_LINEAR_IN;
        }
        this.mAnimatorProperties.duration = (long) (fPow * 1000.0f);
        return this.mAnimatorProperties;
    }

    private float calculateLinearOutFasterInY2(float velocity) {
        float f = this.mMinVelocityPxPerSecond;
        float fMax = Math.max(0.0f, Math.min(1.0f, (velocity - f) / (this.mHighVelocityPxPerSecond - f)));
        return ((1.0f - fMax) * 0.4f) + (fMax * 0.5f);
    }

    public float getMinVelocityPxPerSecond() {
        return this.mMinVelocityPxPerSecond;
    }

    private static final class InterpolatorInterpolator implements Interpolator {
        private Interpolator mCrossfader;
        private Interpolator mInterpolator1;
        private Interpolator mInterpolator2;

        InterpolatorInterpolator(Interpolator interpolator1, Interpolator interpolator2, Interpolator crossfader) {
            this.mInterpolator1 = interpolator1;
            this.mInterpolator2 = interpolator2;
            this.mCrossfader = crossfader;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            float interpolation = this.mCrossfader.getInterpolation(input);
            return ((1.0f - interpolation) * this.mInterpolator1.getInterpolation(input)) + (interpolation * this.mInterpolator2.getInterpolation(input));
        }
    }

    private static final class VelocityInterpolator implements Interpolator {
        private float mDiff;
        private float mDurationSeconds;
        private float mVelocity;

        private VelocityInterpolator(float durationSeconds, float velocity, float diff) {
            this.mDurationSeconds = durationSeconds;
            this.mVelocity = velocity;
            this.mDiff = diff;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            return ((input * this.mDurationSeconds) * this.mVelocity) / this.mDiff;
        }
    }

    private static class AnimatorProperties {
        long duration;
        Interpolator interpolator;

        private AnimatorProperties() {
        }
    }
}
