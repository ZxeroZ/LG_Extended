package com.android.launcher3.util;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.anim.Interpolators;
import com.android.systemui.plugins.ResourceProvider;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class OverScroller {
    private static final int DEFAULT_DURATION = 250;
    private static final int FLING_MODE = 1;
    private static final int SCROLL_MODE = 0;
    private final boolean mFlywheel;
    private TimeInterpolator mInterpolator;
    private int mMode;
    private final SplineOverScroller mScroller;

    public OverScroller(Context context) {
        this(context, null);
    }

    public OverScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public OverScroller(Context context, Interpolator interpolator, boolean flywheel) {
        if (interpolator == null) {
            this.mInterpolator = Interpolators.SCROLL;
        } else {
            this.mInterpolator = interpolator;
        }
        this.mFlywheel = flywheel;
        this.mScroller = new SplineOverScroller(context);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        if (interpolator == null) {
            this.mInterpolator = Interpolators.SCROLL;
        } else {
            this.mInterpolator = interpolator;
        }
    }

    public final void setFriction(float friction) {
        this.mScroller.setFriction(friction);
    }

    public final boolean isFinished() {
        return this.mScroller.mFinished;
    }

    public final void forceFinished(boolean finished) {
        this.mScroller.mFinished = finished;
    }

    public final int getCurrPos() {
        return this.mScroller.mCurrentPosition;
    }

    public float getCurrVelocity() {
        return this.mScroller.mCurrVelocity;
    }

    public final int getStartPos() {
        return this.mScroller.mStart;
    }

    public final int getFinalPos() {
        return this.mScroller.mFinal;
    }

    public final int getDuration() {
        return this.mScroller.mDuration;
    }

    public void extendDuration(int extend) {
        this.mScroller.extendDuration(extend);
    }

    public void setFinalPos(int newPos) {
        this.mScroller.setFinalPosition(newPos);
    }

    public boolean computeScrollOffset() {
        if (isFinished()) {
            return false;
        }
        int i = this.mMode;
        if (i == 0) {
            if (isSpringing()) {
                return true;
            }
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() - this.mScroller.mStartTime;
            int i2 = this.mScroller.mDuration;
            if (jCurrentAnimationTimeMillis < i2) {
                this.mScroller.updateScroll(this.mInterpolator.getInterpolation(jCurrentAnimationTimeMillis / i2));
            } else {
                abortAnimation();
            }
        } else if (i == 1 && !this.mScroller.mFinished && !this.mScroller.update() && !this.mScroller.continueWhenFinished()) {
            this.mScroller.finish();
        }
        return true;
    }

    public void startScroll(int start, int delta) {
        startScroll(start, delta, 250);
    }

    public void startScroll(int start, int delta, int duration) {
        this.mMode = 0;
        this.mScroller.startScroll(start, delta, duration);
    }

    public void startScrollSpring(int start, int delta, int duration, float velocity) {
        this.mMode = 0;
        this.mScroller.mState = 3;
        this.mScroller.startScroll(start, delta, duration, velocity);
    }

    public boolean springBack(int start, int min, int max) {
        this.mMode = 1;
        return this.mScroller.springback(start, min, max);
    }

    public void fling(int start, int velocity, int min, int max) {
        fling(start, velocity, min, max, 0);
    }

    public void fling(int start, int velocity, int min, int max, int over) {
        if (this.mFlywheel && !isFinished()) {
            float f = this.mScroller.mCurrVelocity;
            float f2 = velocity;
            if (Math.signum(f2) == Math.signum(f)) {
                velocity = (int) (f2 + f);
            }
        }
        this.mMode = 1;
        this.mScroller.fling(start, velocity, min, max, over);
    }

    public void notifyEdgeReached(int start, int finalPos, int over) {
        this.mScroller.notifyEdgeReached(start, finalPos, over);
    }

    public boolean isOverScrolled() {
        return (this.mScroller.mFinished || this.mScroller.mState == 0) ? false : true;
    }

    public void abortAnimation() {
        this.mScroller.finish();
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mScroller.mStartTime);
    }

    public boolean isSpringing() {
        return this.mScroller.mState == 3 && !isFinished();
    }

    static class SplineOverScroller {
        private static final int BALLISTIC = 2;
        private static final int CUBIC = 1;
        private static final float END_TENSION = 1.0f;
        private static final float GRAVITY = 2000.0f;
        private static final float INFLEXION = 0.35f;
        private static final int NB_SAMPLES = 100;
        private static final float P1 = 0.175f;
        private static final float P2 = 0.35000002f;
        private static final int SPLINE = 0;
        private static final int SPRING = 3;
        private static final float START_TENSION = 0.5f;
        private Context mContext;
        private float mCurrVelocity;
        private int mCurrentPosition;
        private float mDeceleration;
        private int mDuration;
        private int mFinal;
        private int mOver;
        private float mPhysicalCoeff;
        private int mSplineDistance;
        private int mSplineDuration;
        private SpringAnimation mSpring;
        private int mStart;
        private long mStartTime;
        private int mVelocity;
        private static float DECELERATION_RATE = (float) (Math.log(0.78d) / Math.log(0.9d));
        private static final float[] SPLINE_POSITION = new float[101];
        private static final float[] SPLINE_TIME = new float[101];
        private static final FloatPropertyCompat<SplineOverScroller> SPRING_PROPERTY = new FloatPropertyCompat<SplineOverScroller>("splineOverScrollerSpring") { // from class: com.android.launcher3.util.OverScroller.SplineOverScroller.1
            /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public float getValue(SplineOverScroller scroller) {
                return scroller.mCurrentPosition;
            }

            /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public void setValue(SplineOverScroller scroller, float value) {
                scroller.mCurrentPosition = (int) value;
            }
        };
        private float mFlingFriction = ViewConfiguration.getScrollFriction();
        private int mState = 0;
        private boolean mFinished = true;

        private static float getDeceleration(int velocity) {
            if (velocity > 0) {
                return -2000.0f;
            }
            return GRAVITY;
        }

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
                    if (Math.abs(f15 - f13) < 1.0E-5d) {
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
                    if (Math.abs(f17 - f13) < 1.0E-5d) {
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
        }

        void setFriction(float friction) {
            this.mFlingFriction = friction;
        }

        SplineOverScroller(Context context) {
            this.mContext = context;
            this.mPhysicalCoeff = context.getResources().getDisplayMetrics().density * 160.0f * 386.0878f * 0.84f;
        }

        void updateScroll(float q) {
            if (this.mState == 3) {
                return;
            }
            this.mCurrentPosition = this.mStart + Math.round(q * (this.mFinal - r0));
        }

        private void adjustDuration(int start, int oldFinal, int newFinal) {
            float fAbs = Math.abs((newFinal - start) / (oldFinal - start));
            int i = (int) (fAbs * 100.0f);
            if (i < 100) {
                float f = i / 100.0f;
                int i2 = i + 1;
                float[] fArr = SPLINE_TIME;
                float f2 = fArr[i];
                this.mDuration = (int) (this.mDuration * (f2 + (((fAbs - f) / ((i2 / 100.0f) - f)) * (fArr[i2] - f2))));
            }
        }

        void startScroll(int start, int distance, int duration) {
            startScroll(start, distance, duration, 0.0f);
        }

        void startScroll(int start, int distance, int duration, float velocity) {
            this.mFinished = false;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mFinal = start + distance;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = duration;
            SpringAnimation springAnimation = this.mSpring;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            if (this.mState == 3) {
                this.mSpring = new SpringAnimation(this, SPRING_PROPERTY);
                ResourceProvider resourceProviderProvider = DynamicResource.provider(this.mContext);
                this.mSpring.setSpring(new SpringForce(this.mFinal).setStiffness(resourceProviderProvider.getFloat(R.dimen.horizontal_spring_stiffness)).setDampingRatio(resourceProviderProvider.getFloat(R.dimen.horizontal_spring_damping_ratio)));
                this.mSpring.setStartVelocity(velocity);
                this.mSpring.animateToFinalPosition(this.mFinal);
                this.mSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.launcher3.util.-$$Lambda$OverScroller$SplineOverScroller$8JKUXfdErWsUUbsKAHKKsJXILdY
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        this.f$0.lambda$startScroll$0$OverScroller$SplineOverScroller(dynamicAnimation, z, f, f2);
                    }
                });
            }
            this.mDeceleration = 0.0f;
            this.mVelocity = 0;
        }

        public /* synthetic */ void lambda$startScroll$0$OverScroller$SplineOverScroller(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            this.mSpring = null;
            finish();
            this.mState = 0;
        }

        void finish() {
            SpringAnimation springAnimation = this.mSpring;
            if (springAnimation != null && springAnimation.isRunning()) {
                this.mSpring.cancel();
            }
            this.mCurrentPosition = this.mFinal;
            this.mFinished = true;
        }

        void setFinalPosition(int position) {
            SpringAnimation springAnimation;
            this.mFinal = position;
            if (this.mState == 3 && (springAnimation = this.mSpring) != null) {
                springAnimation.animateToFinalPosition(position);
            }
            this.mSplineDistance = this.mFinal - this.mStart;
            this.mFinished = false;
        }

        void extendDuration(int extend) {
            int iCurrentAnimationTimeMillis = ((int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime)) + extend;
            this.mSplineDuration = iCurrentAnimationTimeMillis;
            this.mDuration = iCurrentAnimationTimeMillis;
            this.mFinished = false;
        }

        boolean springback(int start, int min, int max) {
            this.mFinished = true;
            this.mFinal = start;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mVelocity = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 0;
            if (start < min) {
                startSpringback(start, min, 0);
            } else if (start > max) {
                startSpringback(start, max, 0);
            }
            return !this.mFinished;
        }

        private void startSpringback(int start, int end, int velocity) {
            this.mFinished = false;
            this.mState = 1;
            this.mStart = start;
            this.mCurrentPosition = start;
            this.mFinal = end;
            int i = start - end;
            this.mDeceleration = getDeceleration(i);
            this.mVelocity = -i;
            this.mOver = Math.abs(i);
            this.mDuration = (int) (Math.sqrt((((double) i) * (-2.0d)) / ((double) this.mDeceleration)) * 1000.0d);
        }

        void fling(int start, int velocity, int min, int max, int over) {
            this.mOver = over;
            this.mFinished = false;
            this.mVelocity = velocity;
            float f = velocity;
            this.mCurrVelocity = f;
            this.mSplineDuration = 0;
            this.mDuration = 0;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStart = start;
            this.mCurrentPosition = start;
            if (start > max || start < min) {
                startAfterEdge(start, min, max, velocity);
                return;
            }
            this.mState = 0;
            double splineFlingDistance = 0.0d;
            if (velocity != 0) {
                int splineFlingDuration = getSplineFlingDuration(velocity);
                this.mSplineDuration = splineFlingDuration;
                this.mDuration = splineFlingDuration;
                splineFlingDistance = getSplineFlingDistance(velocity);
            }
            int iSignum = (int) (splineFlingDistance * ((double) Math.signum(f)));
            this.mSplineDistance = iSignum;
            int i = start + iSignum;
            this.mFinal = i;
            if (i < min) {
                adjustDuration(this.mStart, i, min);
                this.mFinal = min;
            }
            int i2 = this.mFinal;
            if (i2 > max) {
                adjustDuration(this.mStart, i2, max);
                this.mFinal = max;
            }
        }

        private double getSplineDeceleration(int velocity) {
            return Math.log((Math.abs(velocity) * INFLEXION) / (this.mFlingFriction * this.mPhysicalCoeff));
        }

        private double getSplineFlingDistance(int velocity) {
            double splineDeceleration = getSplineDeceleration(velocity);
            float f = DECELERATION_RATE;
            return ((double) (this.mFlingFriction * this.mPhysicalCoeff)) * Math.exp((((double) f) / (((double) f) - 1.0d)) * splineDeceleration);
        }

        private int getSplineFlingDuration(int velocity) {
            return (int) (Math.exp(getSplineDeceleration(velocity) / (((double) DECELERATION_RATE) - 1.0d)) * 1000.0d);
        }

        private void fitOnBounceCurve(int start, int end, int velocity) {
            float f = this.mDeceleration;
            float f2 = (-velocity) / f;
            float f3 = velocity;
            float fSqrt = (float) Math.sqrt((((double) ((((f3 * f3) / 2.0f) / Math.abs(f)) + Math.abs(end - start))) * 2.0d) / ((double) Math.abs(this.mDeceleration)));
            this.mStartTime -= (long) ((int) ((fSqrt - f2) * 1000.0f));
            this.mStart = end;
            this.mCurrentPosition = end;
            this.mVelocity = (int) ((-this.mDeceleration) * fSqrt);
        }

        private void startBounceAfterEdge(int start, int end, int velocity) {
            this.mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
            fitOnBounceCurve(start, end, velocity);
            onEdgeReached();
        }

        private void startAfterEdge(int start, int min, int max, int velocity) {
            if (start > min && start < max) {
                Log.e("OverScroller", "startAfterEdge called from a valid position");
                this.mFinished = true;
                return;
            }
            boolean z = start > max;
            int i = z ? max : min;
            if ((start - i) * velocity >= 0) {
                startBounceAfterEdge(start, i, velocity);
            } else if (getSplineFlingDistance(velocity) > Math.abs(r4)) {
                fling(start, velocity, z ? min : start, z ? start : max, this.mOver);
            } else {
                startSpringback(start, i, velocity);
            }
        }

        void notifyEdgeReached(int start, int end, int over) {
            if (this.mState == 0) {
                this.mOver = over;
                this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                startAfterEdge(start, end, end, (int) this.mCurrVelocity);
            }
        }

        private void onEdgeReached() {
            int i = this.mVelocity;
            float f = i * i;
            float fAbs = f / (Math.abs(this.mDeceleration) * 2.0f);
            float fSignum = Math.signum(this.mVelocity);
            int i2 = this.mOver;
            if (fAbs > i2) {
                this.mDeceleration = ((-fSignum) * f) / (i2 * 2.0f);
                fAbs = i2;
            }
            this.mOver = (int) fAbs;
            this.mState = 2;
            int i3 = this.mStart;
            int i4 = this.mVelocity;
            if (i4 <= 0) {
                fAbs = -fAbs;
            }
            this.mFinal = i3 + ((int) fAbs);
            this.mDuration = -((int) ((i4 * 1000.0f) / this.mDeceleration));
        }

        boolean continueWhenFinished() {
            int i = this.mState;
            if (i != 0) {
                if (i == 1) {
                    return false;
                }
                if (i == 2) {
                    this.mStartTime += (long) this.mDuration;
                    startSpringback(this.mFinal, this.mStart, 0);
                }
            } else {
                if (this.mDuration >= this.mSplineDuration) {
                    return false;
                }
                int i2 = this.mFinal;
                this.mStart = i2;
                this.mCurrentPosition = i2;
                int i3 = (int) this.mCurrVelocity;
                this.mVelocity = i3;
                this.mDeceleration = getDeceleration(i3);
                this.mStartTime += (long) this.mDuration;
                onEdgeReached();
            }
            update();
            return true;
        }

        boolean update() {
            if (this.mState == 3) {
                return this.mFinished;
            }
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
            if (jCurrentAnimationTimeMillis == 0) {
                return this.mDuration > 0;
            }
            int i = this.mDuration;
            if (jCurrentAnimationTimeMillis > i) {
                return false;
            }
            double d = 0.0d;
            int i2 = this.mState;
            if (i2 == 0) {
                int i3 = this.mSplineDuration;
                float f = jCurrentAnimationTimeMillis / i3;
                int i4 = (int) (f * 100.0f);
                float f2 = 1.0f;
                float f3 = 0.0f;
                if (i4 < 100) {
                    float f4 = i4 / 100.0f;
                    int i5 = i4 + 1;
                    float[] fArr = SPLINE_POSITION;
                    float f5 = fArr[i4];
                    f3 = (fArr[i5] - f5) / ((i5 / 100.0f) - f4);
                    f2 = f5 + ((f - f4) * f3);
                }
                int i6 = this.mSplineDistance;
                this.mCurrVelocity = ((f3 * i6) / i3) * 1000.0f;
                d = f2 * i6;
            } else if (i2 == 1) {
                float f6 = jCurrentAnimationTimeMillis / i;
                float f7 = f6 * f6;
                float fSignum = Math.signum(this.mVelocity);
                int i7 = this.mOver;
                d = i7 * fSignum * ((3.0f * f7) - ((2.0f * f6) * f7));
                this.mCurrVelocity = fSignum * i7 * 6.0f * ((-f6) + f7);
            } else if (i2 == 2) {
                float f8 = jCurrentAnimationTimeMillis / 1000.0f;
                int i8 = this.mVelocity;
                float f9 = this.mDeceleration;
                this.mCurrVelocity = i8 + (f9 * f8);
                d = (i8 * f8) + (((f9 * f8) * f8) / 2.0f);
            }
            this.mCurrentPosition = this.mStart + ((int) Math.round(d));
            return true;
        }
    }
}
