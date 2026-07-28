package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.lge.launcher3.screeneffect.LauncherScrollerWatcher;

/* JADX INFO: loaded from: classes.dex */
public class LauncherScroller {
    private static final int DEFAULT_DURATION = 250;
    private static final float END_TENSION = 1.0f;
    private static final int FLING_MODE = 1;
    private static final float FRICTION = 1.64f;
    private static final float INFLEXION = 0.35f;
    private static final int NB_SAMPLES = 100;
    private static final float P1 = 0.175f;
    private static final float P2 = 0.35000002f;
    private static final int SCROLL_MODE = 0;
    private static final float START_TENSION = 0.5f;
    private static float sViscousFluidNormalize;
    private static float sViscousFluidScale;
    private float mCurrVelocity;
    private int mCurrX;
    private int mCurrY;
    private float mDeceleration;
    private float mDeltaX;
    private float mDeltaY;
    private int mDistance;
    private int mDuration;
    private float mDurationReciprocal;
    private int mFinalX;
    private int mFinalY;
    private boolean mFinished;
    private float mFlingFriction;
    private boolean mFlywheel;
    private TimeInterpolator mInterpolator;
    private boolean mIsStarted;
    private int mMaxX;
    private int mMaxY;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private float mPhysicalCoeff;
    private final float mPpi;
    private long mStartTime;
    private int mStartX;
    private int mStartY;
    private float mVelocity;
    private static float DECELERATION_RATE = (float) (Math.log(0.78d) / Math.log(0.9d));
    private static final float[] SPLINE_POSITION = new float[101];
    private static final float[] SPLINE_TIME = new float[101];

    static {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11 = 0.0f;
        float f12 = 0.0f;
        for (int i = 0; i < 100; i++) {
            float f13 = i / 100.0f;
            float f14 = 1.0f;
            while (true) {
                f = 2.0f;
                f2 = ((f14 - f11) / 2.0f) + f11;
                f3 = 3.0f;
                f4 = 1.0f - f2;
                f5 = f2 * 3.0f * f4;
                f6 = f2 * f2 * f2;
                float f15 = (((f4 * P1) + (f2 * P2)) * f5) + f6;
                if ((f15 >= f13 ? f15 - f13 : (-f15) + f13) < 1.0E-5d) {
                    break;
                } else if (f15 > f13) {
                    f14 = f2;
                } else {
                    f11 = f2;
                }
            }
            SPLINE_POSITION[i] = (f5 * ((f4 * 0.5f) + f2)) + f6;
            float f16 = 1.0f;
            while (true) {
                f7 = ((f16 - f12) / f) + f12;
                f8 = 1.0f - f7;
                f9 = f7 * f3 * f8;
                f10 = f7 * f7 * f7;
                float f17 = (((f8 * 0.5f) + f7) * f9) + f10;
                if ((f17 >= f13 ? f17 - f13 : (-f17) + f13) < 1.0E-5d) {
                    break;
                }
                if (f17 > f13) {
                    f16 = f7;
                } else {
                    f12 = f7;
                }
                f = 2.0f;
                f3 = 3.0f;
            }
            SPLINE_TIME[i] = (f9 * ((f8 * P1) + (f7 * P2))) + f10;
        }
        float[] fArr = SPLINE_POSITION;
        SPLINE_TIME[100] = 1.0f;
        fArr[100] = 1.0f;
        sViscousFluidScale = 8.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public LauncherScroller(Context context) {
        this(context, null);
    }

    public LauncherScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, context.getApplicationInfo().targetSdkVersion >= 11);
    }

    public LauncherScroller(Context context, Interpolator interpolator, boolean flywheel) {
        this.mFlingFriction = ViewConfiguration.getScrollFriction();
        this.mIsStarted = false;
        this.mFinished = true;
        this.mInterpolator = interpolator;
        this.mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = flywheel;
        this.mPhysicalCoeff = computeDeceleration(FRICTION);
    }

    public final void setFriction(float friction) {
        this.mDeceleration = computeDeceleration(friction);
        this.mFlingFriction = friction;
    }

    private float computeDeceleration(float friction) {
        return this.mPpi * 386.0878f * friction;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceFinished(boolean finished) {
        this.mFinished = finished;
        if (finished) {
            LauncherScrollerWatcher.getInstance().notifyFinishListeners(this.mCurrX, this.mCurrY, LauncherScrollerWatcher.ScrollerFinishType.FORCE_FINISHED);
            this.mIsStarted = false;
        }
    }

    public final int getDuration() {
        return this.mDuration;
    }

    public final int getCurrX() {
        return this.mCurrX;
    }

    public final int getCurrY() {
        return this.mCurrY;
    }

    public float getCurrVelocity() {
        return this.mMode == 1 ? this.mCurrVelocity : this.mVelocity - ((this.mDeceleration * timePassed()) / 2000.0f);
    }

    public final int getStartX() {
        return this.mStartX;
    }

    public final int getStartY() {
        return this.mStartY;
    }

    public final int getFinalX() {
        return this.mFinalX;
    }

    public final int getFinalY() {
        return this.mFinalY;
    }

    public boolean computeScrollOffset() {
        float interpolation;
        if (this.mFinished) {
            if (this.mIsStarted) {
                LauncherScrollerWatcher.getInstance().notifyFinishListeners(this.mCurrX, this.mCurrY, LauncherScrollerWatcher.ScrollerFinishType.TIME_EXPIRATION);
                this.mIsStarted = false;
            }
            return false;
        }
        int iCurrentAnimationTimeMillis = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        int i = this.mDuration;
        if (iCurrentAnimationTimeMillis < i) {
            int i2 = this.mMode;
            if (i2 == 0) {
                float f = iCurrentAnimationTimeMillis * this.mDurationReciprocal;
                TimeInterpolator timeInterpolator = this.mInterpolator;
                if (timeInterpolator == null) {
                    interpolation = viscousFluid(f);
                } else {
                    interpolation = timeInterpolator.getInterpolation(f);
                }
                this.mCurrX = this.mStartX + Math.round(this.mDeltaX * interpolation);
                this.mCurrY = this.mStartY + Math.round(interpolation * this.mDeltaY);
            } else if (i2 == 1) {
                float f2 = iCurrentAnimationTimeMillis / i;
                int i3 = (int) (f2 * 100.0f);
                float f3 = 1.0f;
                float f4 = 0.0f;
                if (i3 < 100) {
                    float f5 = i3 / 100.0f;
                    int i4 = i3 + 1;
                    float[] fArr = SPLINE_POSITION;
                    float f6 = fArr[i3];
                    f4 = (fArr[i4] - f6) / ((i4 / 100.0f) - f5);
                    f3 = f6 + ((f2 - f5) * f4);
                }
                this.mCurrVelocity = ((f4 * this.mDistance) / i) * 1000.0f;
                int iRound = this.mStartX + Math.round((this.mFinalX - r0) * f3);
                this.mCurrX = iRound;
                int iMin = Math.min(iRound, this.mMaxX);
                this.mCurrX = iMin;
                this.mCurrX = Math.max(iMin, this.mMinX);
                int iRound2 = this.mStartY + Math.round(f3 * (this.mFinalY - r0));
                this.mCurrY = iRound2;
                int iMin2 = Math.min(iRound2, this.mMaxY);
                this.mCurrY = iMin2;
                int iMax = Math.max(iMin2, this.mMinY);
                this.mCurrY = iMax;
                if (this.mCurrX == this.mFinalX && iMax == this.mFinalY) {
                    this.mFinished = true;
                }
            }
        } else {
            this.mCurrX = this.mFinalX;
            this.mCurrY = this.mFinalY;
            this.mFinished = true;
        }
        if (this.mIsStarted && this.mFinished) {
            LauncherScrollerWatcher.getInstance().notifyFinishListeners(this.mCurrX, this.mCurrY, LauncherScrollerWatcher.ScrollerFinishType.TIME_EXPIRATION);
            this.mIsStarted = false;
        }
        return true;
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = duration;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        this.mFinalX = startX + dx;
        this.mFinalY = startY + dy;
        this.mDeltaX = dx;
        this.mDeltaY = dy;
        this.mDurationReciprocal = 1.0f / this.mDuration;
        LauncherScrollerWatcher.getInstance().notifyStartListeners(this.mStartX, this.mStartY);
        this.mIsStarted = true;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        if (this.mFlywheel && !this.mFinished) {
            float currVelocity = getCurrVelocity();
            float f = this.mFinalX - this.mStartX;
            float f2 = this.mFinalY - this.mStartY;
            float fHypot = (float) Math.hypot(f, f2);
            float f3 = (f / fHypot) * currVelocity;
            float f4 = (f2 / fHypot) * currVelocity;
            float f5 = velocityX;
            if (Math.signum(f5) == Math.signum(f3)) {
                float f6 = velocityY;
                if (Math.signum(f6) == Math.signum(f4)) {
                    velocityX = (int) (f5 + f3);
                    velocityY = (int) (f6 + f4);
                }
            }
        }
        this.mMode = 1;
        this.mFinished = false;
        float fHypot2 = (float) Math.hypot(velocityX, velocityY);
        this.mVelocity = fHypot2;
        this.mDuration = getSplineFlingDuration(fHypot2);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        float f7 = fHypot2 == 0.0f ? 1.0f : velocityX / fHypot2;
        float f8 = fHypot2 != 0.0f ? velocityY / fHypot2 : 1.0f;
        double splineFlingDistance = getSplineFlingDistance(fHypot2);
        this.mDistance = (int) (((double) Math.signum(fHypot2)) * splineFlingDistance);
        this.mMinX = minX;
        this.mMaxX = maxX;
        this.mMinY = minY;
        this.mMaxY = maxY;
        int iRound = startX + ((int) Math.round(((double) f7) * splineFlingDistance));
        this.mFinalX = iRound;
        int iMin = Math.min(iRound, this.mMaxX);
        this.mFinalX = iMin;
        this.mFinalX = Math.max(iMin, this.mMinX);
        int iRound2 = startY + ((int) Math.round(splineFlingDistance * ((double) f8)));
        this.mFinalY = iRound2;
        int iMin2 = Math.min(iRound2, this.mMaxY);
        this.mFinalY = iMin2;
        this.mFinalY = Math.max(iMin2, this.mMinY);
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log((Math.abs(velocity) * INFLEXION) / (this.mFlingFriction * this.mPhysicalCoeff));
    }

    private int getSplineFlingDuration(float velocity) {
        return (int) (Math.exp(getSplineDeceleration(velocity) / (((double) DECELERATION_RATE) - 1.0d)) * 1000.0d);
    }

    private double getSplineFlingDistance(float velocity) {
        double splineDeceleration = getSplineDeceleration(velocity);
        float f = DECELERATION_RATE;
        return ((double) (this.mFlingFriction * this.mPhysicalCoeff)) * Math.exp((((double) f) / (((double) f) - 1.0d)) * splineDeceleration);
    }

    static float viscousFluid(float x) {
        float fExp;
        float f = x * sViscousFluidScale;
        if (f < 1.0f) {
            fExp = f - (1.0f - ((float) Math.exp(-f)));
        } else {
            fExp = ((1.0f - ((float) Math.exp(1.0f - f))) * 0.63212055f) + 0.36787945f;
        }
        return fExp * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
        LauncherScrollerWatcher.getInstance().notifyFinishListeners(this.mCurrX, this.mCurrY, LauncherScrollerWatcher.ScrollerFinishType.ABORT_ANIMATION);
        this.mIsStarted = false;
    }

    public void extendDuration(int extend) {
        int iTimePassed = timePassed() + extend;
        this.mDuration = iTimePassed;
        this.mDurationReciprocal = 1.0f / iTimePassed;
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int newX) {
        this.mFinalX = newX;
        this.mDeltaX = newX - this.mStartX;
        this.mFinished = false;
    }

    public void setFinalY(int newY) {
        this.mFinalY = newY;
        this.mDeltaY = newY - this.mStartY;
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        return !this.mFinished && Math.signum(xvel) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(yvel) == Math.signum((float) (this.mFinalY - this.mStartY));
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }
}
