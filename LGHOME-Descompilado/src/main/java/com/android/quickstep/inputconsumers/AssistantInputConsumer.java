package com.android.quickstep.inputconsumers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.SystemUiProxy;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.R;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class AssistantInputConsumer extends DelegateInputConsumer {
    private static final int INVOCATION_TYPE_GESTURE = 1;
    private static final String INVOCATION_TYPE_KEY = "invocation_type";
    private static final String OPA_BUNDLE_TRIGGER = "triggered_by";
    private static final int OPA_BUNDLE_TRIGGER_DIAG_SWIPE_GESTURE = 83;
    private static final long RETRACT_ANIMATION_DURATION_MS = 300;
    private static final String TAG = "AssistantInputConsumer";
    private int mActivePointerId;
    private BaseActivityInterface mActivityInterface;
    private final int mAngleThreshold;
    private final Context mContext;
    private float mDistance;
    private final PointF mDownPos;
    private final float mDragDistThreshold;
    private long mDragTime;
    private final float mFlingDistThreshold;
    private final Consumer<MotionEvent> mGestureDetector;
    private final PointF mLastPos;
    private float mLastProgress;
    private boolean mLaunchedAssistant;
    private boolean mPassedSlop;
    private final float mSquaredSlop;
    private final PointF mStartDragPos;
    private float mTimeFraction;
    private final long mTimeThreshold;

    static /* synthetic */ void lambda$new$0(MotionEvent motionEvent) {
    }

    public AssistantInputConsumer(Context context, GestureState gestureState, InputConsumer delegate, InputMonitorCompat inputMonitor, RecentsAnimationDeviceState deviceState, MotionEvent startEvent) {
        Consumer<MotionEvent> consumer;
        super(delegate, inputMonitor);
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mStartDragPos = new PointF();
        this.mActivePointerId = -1;
        Resources resources = context.getResources();
        this.mContext = context;
        this.mDragDistThreshold = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        this.mFlingDistThreshold = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        this.mTimeThreshold = resources.getInteger(R.integer.assistant_gesture_min_time_threshold);
        this.mAngleThreshold = resources.getInteger(R.integer.assistant_gesture_corner_deg_threshold);
        float scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mSquaredSlop = scaledTouchSlop * scaledTouchSlop;
        this.mActivityInterface = gestureState.getActivityInterface();
        if (deviceState.isAssistantGestureIsConstrained() || deviceState.isInDeferredGestureRegion(startEvent)) {
            consumer = new Consumer() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$AssistantInputConsumer$08Y__pD2w2JXyQdqnYwIuUanlBQ
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AssistantInputConsumer.lambda$new$0((MotionEvent) obj);
                }
            };
        } else {
            final GestureDetector gestureDetector = new GestureDetector(context, new AssistantGestureListener());
            consumer = new Consumer() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$AssistantInputConsumer$63AwQY3zqESGln-1-tC6aDc6tus
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    gestureDetector.onTouchEvent((MotionEvent) obj);
                }
            };
        }
        this.mGestureDetector = consumer;
    }

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return this.mDelegate.getType() | 8;
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x011e  */
    @Override // com.android.quickstep.InputConsumer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onMotionEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            int r0 = r9.getActionMasked()
            r1 = 0
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L155
            r4 = 2
            if (r0 == r3) goto L11e
            if (r0 == r4) goto L66
            r5 = 3
            if (r0 == r5) goto L11e
            r1 = 5
            if (r0 == r1) goto L5e
            r1 = 6
            if (r0 == r1) goto L19
            goto L171
        L19:
            int r0 = r9.getActionIndex()
            int r1 = r9.getPointerId(r0)
            int r4 = r8.mActivePointerId
            if (r1 != r4) goto L171
            if (r0 != 0) goto L28
            r2 = r3
        L28:
            android.graphics.PointF r0 = r8.mDownPos
            float r1 = r9.getX(r2)
            android.graphics.PointF r4 = r8.mLastPos
            float r4 = r4.x
            android.graphics.PointF r5 = r8.mDownPos
            float r5 = r5.x
            float r4 = r4 - r5
            float r1 = r1 - r4
            float r4 = r9.getY(r2)
            android.graphics.PointF r5 = r8.mLastPos
            float r5 = r5.y
            android.graphics.PointF r6 = r8.mDownPos
            float r6 = r6.y
            float r5 = r5 - r6
            float r4 = r4 - r5
            r0.set(r1, r4)
            android.graphics.PointF r0 = r8.mLastPos
            float r1 = r9.getX(r2)
            float r4 = r9.getY(r2)
            r0.set(r1, r4)
            int r0 = r9.getPointerId(r2)
            r8.mActivePointerId = r0
            goto L171
        L5e:
            int r0 = r8.mState
            if (r0 == r3) goto L171
            r8.mState = r4
            goto L171
        L66:
            int r0 = r8.mState
            if (r0 != r4) goto L6c
            goto L171
        L6c:
            com.android.quickstep.InputConsumer r0 = r8.mDelegate
            boolean r0 = r0.allowInterceptByParent()
            if (r0 != 0) goto L78
            r8.mState = r4
            goto L171
        L78:
            int r0 = r8.mActivePointerId
            int r0 = r9.findPointerIndex(r0)
            r2 = -1
            if (r0 != r2) goto L83
            goto L171
        L83:
            android.graphics.PointF r2 = r8.mLastPos
            float r5 = r9.getX(r0)
            float r0 = r9.getY(r0)
            r2.set(r5, r0)
            boolean r0 = r8.mPassedSlop
            if (r0 != 0) goto Le6
            android.graphics.PointF r0 = r8.mLastPos
            float r0 = r0.x
            android.graphics.PointF r1 = r8.mDownPos
            float r1 = r1.x
            float r0 = r0 - r1
            android.graphics.PointF r1 = r8.mLastPos
            float r1 = r1.y
            android.graphics.PointF r2 = r8.mDownPos
            float r2 = r2.y
            float r1 = r1 - r2
            float r0 = com.android.launcher3.Utilities.squaredHypot(r0, r1)
            float r1 = r8.mSquaredSlop
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L171
            r8.mPassedSlop = r3
            android.graphics.PointF r0 = r8.mStartDragPos
            android.graphics.PointF r1 = r8.mLastPos
            float r1 = r1.x
            android.graphics.PointF r2 = r8.mLastPos
            float r2 = r2.y
            r0.set(r1, r2)
            long r0 = android.os.SystemClock.uptimeMillis()
            r8.mDragTime = r0
            android.graphics.PointF r0 = r8.mDownPos
            float r0 = r0.x
            android.graphics.PointF r1 = r8.mLastPos
            float r1 = r1.x
            float r0 = r0 - r1
            android.graphics.PointF r1 = r8.mDownPos
            float r1 = r1.y
            android.graphics.PointF r2 = r8.mLastPos
            float r2 = r2.y
            float r1 = r1 - r2
            boolean r0 = r8.isValidAssistantGestureAngle(r0, r1)
            if (r0 == 0) goto Le2
            r8.setActive(r9)
            goto L171
        Le2:
            r8.mState = r4
            goto L171
        Le6:
            android.graphics.PointF r0 = r8.mLastPos
            float r0 = r0.x
            android.graphics.PointF r2 = r8.mStartDragPos
            float r2 = r2.x
            float r0 = r0 - r2
            double r4 = (double) r0
            android.graphics.PointF r0 = r8.mLastPos
            float r0 = r0.y
            android.graphics.PointF r2 = r8.mStartDragPos
            float r2 = r2.y
            float r0 = r0 - r2
            double r6 = (double) r0
            double r4 = java.lang.Math.hypot(r4, r6)
            float r0 = (float) r4
            r8.mDistance = r0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L171
            long r0 = android.os.SystemClock.uptimeMillis()
            long r4 = r8.mDragTime
            long r0 = r0 - r4
            float r0 = (float) r0
            r1 = 1065353216(0x3f800000, float:1.0)
            float r0 = r0 * r1
            long r4 = r8.mTimeThreshold
            float r2 = (float) r4
            float r0 = r0 / r2
            float r0 = java.lang.Math.min(r0, r1)
            r8.mTimeFraction = r0
            r8.updateAssistantProgress()
            goto L171
        L11e:
            int r0 = r8.mState
            if (r0 == r4) goto L150
            boolean r0 = r8.mLaunchedAssistant
            if (r0 != 0) goto L150
            float[] r0 = new float[r4]
            float r4 = r8.mLastProgress
            r0[r2] = r4
            r0[r3] = r1
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r4 = 300(0x12c, double:1.48E-321)
            android.animation.ValueAnimator r0 = r0.setDuration(r4)
            com.android.quickstep.inputconsumers.-$$Lambda$AssistantInputConsumer$aAn6CxQkiv_P-ViWd6tKVK0tm9c r1 = new com.android.quickstep.inputconsumers.-$$Lambda$AssistantInputConsumer$aAn6CxQkiv_P-ViWd6tKVK0tm9c
            r1.<init>()
            r0.addUpdateListener(r1)
            com.android.quickstep.inputconsumers.AssistantInputConsumer$1 r1 = new com.android.quickstep.inputconsumers.AssistantInputConsumer$1
            r1.<init>()
            r0.addListener(r1)
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.DEACCEL_2
            r0.setInterpolator(r1)
            r0.start()
        L150:
            r8.mPassedSlop = r2
            r8.mState = r2
            goto L171
        L155:
            int r0 = r9.getPointerId(r2)
            r8.mActivePointerId = r0
            android.graphics.PointF r0 = r8.mDownPos
            float r2 = r9.getX()
            float r4 = r9.getY()
            r0.set(r2, r4)
            android.graphics.PointF r0 = r8.mLastPos
            android.graphics.PointF r2 = r8.mDownPos
            r0.set(r2)
            r8.mTimeFraction = r1
        L171:
            java.util.function.Consumer<android.view.MotionEvent> r0 = r8.mGestureDetector
            r0.accept(r9)
            int r0 = r8.mState
            if (r0 == r3) goto L17f
            com.android.quickstep.InputConsumer r0 = r8.mDelegate
            r0.onMotionEvent(r9)
        L17f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.inputconsumers.AssistantInputConsumer.onMotionEvent(android.view.MotionEvent):void");
    }

    public /* synthetic */ void lambda$onMotionEvent$1$AssistantInputConsumer(ValueAnimator valueAnimator) {
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).onAssistantProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void updateAssistantProgress() {
        if (this.mLaunchedAssistant) {
            return;
        }
        float fMin = Math.min((this.mDistance * 1.0f) / this.mDragDistThreshold, 1.0f);
        float f = this.mTimeFraction;
        this.mLastProgress = fMin * f;
        if (this.mDistance >= this.mDragDistThreshold && f >= 1.0f) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).onAssistantGestureCompletion(0.0f);
            startAssistantInternal();
        } else {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).onAssistantProgress(this.mLastProgress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAssistantInternal() {
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getRootView().performHapticFeedback(13, 1);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(OPA_BUNDLE_TRIGGER, 83);
        bundle.putInt(INVOCATION_TYPE_KEY, 1);
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).startAssistant(bundle);
        this.mLaunchedAssistant = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidAssistantGestureAngle(float deltaX, float deltaY) {
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        if (degrees > 90.0f) {
            degrees = 180.0f - degrees;
        }
        return degrees > ((float) this.mAngleThreshold) && degrees < 90.0f;
    }

    private class AssistantGestureListener extends GestureDetector.SimpleOnGestureListener {
        private AssistantGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!AssistantInputConsumer.this.isValidAssistantGestureAngle(velocityX, -velocityY) || AssistantInputConsumer.this.mDistance < AssistantInputConsumer.this.mFlingDistThreshold || AssistantInputConsumer.this.mLaunchedAssistant || AssistantInputConsumer.this.mState == 2) {
                return true;
            }
            AssistantInputConsumer.this.mLastProgress = 1.0f;
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(AssistantInputConsumer.this.mContext).onAssistantGestureCompletion((float) Math.sqrt((velocityX * velocityX) + (velocityY * velocityY)));
            AssistantInputConsumer.this.startAssistantInternal();
            return true;
        }
    }
}
