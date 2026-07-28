package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class SwipeDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_NEGATIVE = 2;
    public static final int DIRECTION_POSITIVE = 1;
    public static final float RELEASE_VELOCITY_PX_MS = 1.0f;
    public static final float SCROLL_VELOCITY_DAMPENING_RC = 15.915494f;
    private static final String TAG = "SwipeDetector";
    protected int mActivePointerId;
    private long mCurrentMillis;
    private final Direction mDir;
    private float mDisplacement;
    private final PointF mDownPos;
    private boolean mIgnoreSlopWhenSettling;
    private boolean mIsOutOfTouchSlop;
    private final boolean mIsRtl;
    private float mLastDisplacement;
    private final PointF mLastPos;
    private final Listener mListener;
    private final float mMaxVelocity;
    private int mScrollConditions;
    private ScrollState mState;
    private float mSubtractDisplacement;
    private final float mTouchSlop;
    private float mVelocity;
    private VelocityTracker mVelocityTracker;
    public static final Direction VERTICAL = new Direction() { // from class: com.android.launcher3.touch.SwipeDetector.1
        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        boolean isNegative(float displacement) {
            return displacement > 0.0f;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        boolean isPositive(float displacement) {
            return displacement < 0.0f;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint, boolean isRtl) {
            return ev.getY(pointerIndex) - refPoint.y;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getX(pointerIndex) - downPos.x);
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getVelocity(VelocityTracker tracker, boolean isRtl) {
            return tracker.getYVelocity();
        }
    };
    public static final Direction HORIZONTAL = new Direction() { // from class: com.android.launcher3.touch.SwipeDetector.2
        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        boolean isNegative(float displacement) {
            return displacement < 0.0f;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        boolean isPositive(float displacement) {
            return displacement > 0.0f;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint, boolean isRtl) {
            float x = ev.getX(pointerIndex) - refPoint.x;
            return isRtl ? -x : x;
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getY(pointerIndex) - downPos.y);
        }

        @Override // com.android.launcher3.touch.SwipeDetector.Direction
        float getVelocity(VelocityTracker tracker, boolean isRtl) {
            float xVelocity = tracker.getXVelocity();
            return isRtl ? -xVelocity : xVelocity;
        }
    };

    public static abstract class Direction {
        abstract float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos);

        abstract float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint, boolean isRtl);

        abstract float getVelocity(VelocityTracker tracker, boolean isRtl);

        abstract boolean isNegative(float displacement);

        abstract boolean isPositive(float displacement);
    }

    enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    private static float computeDampeningFactor(float deltaTime) {
        return deltaTime / (15.915494f + deltaTime);
    }

    public static float interpolate(float from, float to, float alpha) {
        return ((1.0f - alpha) * from) + (alpha * to);
    }

    private void setState(ScrollState newState) {
        if (newState == ScrollState.DRAGGING) {
            initializeDragging();
            if (this.mState == ScrollState.IDLE) {
                reportDragStart(false);
            } else if (this.mState == ScrollState.SETTLING) {
                reportDragStart(true);
            }
        }
        if (newState == ScrollState.SETTLING) {
            reportDragEnd();
        }
        this.mState = newState;
    }

    public boolean isDraggingOrSettling() {
        return this.mState == ScrollState.DRAGGING || this.mState == ScrollState.SETTLING;
    }

    public int getDownX() {
        return (int) this.mDownPos.x;
    }

    public int getDownY() {
        return (int) this.mDownPos.y;
    }

    public boolean isIdleState() {
        return this.mState == ScrollState.IDLE;
    }

    public boolean isSettlingState() {
        return this.mState == ScrollState.SETTLING;
    }

    public boolean isDraggingState() {
        return this.mState == ScrollState.DRAGGING;
    }

    public interface Listener {
        boolean onDrag(float displacement);

        void onDragEnd(float velocity, boolean fling);

        void onDragStart(boolean start);

        default boolean onDrag(float displacement, MotionEvent event) {
            return onDrag(displacement);
        }
    }

    public SwipeDetector(Context context, Listener l, Direction dir) {
        this(ViewConfiguration.get(context), l, dir, Utilities.isRtl(context.getResources()));
    }

    protected SwipeDetector(ViewConfiguration config, Listener l, Direction dir, boolean isRtl) {
        this.mActivePointerId = -1;
        this.mState = ScrollState.IDLE;
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mListener = l;
        this.mDir = dir;
        this.mIsRtl = isRtl;
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMaxVelocity = config.getScaledMaximumFlingVelocity();
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollConditions = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    public int getScrollDirections() {
        return this.mScrollConditions;
    }

    private boolean shouldScrollStart(MotionEvent ev, int pointerIndex) {
        if (Math.max(this.mDir.getActiveTouchSlop(ev, pointerIndex, this.mDownPos), this.mTouchSlop) <= Math.abs(this.mDisplacement) && !this.mIsOutOfTouchSlop) {
            return ((this.mScrollConditions & 2) > 0 && this.mDir.isNegative(this.mDisplacement)) || ((this.mScrollConditions & 1) > 0 && this.mDir.isPositive(this.mDisplacement));
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x00ea  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getActionMasked()
            if (r0 != 0) goto Ld
            android.view.VelocityTracker r1 = r7.mVelocityTracker
            if (r1 == 0) goto Ld
            r1.clear()
        Ld:
            android.view.VelocityTracker r1 = r7.mVelocityTracker
            if (r1 != 0) goto L17
            android.view.VelocityTracker r1 = android.view.VelocityTracker.obtain()
            r7.mVelocityTracker = r1
        L17:
            android.view.VelocityTracker r1 = r7.mVelocityTracker
            r1.addMovement(r8)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L100
            if (r0 == r2) goto Lea
            r3 = 2
            if (r0 == r3) goto L72
            r3 = 3
            if (r0 == r3) goto Lea
            r3 = 6
            if (r0 == r3) goto L2d
            goto L132
        L2d:
            int r0 = r8.getActionIndex()
            int r3 = r8.getPointerId(r0)
            int r4 = r7.mActivePointerId
            if (r3 != r4) goto L132
            if (r0 != 0) goto L3c
            r1 = r2
        L3c:
            android.graphics.PointF r0 = r7.mDownPos
            float r3 = r8.getX(r1)
            android.graphics.PointF r4 = r7.mLastPos
            float r4 = r4.x
            android.graphics.PointF r5 = r7.mDownPos
            float r5 = r5.x
            float r4 = r4 - r5
            float r3 = r3 - r4
            float r4 = r8.getY(r1)
            android.graphics.PointF r5 = r7.mLastPos
            float r5 = r5.y
            android.graphics.PointF r6 = r7.mDownPos
            float r6 = r6.y
            float r5 = r5 - r6
            float r4 = r4 - r5
            r0.set(r3, r4)
            android.graphics.PointF r0 = r7.mLastPos
            float r3 = r8.getX(r1)
            float r4 = r8.getY(r1)
            r0.set(r3, r4)
            int r8 = r8.getPointerId(r1)
            r7.mActivePointerId = r8
            goto L132
        L72:
            int r0 = r7.mActivePointerId
            int r0 = r8.findPointerIndex(r0)
            r3 = -1
            if (r0 != r3) goto L7d
            goto L132
        L7d:
            com.android.launcher3.touch.SwipeDetector$Direction r3 = r7.mDir
            android.graphics.PointF r4 = r7.mDownPos
            boolean r5 = r7.mIsRtl
            float r3 = r3.getDisplacement(r8, r0, r4, r5)
            r7.mDisplacement = r3
            com.android.launcher3.touch.SwipeDetector$ScrollState r3 = r7.mState
            com.android.launcher3.touch.SwipeDetector$ScrollState r4 = com.android.launcher3.touch.SwipeDetector.ScrollState.DRAGGING
            if (r3 == r4) goto L9a
            boolean r3 = r7.shouldScrollStart(r8, r0)
            if (r3 == 0) goto L9a
            com.android.launcher3.touch.SwipeDetector$ScrollState r3 = com.android.launcher3.touch.SwipeDetector.ScrollState.DRAGGING
            r7.setState(r3)
        L9a:
            com.android.launcher3.touch.SwipeDetector$ScrollState r3 = r7.mState
            com.android.launcher3.touch.SwipeDetector$ScrollState r4 = com.android.launcher3.touch.SwipeDetector.ScrollState.DRAGGING
            if (r3 != r4) goto La3
            r7.reportDragging(r8)
        La3:
            android.graphics.PointF r3 = r7.mLastPos
            float r4 = r8.getX(r0)
            float r0 = r8.getY(r0)
            r3.set(r4, r0)
            float r0 = r8.getX()
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.x
            float r0 = r0 - r3
            float r0 = java.lang.Math.abs(r0)
            float r8 = r8.getY()
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.y
            float r8 = r8 - r3
            float r8 = java.lang.Math.abs(r8)
            float r3 = r7.mTouchSlop
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto Ld2
            r0 = r2
            goto Ld3
        Ld2:
            r0 = r1
        Ld3:
            int r8 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r8 <= 0) goto Ld8
            r1 = r2
        Ld8:
            boolean r8 = r7.mIsOutOfTouchSlop
            if (r8 != 0) goto L132
            if (r0 != 0) goto Le0
            if (r1 == 0) goto L132
        Le0:
            java.lang.String r8 = "SwipeDetector"
            java.lang.String r0 = "onTouchEvent() : mIsOutOfTouchSlop = true"
            com.lge.launcher3.util.LGLog.i(r8, r0)
            r7.mIsOutOfTouchSlop = r2
            goto L132
        Lea:
            com.android.launcher3.touch.SwipeDetector$ScrollState r8 = r7.mState
            com.android.launcher3.touch.SwipeDetector$ScrollState r0 = com.android.launcher3.touch.SwipeDetector.ScrollState.DRAGGING
            if (r8 != r0) goto Lf5
            com.android.launcher3.touch.SwipeDetector$ScrollState r8 = com.android.launcher3.touch.SwipeDetector.ScrollState.SETTLING
            r7.setState(r8)
        Lf5:
            android.view.VelocityTracker r8 = r7.mVelocityTracker
            r8.recycle()
            r8 = 0
            r7.mVelocityTracker = r8
            r7.mIsOutOfTouchSlop = r1
            goto L132
        L100:
            int r0 = r8.getPointerId(r1)
            r7.mActivePointerId = r0
            android.graphics.PointF r0 = r7.mDownPos
            float r3 = r8.getX()
            float r8 = r8.getY()
            r0.set(r3, r8)
            android.graphics.PointF r8 = r7.mLastPos
            android.graphics.PointF r0 = r7.mDownPos
            r8.set(r0)
            r8 = 0
            r7.mLastDisplacement = r8
            r7.mDisplacement = r8
            r7.mVelocity = r8
            r7.mIsOutOfTouchSlop = r1
            com.android.launcher3.touch.SwipeDetector$ScrollState r8 = r7.mState
            com.android.launcher3.touch.SwipeDetector$ScrollState r0 = com.android.launcher3.touch.SwipeDetector.ScrollState.SETTLING
            if (r8 != r0) goto L132
            boolean r8 = r7.mIgnoreSlopWhenSettling
            if (r8 == 0) goto L132
            com.android.launcher3.touch.SwipeDetector$ScrollState r8 = com.android.launcher3.touch.SwipeDetector.ScrollState.DRAGGING
            r7.setState(r8)
        L132:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.touch.SwipeDetector.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void finishedScrolling() {
        setState(ScrollState.IDLE);
    }

    private boolean reportDragStart(boolean recatch) {
        this.mListener.onDragStart(!recatch);
        return true;
    }

    private void initializeDragging() {
        if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
            this.mSubtractDisplacement = 0.0f;
        }
        if (this.mDisplacement > 0.0f) {
            this.mSubtractDisplacement = this.mTouchSlop;
        } else {
            this.mSubtractDisplacement = -this.mTouchSlop;
        }
    }

    public boolean wasInitialTouchPositive() {
        return this.mDir.isPositive(this.mSubtractDisplacement);
    }

    private boolean reportDragging(MotionEvent event) {
        float f = this.mDisplacement;
        if (f == this.mLastDisplacement) {
            return true;
        }
        this.mLastDisplacement = f;
        return this.mListener.onDrag(f - this.mSubtractDisplacement, event);
    }

    private void reportDragEnd() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        float velocity = this.mDir.getVelocity(this.mVelocityTracker, this.mIsRtl) / 1000.0f;
        this.mListener.onDragEnd(velocity, Math.abs(velocity) > 1.0f);
    }

    public float computeVelocity(float delta, long currentMillis) {
        long j = this.mCurrentMillis;
        this.mCurrentMillis = currentMillis;
        float f = currentMillis - j;
        float f2 = f > 0.0f ? delta / f : 0.0f;
        if (Math.abs(this.mVelocity) < 0.001f) {
            this.mVelocity = f2;
        } else {
            this.mVelocity = interpolate(this.mVelocity, f2, computeDampeningFactor(f));
        }
        return this.mVelocity;
    }

    public static long calculateDuration(float velocity, float progressNeeded) {
        float fMax = Math.max(2.0f, Math.abs(velocity * 0.5f));
        return (long) Math.max(100.0f, (ANIMATION_DURATION / fMax) * Math.max(0.2f, progressNeeded));
    }
}
