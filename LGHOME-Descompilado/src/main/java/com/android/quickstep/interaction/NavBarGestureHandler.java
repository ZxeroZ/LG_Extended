package com.android.quickstep.interaction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.util.VibratorWrapper;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.NavBarPosition;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.systemui.shared.system.QuickStepContract;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class NavBarGestureHandler implements View.OnTouchListener, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private static final String LOG_TAG = "NavBarGestureHandler";
    private static final long RETRACT_GESTURE_ANIMATION_DURATION_MS = 300;
    private final int mAssistantAngleThreshold;
    private float mAssistantDistance;
    private final float mAssistantDragDistThreshold;
    private long mAssistantDragStartTime;
    private final float mAssistantFlingDistThreshold;
    private boolean mAssistantGestureActive;
    private final GestureDetector mAssistantGestureDetector;
    private float mAssistantLastProgress;
    private final RectF mAssistantLeftRegion;
    private final RectF mAssistantRightRegion;
    private final float mAssistantSquaredSlop;
    private final PointF mAssistantStartDragPos;
    private float mAssistantTimeFraction;
    private final long mAssistantTimeThreshold;
    private final int mBottomGestureHeight;
    private final Context mContext;
    private final Point mDisplaySize;
    private final PointF mDownPos;
    private NavBarGestureAttemptCallback mGestureCallback;
    private final PointF mLastPos;
    private boolean mLaunchedAssistant;
    private boolean mPassedAssistantSlop;
    private final TriggerSwipeUpTouchTracker mSwipeUpTouchTracker;
    private boolean mTouchCameFromAssistantCorner;
    private boolean mTouchCameFromNavBar;

    interface NavBarGestureAttemptCallback {
        void onNavBarGestureAttempted(NavBarGestureResult result, PointF finalVelocity);

        default void setAssistantProgress(float progress) {
        }

        default void setNavBarGestureProgress(Float displacement) {
        }
    }

    enum NavBarGestureResult {
        UNKNOWN,
        HOME_GESTURE_COMPLETED,
        OVERVIEW_GESTURE_COMPLETED,
        HOME_NOT_STARTED_TOO_FAR_FROM_EDGE,
        OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE,
        HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION,
        HOME_OR_OVERVIEW_CANCELLED,
        ASSISTANT_COMPLETED,
        ASSISTANT_NOT_STARTED_BAD_ANGLE,
        ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT
    }

    NavBarGestureHandler(Context context) {
        int i;
        Point point = new Point();
        this.mDisplaySize = point;
        RectF rectF = new RectF();
        this.mAssistantLeftRegion = rectF;
        RectF rectF2 = new RectF();
        this.mAssistantRightRegion = rectF2;
        this.mAssistantStartDragPos = new PointF();
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mContext = context;
        Display display = context.getDisplay();
        if (display == null) {
            i = 0;
        } else {
            int rotation = display.getRotation();
            display.getRealSize(point);
            i = rotation;
        }
        this.mSwipeUpTouchTracker = new TriggerSwipeUpTouchTracker(context, true, new NavBarPosition(SysUINavigationMode.Mode.NO_BUTTON, i), null, this);
        Resources resources = context.getResources();
        int navbarSize = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, resources);
        this.mBottomGestureHeight = navbarSize;
        this.mAssistantDragDistThreshold = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        this.mAssistantFlingDistThreshold = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        this.mAssistantTimeThreshold = resources.getInteger(R.integer.assistant_gesture_min_time_threshold);
        this.mAssistantAngleThreshold = resources.getInteger(R.integer.assistant_gesture_corner_deg_threshold);
        this.mAssistantGestureDetector = new GestureDetector(context, new AssistantGestureListener());
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.gestures_assistant_width);
        float fMax = Math.max(navbarSize, QuickStepContract.getWindowCornerRadius(context));
        float f = point.y;
        rectF2.bottom = f;
        rectF.bottom = f;
        float f2 = point.y - fMax;
        rectF2.top = f2;
        rectF.top = f2;
        rectF.left = 0.0f;
        rectF.right = dimensionPixelSize;
        rectF2.right = point.x;
        rectF2.left = point.x - dimensionPixelSize;
        float scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mAssistantSquaredSlop = scaledTouchSlop * scaledTouchSlop;
    }

    void registerNavBarGestureAttemptCallback(NavBarGestureAttemptCallback callback) {
        this.mGestureCallback = callback;
    }

    void unregisterNavBarGestureAttemptCallback() {
        this.mGestureCallback = null;
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUp(boolean wasFling, PointF finalVelocity) {
        if (this.mGestureCallback == null || this.mAssistantGestureActive) {
            return;
        }
        finalVelocity.set(finalVelocity.x / 1000.0f, finalVelocity.y / 1000.0f);
        if (this.mTouchCameFromNavBar) {
            this.mGestureCallback.onNavBarGestureAttempted(wasFling ? NavBarGestureResult.HOME_GESTURE_COMPLETED : NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED, finalVelocity);
        } else {
            this.mGestureCallback.onNavBarGestureAttempted(wasFling ? NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE : NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE, finalVelocity);
        }
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUpCancelled() {
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback == null || this.mAssistantGestureActive) {
            return;
        }
        navBarGestureAttemptCallback.onNavBarGestureAttempted(NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED, new PointF());
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x00c4  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
        /*
            r7 = this;
            int r8 = r9.getAction()
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r0 = r7.mSwipeUpTouchTracker
            boolean r0 = r0.interceptedTouch()
            r1 = 0
            r2 = 1
            if (r8 == 0) goto L114
            r3 = 0
            r4 = 2
            if (r8 == r2) goto Lc4
            if (r8 == r4) goto L19
            r5 = 3
            if (r8 == r5) goto Lc4
            goto L177
        L19:
            boolean r8 = r7.mAssistantGestureActive
            if (r8 != 0) goto L1f
            goto L177
        L1f:
            android.graphics.PointF r8 = r7.mLastPos
            float r1 = r9.getX()
            float r4 = r9.getY()
            r8.set(r1, r4)
            boolean r8 = r7.mPassedAssistantSlop
            if (r8 != 0) goto L8b
            android.graphics.PointF r8 = r7.mLastPos
            float r8 = r8.x
            android.graphics.PointF r1 = r7.mDownPos
            float r1 = r1.x
            float r8 = r8 - r1
            android.graphics.PointF r1 = r7.mLastPos
            float r1 = r1.y
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.y
            float r1 = r1 - r3
            float r8 = com.android.launcher3.Utilities.squaredHypot(r8, r1)
            float r1 = r7.mAssistantSquaredSlop
            int r8 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r8 <= 0) goto L177
            r7.mPassedAssistantSlop = r2
            android.graphics.PointF r8 = r7.mAssistantStartDragPos
            android.graphics.PointF r1 = r7.mLastPos
            float r1 = r1.x
            android.graphics.PointF r2 = r7.mLastPos
            float r2 = r2.y
            r8.set(r1, r2)
            long r1 = android.os.SystemClock.uptimeMillis()
            r7.mAssistantDragStartTime = r1
            android.graphics.PointF r8 = r7.mDownPos
            float r8 = r8.x
            android.graphics.PointF r1 = r7.mLastPos
            float r1 = r1.x
            float r8 = r8 - r1
            android.graphics.PointF r1 = r7.mDownPos
            float r1 = r1.y
            android.graphics.PointF r2 = r7.mLastPos
            float r2 = r2.y
            float r1 = r1 - r2
            boolean r8 = r7.isValidAssistantGestureAngle(r8, r1)
            r7.mAssistantGestureActive = r8
            if (r8 != 0) goto L177
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r8 = r7.mGestureCallback
            if (r8 == 0) goto L177
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r1 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE
            android.graphics.PointF r2 = new android.graphics.PointF
            r2.<init>()
            r8.onNavBarGestureAttempted(r1, r2)
            goto L177
        L8b:
            android.graphics.PointF r8 = r7.mLastPos
            float r8 = r8.x
            android.graphics.PointF r1 = r7.mAssistantStartDragPos
            float r1 = r1.x
            float r8 = r8 - r1
            double r1 = (double) r8
            android.graphics.PointF r8 = r7.mLastPos
            float r8 = r8.y
            android.graphics.PointF r4 = r7.mAssistantStartDragPos
            float r4 = r4.y
            float r8 = r8 - r4
            double r4 = (double) r8
            double r1 = java.lang.Math.hypot(r1, r4)
            float r8 = (float) r1
            r7.mAssistantDistance = r8
            int r8 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r8 < 0) goto L177
            long r1 = android.os.SystemClock.uptimeMillis()
            long r3 = r7.mAssistantDragStartTime
            long r1 = r1 - r3
            float r8 = (float) r1
            r1 = 1065353216(0x3f800000, float:1.0)
            float r8 = r8 * r1
            long r2 = r7.mAssistantTimeThreshold
            float r2 = (float) r2
            float r8 = r8 / r2
            float r8 = java.lang.Math.min(r8, r1)
            r7.mAssistantTimeFraction = r8
            r7.updateAssistantProgress()
            goto L177
        Lc4:
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r8 = r7.mGestureCallback
            if (r8 == 0) goto Ldb
            if (r0 != 0) goto Ldb
            boolean r5 = r7.mTouchCameFromNavBar
            if (r5 == 0) goto Ldb
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r0 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION
            android.graphics.PointF r1 = new android.graphics.PointF
            r1.<init>()
            r8.onNavBarGestureAttempted(r0, r1)
            r0 = r2
            goto L177
        Ldb:
            boolean r5 = r7.mAssistantGestureActive
            if (r5 == 0) goto L111
            boolean r5 = r7.mLaunchedAssistant
            if (r5 != 0) goto L111
            if (r8 == 0) goto L111
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT
            android.graphics.PointF r6 = new android.graphics.PointF
            r6.<init>()
            r8.onNavBarGestureAttempted(r5, r6)
            float[] r8 = new float[r4]
            float r4 = r7.mAssistantLastProgress
            r8[r1] = r4
            r8[r2] = r3
            android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
            r2 = 300(0x12c, double:1.48E-321)
            android.animation.ValueAnimator r8 = r8.setDuration(r2)
            com.android.quickstep.interaction.-$$Lambda$NavBarGestureHandler$jsbVIeBpyw9lElyDXHlB6KgEmy8 r2 = new com.android.quickstep.interaction.-$$Lambda$NavBarGestureHandler$jsbVIeBpyw9lElyDXHlB6KgEmy8
            r2.<init>()
            r8.addUpdateListener(r2)
            android.view.animation.Interpolator r2 = com.android.launcher3.anim.Interpolators.DEACCEL_2
            r8.setInterpolator(r2)
            r8.start()
        L111:
            r7.mPassedAssistantSlop = r1
            goto L177
        L114:
            android.graphics.PointF r8 = r7.mDownPos
            float r3 = r9.getX()
            float r4 = r9.getY()
            r8.set(r3, r4)
            android.graphics.PointF r8 = r7.mLastPos
            android.graphics.PointF r3 = r7.mDownPos
            r8.set(r3)
            android.graphics.RectF r8 = r7.mAssistantLeftRegion
            float r3 = r9.getX()
            float r4 = r9.getY()
            boolean r8 = r8.contains(r3, r4)
            if (r8 != 0) goto L14b
            android.graphics.RectF r8 = r7.mAssistantRightRegion
            float r3 = r9.getX()
            float r4 = r9.getY()
            boolean r8 = r8.contains(r3, r4)
            if (r8 == 0) goto L149
            goto L14b
        L149:
            r8 = r1
            goto L14c
        L14b:
            r8 = r2
        L14c:
            r7.mTouchCameFromAssistantCorner = r8
            r7.mAssistantGestureActive = r8
            if (r8 != 0) goto L163
            android.graphics.PointF r8 = r7.mDownPos
            float r8 = r8.y
            android.graphics.Point r3 = r7.mDisplaySize
            int r3 = r3.y
            int r4 = r7.mBottomGestureHeight
            int r3 = r3 - r4
            float r3 = (float) r3
            int r8 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r8 < 0) goto L163
            goto L164
        L163:
            r2 = r1
        L164:
            r7.mTouchCameFromNavBar = r2
            if (r2 != 0) goto L170
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r8 = r7.mGestureCallback
            if (r8 == 0) goto L170
            r2 = 0
            r8.setNavBarGestureProgress(r2)
        L170:
            r7.mLaunchedAssistant = r1
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r8 = r7.mSwipeUpTouchTracker
            r8.init()
        L177:
            boolean r8 = r7.mTouchCameFromNavBar
            if (r8 == 0) goto L18f
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r8 = r7.mGestureCallback
            if (r8 == 0) goto L18f
            float r1 = r9.getY()
            android.graphics.PointF r2 = r7.mDownPos
            float r2 = r2.y
            float r1 = r1 - r2
            java.lang.Float r1 = java.lang.Float.valueOf(r1)
            r8.setNavBarGestureProgress(r1)
        L18f:
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r8 = r7.mSwipeUpTouchTracker
            r8.onMotionEvent(r9)
            android.view.GestureDetector r8 = r7.mAssistantGestureDetector
            r8.onTouchEvent(r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.NavBarGestureHandler.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    public /* synthetic */ void lambda$onTouch$0$NavBarGestureHandler(ValueAnimator valueAnimator) {
        this.mGestureCallback.setAssistantProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidAssistantGestureAngle(float deltaX, float deltaY) {
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        if (degrees > 90.0f) {
            degrees = 180.0f - degrees;
        }
        return degrees > ((float) this.mAssistantAngleThreshold) && degrees < 90.0f;
    }

    private void updateAssistantProgress() {
        if (this.mLaunchedAssistant) {
            return;
        }
        float fMin = Math.min((this.mAssistantDistance * 1.0f) / this.mAssistantDragDistThreshold, 1.0f);
        float f = this.mAssistantTimeFraction;
        float f2 = fMin * f;
        this.mAssistantLastProgress = f2;
        if (this.mAssistantDistance >= this.mAssistantDragDistThreshold && f >= 1.0f) {
            startAssistant(new PointF());
            return;
        }
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback != null) {
            navBarGestureAttemptCallback.setAssistantProgress(f2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAssistant(PointF velocity) {
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback != null) {
            navBarGestureAttemptCallback.onNavBarGestureAttempted(NavBarGestureResult.ASSISTANT_COMPLETED, velocity);
        }
        VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.EFFECT_CLICK);
        this.mLaunchedAssistant = true;
    }

    private class AssistantGestureListener extends GestureDetector.SimpleOnGestureListener {
        private AssistantGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (NavBarGestureHandler.this.mLaunchedAssistant || !NavBarGestureHandler.this.mTouchCameFromAssistantCorner) {
                return true;
            }
            PointF pointF = new PointF(velocityX, velocityY);
            if (!NavBarGestureHandler.this.isValidAssistantGestureAngle(velocityX, -velocityY)) {
                if (NavBarGestureHandler.this.mGestureCallback == null) {
                    return true;
                }
                NavBarGestureHandler.this.mGestureCallback.onNavBarGestureAttempted(NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE, pointF);
                return true;
            }
            if (NavBarGestureHandler.this.mAssistantDistance < NavBarGestureHandler.this.mAssistantFlingDistThreshold) {
                return true;
            }
            NavBarGestureHandler.this.mAssistantLastProgress = 1.0f;
            NavBarGestureHandler.this.startAssistant(pointF);
            return true;
        }
    }
}
