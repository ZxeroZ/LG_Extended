package com.android.launcher3.touch;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseSwipeDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    private static final float RELEASE_VELOCITY_PX_MS = 1.0f;
    private static final String TAG = "BaseSwipeDetector";
    private static final PointF sTempPoint = new PointF();
    protected boolean mIgnoreSlopWhenSettling;
    protected final boolean mIsRtl;
    private boolean mIsSettingState;
    protected final float mMaxVelocity;
    protected final float mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private final PointF mDownPos = new PointF();
    private final PointF mLastPos = new PointF();
    private final Queue<Runnable> mSetStateQueue = new LinkedList();
    private int mActivePointerId = -1;
    private PointF mLastDisplacement = new PointF();
    private PointF mDisplacement = new PointF();
    protected PointF mSubtractDisplacement = new PointF();
    ScrollState mState = ScrollState.IDLE;

    /* JADX INFO: Access modifiers changed from: private */
    enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    protected abstract void reportDragEndInternal(PointF velocity);

    protected abstract void reportDragStartInternal(boolean recatch);

    protected abstract void reportDraggingInternal(PointF displacement, MotionEvent event);

    protected abstract boolean shouldScrollStart(PointF displacement);

    protected BaseSwipeDetector(ViewConfiguration config, boolean isRtl) {
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMaxVelocity = config.getScaledMaximumFlingVelocity();
        this.mIsRtl = isRtl;
    }

    public static long calculateDuration(float velocity, float progressNeeded) {
        float fMax = Math.max(2.0f, Math.abs(velocity * 0.5f));
        return (long) Math.max(100.0f, (ANIMATION_DURATION / fMax) * Math.max(0.2f, progressNeeded));
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

    public boolean isDraggingOrSettling() {
        return this.mState == ScrollState.DRAGGING || this.mState == ScrollState.SETTLING;
    }

    public void finishedScrolling() {
        lambda$setState$0$BaseSwipeDetector(ScrollState.IDLE);
    }

    public boolean isFling(float velocity) {
        return Math.abs(velocity) > 1.0f;
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x00d4  */
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
            if (r0 == 0) goto Le8
            if (r0 == r2) goto Ld4
            r3 = 2
            if (r0 == r3) goto L72
            r3 = 3
            if (r0 == r3) goto Ld4
            r3 = 6
            if (r0 == r3) goto L2d
            goto L11c
        L2d:
            int r0 = r8.getActionIndex()
            int r3 = r8.getPointerId(r0)
            int r4 = r7.mActivePointerId
            if (r3 != r4) goto L11c
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
            goto L11c
        L72:
            int r0 = r7.mActivePointerId
            int r0 = r8.findPointerIndex(r0)
            r1 = -1
            if (r0 != r1) goto L7d
            goto L11c
        L7d:
            android.graphics.PointF r1 = r7.mDisplacement
            float r3 = r8.getX(r0)
            android.graphics.PointF r4 = r7.mDownPos
            float r4 = r4.x
            float r3 = r3 - r4
            float r4 = r8.getY(r0)
            android.graphics.PointF r5 = r7.mDownPos
            float r5 = r5.y
            float r4 = r4 - r5
            r1.set(r3, r4)
            boolean r1 = r7.mIsRtl
            if (r1 == 0) goto L9f
            android.graphics.PointF r1 = r7.mDisplacement
            float r3 = r1.x
            float r3 = -r3
            r1.x = r3
        L9f:
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r1 = r7.mState
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r3 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.DRAGGING
            if (r1 == r3) goto Lb2
            android.graphics.PointF r1 = r7.mDisplacement
            boolean r1 = r7.shouldScrollStart(r1)
            if (r1 == 0) goto Lb2
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r1 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.DRAGGING
            r7.lambda$setState$0$BaseSwipeDetector(r1)
        Lb2:
            boolean r1 = com.android.launcher3.testing.TestProtocol.sDebugTracing
            if (r1 == 0) goto Lbd
            java.lang.String r1 = "b/139891609"
            java.lang.String r3 = "before report dragging"
            android.util.Log.d(r1, r3)
        Lbd:
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r1 = r7.mState
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r3 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.DRAGGING
            if (r1 != r3) goto Lc6
            r7.reportDragging(r8)
        Lc6:
            android.graphics.PointF r1 = r7.mLastPos
            float r3 = r8.getX(r0)
            float r8 = r8.getY(r0)
            r1.set(r3, r8)
            goto L11c
        Ld4:
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r8 = r7.mState
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r0 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.DRAGGING
            if (r8 != r0) goto Ldf
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r8 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.SETTLING
            r7.lambda$setState$0$BaseSwipeDetector(r8)
        Ldf:
            android.view.VelocityTracker r8 = r7.mVelocityTracker
            r8.recycle()
            r8 = 0
            r7.mVelocityTracker = r8
            goto L11c
        Le8:
            int r0 = r8.getPointerId(r1)
            r7.mActivePointerId = r0
            android.graphics.PointF r0 = r7.mDownPos
            float r1 = r8.getX()
            float r8 = r8.getY()
            r0.set(r1, r8)
            android.graphics.PointF r8 = r7.mLastPos
            android.graphics.PointF r0 = r7.mDownPos
            r8.set(r0)
            android.graphics.PointF r8 = r7.mLastDisplacement
            r0 = 0
            r8.set(r0, r0)
            android.graphics.PointF r8 = r7.mDisplacement
            r8.set(r0, r0)
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r8 = r7.mState
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r0 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.SETTLING
            if (r8 != r0) goto L11c
            boolean r8 = r7.mIgnoreSlopWhenSettling
            if (r8 == 0) goto L11c
            com.android.launcher3.touch.BaseSwipeDetector$ScrollState r8 = com.android.launcher3.touch.BaseSwipeDetector.ScrollState.DRAGGING
            r7.lambda$setState$0$BaseSwipeDetector(r8)
        L11c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.touch.BaseSwipeDetector.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$setState$0$BaseSwipeDetector(Lcom/android/launcher3/touch/BaseSwipeDetector$ScrollState;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: setState, reason: merged with bridge method [inline-methods] */
    public void lambda$setState$0$BaseSwipeDetector(final ScrollState newState) {
        if (this.mIsSettingState) {
            this.mSetStateQueue.add(new Runnable() { // from class: com.android.launcher3.touch.-$$Lambda$BaseSwipeDetector$c4IQ0Apvt3h2wv2zDNZzyEgGEK8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setState$0$BaseSwipeDetector(newState);
                }
            });
            return;
        }
        this.mIsSettingState = true;
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
        this.mIsSettingState = false;
        if (this.mSetStateQueue.isEmpty()) {
            return;
        }
        this.mSetStateQueue.remove().run();
    }

    private void initializeDragging() {
        if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
            this.mSubtractDisplacement.set(0.0f, 0.0f);
            return;
        }
        this.mSubtractDisplacement.x = this.mDisplacement.x > 0.0f ? this.mTouchSlop : -this.mTouchSlop;
        this.mSubtractDisplacement.y = this.mDisplacement.y > 0.0f ? this.mTouchSlop : -this.mTouchSlop;
    }

    private void reportDragStart(boolean recatch) {
        reportDragStartInternal(recatch);
    }

    private void reportDragging(MotionEvent event) {
        PointF pointF = this.mDisplacement;
        PointF pointF2 = this.mLastDisplacement;
        if (pointF != pointF2) {
            pointF2.set(pointF);
            PointF pointF3 = sTempPoint;
            pointF3.set(this.mDisplacement.x - this.mSubtractDisplacement.x, this.mDisplacement.y - this.mSubtractDisplacement.y);
            reportDraggingInternal(pointF3, event);
        }
    }

    private void reportDragEnd() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        PointF pointF = new PointF(this.mVelocityTracker.getXVelocity() / 1000.0f, this.mVelocityTracker.getYVelocity() / 1000.0f);
        if (this.mIsRtl) {
            pointF.x = -pointF.x;
        }
        reportDragEndInternal(pointF);
    }
}
