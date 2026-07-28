package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.FloatProperty;
import com.android.launcher3.util.DisplayController;

/* JADX INFO: loaded from: classes.dex */
public class SpringAnimationBuilder {
    private static final float THRESHOLD_MULTIPLIER = 0.65f;
    private double a;
    private double b;
    private double beta;
    private double gamma;
    private final Context mContext;
    private float mEndValue;
    private float mStartValue;
    private double mValueThreshold;
    private double mVelocityThreshold;
    private double va;
    private double vb;
    private float mVelocity = 0.0f;
    private float mStiffness = 1500.0f;
    private float mDampingRatio = 0.5f;
    private float mMinVisibleChange = 1.0f;
    private float mDuration = 0.0f;

    public SpringAnimationBuilder(Context context) {
        this.mContext = context;
    }

    public SpringAnimationBuilder setEndValue(float value) {
        this.mEndValue = value;
        return this;
    }

    public SpringAnimationBuilder setStartValue(float value) {
        this.mStartValue = value;
        return this;
    }

    public SpringAnimationBuilder setValues(float... values) {
        if (values.length > 1) {
            this.mStartValue = values[0];
            this.mEndValue = values[values.length - 1];
        } else {
            this.mEndValue = values[0];
        }
        return this;
    }

    public SpringAnimationBuilder setStiffness(float stiffness) {
        if (stiffness <= 0.0f) {
            throw new IllegalArgumentException("Spring stiffness constant must be positive.");
        }
        this.mStiffness = stiffness;
        return this;
    }

    public SpringAnimationBuilder setDampingRatio(float dampingRatio) {
        if (dampingRatio <= 0.0f || dampingRatio >= 1.0f) {
            throw new IllegalArgumentException("Damping ratio must be between 0 and 1");
        }
        this.mDampingRatio = dampingRatio;
        return this;
    }

    public SpringAnimationBuilder setMinimumVisibleChange(float minimumVisibleChange) {
        if (minimumVisibleChange <= 0.0f) {
            throw new IllegalArgumentException("Minimum visible change must be positive.");
        }
        this.mMinVisibleChange = minimumVisibleChange;
        return this;
    }

    public SpringAnimationBuilder setStartVelocity(float startVelocity) {
        this.mVelocity = startVelocity;
        return this;
    }

    public float getInterpolatedValue(float fraction) {
        return getValue(this.mDuration * fraction);
    }

    private float getValue(float time) {
        double d = time;
        return ((float) (exponentialComponent(d) * cosSinX(d))) + this.mEndValue;
    }

    public SpringAnimationBuilder computeParams() {
        int singleFrameMs = DisplayController.getSingleFrameMs(this.mContext);
        double dSqrt = Math.sqrt(this.mStiffness);
        float f = this.mDampingRatio;
        double dSqrt2 = Math.sqrt(1.0f - (f * f)) * dSqrt;
        double d = ((double) (this.mDampingRatio * 2.0f)) * dSqrt;
        this.beta = d;
        this.gamma = dSqrt2;
        double d2 = this.mStartValue - this.mEndValue;
        this.a = d2;
        double d3 = ((d * d2) / (dSqrt2 * 2.0d)) + (((double) this.mVelocity) / dSqrt2);
        this.b = d3;
        this.va = ((d2 * d) / 2.0d) - (d3 * dSqrt2);
        this.vb = (dSqrt2 * d2) + ((d * d3) / 2.0d);
        double d4 = this.mMinVisibleChange * THRESHOLD_MULTIPLIER;
        this.mValueThreshold = d4;
        double d5 = singleFrameMs;
        this.mVelocityThreshold = (d4 * 1000.0d) / d5;
        double dAtan2 = Math.atan2(-d2, d3);
        double d6 = this.gamma;
        double d7 = dAtan2 / d6;
        double d8 = 3.141592653589793d / d6;
        while (true) {
            if (d7 >= 0.0d && Math.abs(exponentialComponent(d7) * cosSinV(d7)) < this.mVelocityThreshold) {
                break;
            }
            d7 += d8;
        }
        double dMax = Math.max(0.0d, d7 - (d8 / 2.0d));
        double d9 = d5 / 2000.0d;
        while (d7 - dMax >= d9) {
            double d10 = (dMax + d7) / 2.0d;
            if (isAtEquilibrium(d10)) {
                d7 = d10;
            } else {
                dMax = d10;
            }
        }
        this.mDuration = (float) d7;
        return this;
    }

    public long getDuration() {
        return (long) (((double) this.mDuration) * 1000.0d);
    }

    public <T> ValueAnimator build(final T target, final FloatProperty<T> property) {
        computeParams();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, this.mDuration);
        valueAnimatorOfFloat.setDuration(getDuration()).setInterpolator(Interpolators.LINEAR);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.anim.-$$Lambda$SpringAnimationBuilder$BWOq9bk0Gfl6FqIVTW8shBux3vM
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$build$0$SpringAnimationBuilder(property, target, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.anim.SpringAnimationBuilder.1
            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animation) {
                property.set(target, Float.valueOf(SpringAnimationBuilder.this.mEndValue));
            }
        });
        return valueAnimatorOfFloat;
    }

    public /* synthetic */ void lambda$build$0$SpringAnimationBuilder(FloatProperty floatProperty, Object obj, ValueAnimator valueAnimator) {
        floatProperty.set(obj, Float.valueOf(getInterpolatedValue(valueAnimator.getAnimatedFraction())));
    }

    private boolean isAtEquilibrium(double t) {
        double dExponentialComponent = exponentialComponent(t);
        return Math.abs(cosSinX(t) * dExponentialComponent) < this.mValueThreshold && Math.abs(dExponentialComponent * cosSinV(t)) < this.mVelocityThreshold;
    }

    private double exponentialComponent(double t) {
        return Math.pow(2.718281828459045d, ((-this.beta) * t) / 2.0d);
    }

    private double cosSinX(double t) {
        return cosSin(t, this.a, this.b);
    }

    private double cosSinV(double t) {
        return cosSin(t, this.va, this.vb);
    }

    private double cosSin(double t, double cosFactor, double sinFactor) {
        double d = t * this.gamma;
        return (cosFactor * Math.cos(d)) + (sinFactor * Math.sin(d));
    }
}
