package com.android.launcher3.allapps;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import com.android.launcher3.Launcher;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class VerticalPullDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_UP = 1;
    private static final float FAST_FLING_PX_MS = 10.0f;
    private static final float MAX_ANGLE_FOR_SWIPE_UP_DOWN = 0.8726647f;
    public static final float RELEASE_VELOCITY_PX_MS = 1.0f;
    public static final float SCROLL_VELOCITY_DAMPENING_RC = 15.915494f;
    private static final String TAG = "VerticalPullDetector";
    private long mCurrentMillis;
    private float mDisplacementX;
    private float mDisplacementY;
    private float mDownX;
    private float mDownY;
    private boolean mIgnoreSlopWhenSettling;
    private float mLastDisplacement;
    private float mLastY;
    private Launcher mLauncher;
    Listener mListener;
    private int mScrollConditions;
    private float mSubtractDisplacement;
    private float mSwipeUpDownInitValue;
    private float mTouchSlop;
    private float mVelocity;
    private boolean mEnbleInOutTouchSlop = true;
    private ScrollState mState = ScrollState.IDLE;

    public interface Listener {
        boolean onDrag(float displacementX, float displacementY, float velocity);

        void onDragEnd(float velocity, boolean fling);

        void onDragStart(boolean start);
    }

    enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    private static float computeDampeningFactor(float deltaTime) {
        return deltaTime / (15.915494f + deltaTime);
    }

    private static float interpolate(float from, float to, float alpha) {
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

    public boolean isIdleState() {
        return this.mState == ScrollState.IDLE;
    }

    public boolean isSettlingState() {
        return this.mState == ScrollState.SETTLING;
    }

    public boolean isDraggingState() {
        return this.mState == ScrollState.DRAGGING;
    }

    public void setListener(Listener l) {
        this.mListener = l;
    }

    public VerticalPullDetector(Context context) {
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Launcher launcher = (Launcher) context;
        this.mLauncher = launcher;
        this.mSwipeUpDownInitValue = launcher.getResources().getFloat(R.dimen.config_swipeupdown_init_distance);
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollConditions = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    public int getScrollConditions() {
        return this.mScrollConditions;
    }

    public void skipInOutTouchSlop() {
        this.mEnbleInOutTouchSlop = false;
    }

    public boolean shouldScrollStart() {
        if (Math.abs(this.mDisplacementY) >= this.mTouchSlop && (!this.mEnbleInOutTouchSlop || !PagedView.IsOutOfTouchSlop())) {
            if (!checkSwipeUpAppDrawerAngleCondition(Math.max(Math.abs(this.mDisplacementX), 1.0f), Math.abs(this.mDisplacementY))) {
                return false;
            }
            int i = this.mScrollConditions;
            if (((i & 2) > 0 && this.mDisplacementY > 0.0f) || ((i & 1) > 0 && this.mDisplacementY < 0.0f)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSwipeUpAppDrawerAngleCondition(float deltaX, float deltaY) {
        float fAtan = (float) Math.atan(deltaX / Math.abs(deltaY));
        if (this.mDisplacementY >= 0.0f || LGHomeFeature.isSwipeUpAppDrawerEnable()) {
            return (this.mDisplacementY <= 0.0f || this.mLauncher.getWorkspace().getInAppsEnabled() || (LGHomeFeature.isSwipeUpAppDrawerEnable() && this.mLauncher.isAllAppsVisible())) && fAtan < MAX_ANGLE_FOR_SWIPE_UP_DOWN;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0056  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r4) {
        /*
            r3 = this;
            int r0 = r4.getAction()
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 1
            if (r0 == 0) goto L62
            if (r0 == r1) goto L56
            r2 = 2
            if (r0 == r2) goto L12
            r2 = 3
            if (r0 == r2) goto L56
            goto L84
        L12:
            float r0 = r4.getX()
            float r2 = r3.mDownX
            float r0 = r0 - r2
            r3.mDisplacementX = r0
            float r0 = r4.getY()
            float r2 = r3.mDownY
            float r0 = r0 - r2
            r3.mDisplacementY = r0
            float r0 = r3.mDisplacementX
            float r0 = java.lang.Math.abs(r0)
            float r2 = r3.mTouchSlop
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            float r0 = r3.mDisplacementY
            float r0 = java.lang.Math.abs(r0)
            float r2 = r3.mTouchSlop
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r3.computeVelocity(r4)
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = r3.mState
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r2 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.DRAGGING
            if (r0 == r2) goto L4c
            boolean r0 = r3.shouldScrollStart()
            if (r0 == 0) goto L4c
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.DRAGGING
            r3.setState(r0)
        L4c:
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = r3.mState
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r2 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.DRAGGING
            if (r0 != r2) goto L84
            r3.reportDragging()
            goto L84
        L56:
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = r3.mState
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r2 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.DRAGGING
            if (r0 != r2) goto L84
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.SETTLING
            r3.setState(r0)
            goto L84
        L62:
            float r0 = r4.getX()
            r3.mDownX = r0
            float r0 = r4.getY()
            r3.mDownY = r0
            r0 = 0
            r3.mLastDisplacement = r0
            r3.mDisplacementY = r0
            r3.mVelocity = r0
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = r3.mState
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r2 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.SETTLING
            if (r0 != r2) goto L84
            boolean r0 = r3.mIgnoreSlopWhenSettling
            if (r0 == 0) goto L84
            com.android.launcher3.allapps.VerticalPullDetector$ScrollState r0 = com.android.launcher3.allapps.VerticalPullDetector.ScrollState.DRAGGING
            r3.setState(r0)
        L84:
            float r0 = r3.mDisplacementY
            r3.mLastDisplacement = r0
            float r4 = r4.getY()
            r3.mLastY = r4
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.VerticalPullDetector.onTouchEvent(android.view.MotionEvent):boolean");
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
        if (this.mDisplacementY > 0.0f) {
            this.mSubtractDisplacement = this.mTouchSlop;
        } else {
            this.mSubtractDisplacement = -this.mTouchSlop;
        }
    }

    private boolean reportDragging() {
        float f = this.mDisplacementY;
        if (f - this.mLastDisplacement != 0.0f) {
            return this.mListener.onDrag(this.mDisplacementX, f, this.mVelocity);
        }
        return true;
    }

    private void reportDragEnd() {
        Listener listener = this.mListener;
        float f = this.mVelocity;
        listener.onDragEnd(f, Math.abs(f) > 1.0f);
    }

    private float computeVelocity(MotionEvent to) {
        return computeVelocity(to.getY() - this.mLastY, to.getEventTime());
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

    public long calculateDuration(float velocity, float progressNeeded) {
        float fMax = Math.max(2.0f, Math.abs(velocity * 0.5f));
        return (long) Math.max(100.0f, (ANIMATION_DURATION / fMax) * Math.max(0.2f, progressNeeded));
    }

    public static class ScrollInterpolator implements Interpolator {
        boolean mSteeper;

        public void setVelocityAtZero(float velocity) {
            this.mSteeper = velocity > VerticalPullDetector.FAST_FLING_PX_MS;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            float f = t - 1.0f;
            float f2 = f * f;
            float f3 = f * f2;
            if (this.mSteeper) {
                f3 *= f2;
            }
            return f3 + 1.0f;
        }
    }
}
