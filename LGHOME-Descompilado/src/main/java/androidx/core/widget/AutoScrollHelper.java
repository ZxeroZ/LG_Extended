package androidx.core.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes.dex */
public abstract class AutoScrollHelper implements View.OnTouchListener {
    private static final int DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    private static final int DEFAULT_EDGE_TYPE = 1;
    private static final float DEFAULT_MAXIMUM_EDGE = Float.MAX_VALUE;
    private static final int DEFAULT_MAXIMUM_VELOCITY_DIPS = 1575;
    private static final int DEFAULT_MINIMUM_VELOCITY_DIPS = 315;
    private static final int DEFAULT_RAMP_DOWN_DURATION = 500;
    private static final int DEFAULT_RAMP_UP_DURATION = 500;
    private static final float DEFAULT_RELATIVE_EDGE = 0.2f;
    private static final float DEFAULT_RELATIVE_VELOCITY = 1.0f;
    public static final int EDGE_TYPE_INSIDE = 0;
    public static final int EDGE_TYPE_INSIDE_EXTEND = 1;
    public static final int EDGE_TYPE_OUTSIDE = 2;
    private static final int HORIZONTAL = 0;
    public static final float NO_MAX = Float.MAX_VALUE;
    public static final float NO_MIN = 0.0f;
    public static final float RELATIVE_UNSPECIFIED = 0.0f;
    private static final int VERTICAL = 1;
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    boolean mAnimating;
    private int mEdgeType;
    private boolean mEnabled;
    private boolean mExclusive;
    boolean mNeedsCancel;
    boolean mNeedsReset;
    private Runnable mRunnable;
    final View mTarget;
    final ClampedScroller mScroller = new ClampedScroller();
    private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
    private float[] mRelativeEdges = {0.0f, 0.0f};
    private float[] mMaximumEdges = {Float.MAX_VALUE, Float.MAX_VALUE};
    private float[] mRelativeVelocity = {0.0f, 0.0f};
    private float[] mMinimumVelocity = {0.0f, 0.0f};
    private float[] mMaximumVelocity = {Float.MAX_VALUE, Float.MAX_VALUE};

    static float constrain(float value, float min, float max) {
        return value > max ? max : value < min ? min : value;
    }

    static int constrain(int value, int min, int max) {
        return value > max ? max : value < min ? min : value;
    }

    public abstract boolean canTargetScrollHorizontally(int direction);

    public abstract boolean canTargetScrollVertically(int direction);

    public abstract void scrollTargetBy(int deltaX, int deltaY);

    public AutoScrollHelper(View target) {
        this.mTarget = target;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int i = (int) ((displayMetrics.density * 1575.0f) + 0.5f);
        int i2 = (int) ((displayMetrics.density * 315.0f) + 0.5f);
        float f = i;
        setMaximumVelocity(f, f);
        float f2 = i2;
        setMinimumVelocity(f2, f2);
        setEdgeType(1);
        setMaximumEdges(Float.MAX_VALUE, Float.MAX_VALUE);
        setRelativeEdges(0.2f, 0.2f);
        setRelativeVelocity(1.0f, 1.0f);
        setActivationDelay(DEFAULT_ACTIVATION_DELAY);
        setRampUpDuration(500);
        setRampDownDuration(500);
    }

    public AutoScrollHelper setEnabled(boolean enabled) {
        if (this.mEnabled && !enabled) {
            requestStop();
        }
        this.mEnabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public AutoScrollHelper setExclusive(boolean exclusive) {
        this.mExclusive = exclusive;
        return this;
    }

    public boolean isExclusive() {
        return this.mExclusive;
    }

    public AutoScrollHelper setMaximumVelocity(float horizontalMax, float verticalMax) {
        float[] fArr = this.mMaximumVelocity;
        fArr[0] = horizontalMax / 1000.0f;
        fArr[1] = verticalMax / 1000.0f;
        return this;
    }

    public AutoScrollHelper setMinimumVelocity(float horizontalMin, float verticalMin) {
        float[] fArr = this.mMinimumVelocity;
        fArr[0] = horizontalMin / 1000.0f;
        fArr[1] = verticalMin / 1000.0f;
        return this;
    }

    public AutoScrollHelper setRelativeVelocity(float horizontal, float vertical) {
        float[] fArr = this.mRelativeVelocity;
        fArr[0] = horizontal / 1000.0f;
        fArr[1] = vertical / 1000.0f;
        return this;
    }

    public AutoScrollHelper setEdgeType(int type) {
        this.mEdgeType = type;
        return this;
    }

    public AutoScrollHelper setRelativeEdges(float horizontal, float vertical) {
        float[] fArr = this.mRelativeEdges;
        fArr[0] = horizontal;
        fArr[1] = vertical;
        return this;
    }

    public AutoScrollHelper setMaximumEdges(float horizontalMax, float verticalMax) {
        float[] fArr = this.mMaximumEdges;
        fArr[0] = horizontalMax;
        fArr[1] = verticalMax;
        return this;
    }

    public AutoScrollHelper setActivationDelay(int delayMillis) {
        this.mActivationDelay = delayMillis;
        return this;
    }

    public AutoScrollHelper setRampUpDuration(int durationMillis) {
        this.mScroller.setRampUpDuration(durationMillis);
        return this;
    }

    public AutoScrollHelper setRampDownDuration(int durationMillis) {
        this.mScroller.setRampDownDuration(durationMillis);
        return this;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0016  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r6, android.view.MotionEvent r7) {
        /*
            r5 = this;
            boolean r0 = r5.mEnabled
            r1 = 0
            if (r0 != 0) goto L6
            return r1
        L6:
            int r0 = r7.getActionMasked()
            r2 = 1
            if (r0 == 0) goto L1a
            if (r0 == r2) goto L16
            r3 = 2
            if (r0 == r3) goto L1e
            r6 = 3
            if (r0 == r6) goto L16
            goto L58
        L16:
            r5.requestStop()
            goto L58
        L1a:
            r5.mNeedsCancel = r2
            r5.mAlreadyDelayed = r1
        L1e:
            float r0 = r7.getX()
            int r3 = r6.getWidth()
            float r3 = (float) r3
            android.view.View r4 = r5.mTarget
            int r4 = r4.getWidth()
            float r4 = (float) r4
            float r0 = r5.computeTargetVelocity(r1, r0, r3, r4)
            float r7 = r7.getY()
            int r6 = r6.getHeight()
            float r6 = (float) r6
            android.view.View r3 = r5.mTarget
            int r3 = r3.getHeight()
            float r3 = (float) r3
            float r6 = r5.computeTargetVelocity(r2, r7, r6, r3)
            androidx.core.widget.AutoScrollHelper$ClampedScroller r7 = r5.mScroller
            r7.setTargetVelocity(r0, r6)
            boolean r6 = r5.mAnimating
            if (r6 != 0) goto L58
            boolean r6 = r5.shouldAnimate()
            if (r6 == 0) goto L58
            r5.startAnimating()
        L58:
            boolean r6 = r5.mExclusive
            if (r6 == 0) goto L61
            boolean r6 = r5.mAnimating
            if (r6 == 0) goto L61
            r1 = r2
        L61:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.widget.AutoScrollHelper.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    boolean shouldAnimate() {
        ClampedScroller clampedScroller = this.mScroller;
        int verticalDirection = clampedScroller.getVerticalDirection();
        int horizontalDirection = clampedScroller.getHorizontalDirection();
        return (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) || (horizontalDirection != 0 && canTargetScrollHorizontally(horizontalDirection));
    }

    private void startAnimating() {
        int i;
        if (this.mRunnable == null) {
            this.mRunnable = new ScrollAnimationRunnable();
        }
        this.mAnimating = true;
        this.mNeedsReset = true;
        if (!this.mAlreadyDelayed && (i = this.mActivationDelay) > 0) {
            ViewCompat.postOnAnimationDelayed(this.mTarget, this.mRunnable, i);
        } else {
            this.mRunnable.run();
        }
        this.mAlreadyDelayed = true;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
        } else {
            this.mScroller.requestStop();
        }
    }

    private float computeTargetVelocity(int direction, float coordinate, float srcSize, float dstSize) {
        float edgeValue = getEdgeValue(this.mRelativeEdges[direction], srcSize, this.mMaximumEdges[direction], coordinate);
        if (edgeValue == 0.0f) {
            return 0.0f;
        }
        float f = this.mRelativeVelocity[direction];
        float f2 = this.mMinimumVelocity[direction];
        float f3 = this.mMaximumVelocity[direction];
        float f4 = f * dstSize;
        if (edgeValue > 0.0f) {
            return constrain(edgeValue * f4, f2, f3);
        }
        return -constrain((-edgeValue) * f4, f2, f3);
    }

    private float getEdgeValue(float relativeValue, float size, float maxValue, float current) {
        float interpolation;
        float fConstrain = constrain(relativeValue * size, 0.0f, maxValue);
        float fConstrainEdgeValue = constrainEdgeValue(size - current, fConstrain) - constrainEdgeValue(current, fConstrain);
        if (fConstrainEdgeValue < 0.0f) {
            interpolation = -this.mEdgeInterpolator.getInterpolation(-fConstrainEdgeValue);
        } else {
            if (fConstrainEdgeValue <= 0.0f) {
                return 0.0f;
            }
            interpolation = this.mEdgeInterpolator.getInterpolation(fConstrainEdgeValue);
        }
        return constrain(interpolation, -1.0f, 1.0f);
    }

    private float constrainEdgeValue(float current, float leading) {
        if (leading == 0.0f) {
            return 0.0f;
        }
        int i = this.mEdgeType;
        if (i == 0 || i == 1) {
            if (current < leading) {
                if (current >= 0.0f) {
                    return 1.0f - (current / leading);
                }
                if (this.mAnimating && i == 1) {
                    return 1.0f;
                }
            }
        } else if (i == 2 && current < 0.0f) {
            return current / (-leading);
        }
        return 0.0f;
    }

    void cancelTargetTouch() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
        this.mTarget.onTouchEvent(motionEventObtain);
        motionEventObtain.recycle();
    }

    private class ScrollAnimationRunnable implements Runnable {
        ScrollAnimationRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AutoScrollHelper.this.mAnimating) {
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.this.mNeedsReset = false;
                    AutoScrollHelper.this.mScroller.start();
                }
                ClampedScroller clampedScroller = AutoScrollHelper.this.mScroller;
                if (clampedScroller.isFinished() || !AutoScrollHelper.this.shouldAnimate()) {
                    AutoScrollHelper.this.mAnimating = false;
                    return;
                }
                if (AutoScrollHelper.this.mNeedsCancel) {
                    AutoScrollHelper.this.mNeedsCancel = false;
                    AutoScrollHelper.this.cancelTargetTouch();
                }
                clampedScroller.computeScrollDelta();
                AutoScrollHelper.this.scrollTargetBy(clampedScroller.getDeltaX(), clampedScroller.getDeltaY());
                ViewCompat.postOnAnimation(AutoScrollHelper.this.mTarget, this);
            }
        }
    }

    private static class ClampedScroller {
        private int mEffectiveRampDown;
        private int mRampDownDuration;
        private int mRampUpDuration;
        private float mStopValue;
        private float mTargetVelocityX;
        private float mTargetVelocityY;
        private long mStartTime = Long.MIN_VALUE;
        private long mStopTime = -1;
        private long mDeltaTime = 0;
        private int mDeltaX = 0;
        private int mDeltaY = 0;

        private float interpolateValue(float value) {
            return ((-4.0f) * value * value) + (value * 4.0f);
        }

        ClampedScroller() {
        }

        public void setRampUpDuration(int durationMillis) {
            this.mRampUpDuration = durationMillis;
        }

        public void setRampDownDuration(int durationMillis) {
            this.mRampDownDuration = durationMillis;
        }

        public void start() {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.mStartTime = jCurrentAnimationTimeMillis;
            this.mStopTime = -1L;
            this.mDeltaTime = jCurrentAnimationTimeMillis;
            this.mStopValue = 0.5f;
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }

        public void requestStop() {
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.mEffectiveRampDown = AutoScrollHelper.constrain((int) (jCurrentAnimationTimeMillis - this.mStartTime), 0, this.mRampDownDuration);
            this.mStopValue = getValueAt(jCurrentAnimationTimeMillis);
            this.mStopTime = jCurrentAnimationTimeMillis;
        }

        public boolean isFinished() {
            return this.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > this.mStopTime + ((long) this.mEffectiveRampDown);
        }

        private float getValueAt(long currentTime) {
            if (currentTime < this.mStartTime) {
                return 0.0f;
            }
            long j = this.mStopTime;
            if (j < 0 || currentTime < j) {
                return AutoScrollHelper.constrain((currentTime - r0) / this.mRampUpDuration, 0.0f, 1.0f) * 0.5f;
            }
            float f = this.mStopValue;
            return (1.0f - f) + (f * AutoScrollHelper.constrain((currentTime - j) / this.mEffectiveRampDown, 0.0f, 1.0f));
        }

        public void computeScrollDelta() {
            if (this.mDeltaTime == 0) {
                throw new RuntimeException("Cannot compute scroll delta before calling start()");
            }
            long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            float fInterpolateValue = interpolateValue(getValueAt(jCurrentAnimationTimeMillis));
            long j = jCurrentAnimationTimeMillis - this.mDeltaTime;
            this.mDeltaTime = jCurrentAnimationTimeMillis;
            float f = j * fInterpolateValue;
            this.mDeltaX = (int) (this.mTargetVelocityX * f);
            this.mDeltaY = (int) (f * this.mTargetVelocityY);
        }

        public void setTargetVelocity(float x, float y) {
            this.mTargetVelocityX = x;
            this.mTargetVelocityY = y;
        }

        public int getHorizontalDirection() {
            float f = this.mTargetVelocityX;
            return (int) (f / Math.abs(f));
        }

        public int getVerticalDirection() {
            float f = this.mTargetVelocityY;
            return (int) (f / Math.abs(f));
        }

        public int getDeltaX() {
            return this.mDeltaX;
        }

        public int getDeltaY() {
            return this.mDeltaY;
        }
    }
}
