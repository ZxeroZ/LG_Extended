package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class AccessibilityInputConsumer extends DelegateInputConsumer {
    private static final String TAG = "A11yInputConsumer";
    private int mActivePointerId;
    private final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private float mDownY;
    private final float mMinFlingVelocity;
    private final float mMinGestureDistance;
    private final MotionPauseDetector mMotionPauseDetector;
    private float mTotalY;
    private final VelocityTracker mVelocityTracker;

    public AccessibilityInputConsumer(Context context, RecentsAnimationDeviceState deviceState, InputConsumer delegate, InputMonitorCompat inputMonitor) {
        super(delegate, inputMonitor);
        this.mActivePointerId = -1;
        this.mContext = context;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mMinGestureDistance = context.getResources().getDimension(R.dimen.accessibility_gesture_min_swipe_distance);
        this.mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        this.mDeviceState = deviceState;
        this.mMotionPauseDetector = new MotionPauseDetector(context);
    }

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return this.mDelegate.getType() | 32;
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    @Override // com.android.quickstep.InputConsumer
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onMotionEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r5.mState
            r1 = 2
            if (r0 == r1) goto La
            android.view.VelocityTracker r0 = r5.mVelocityTracker
            r0.addMovement(r6)
        La:
            int r0 = r6.getActionMasked()
            r2 = 0
            r3 = 1
            if (r0 == r3) goto L95
            if (r0 == r1) goto L79
            r4 = 3
            if (r0 == r4) goto Led
            r4 = 5
            if (r0 == r4) goto L4c
            r1 = 6
            if (r0 == r1) goto L1f
            goto Lf7
        L1f:
            int r0 = r5.mState
            if (r0 != r3) goto Lf7
            int r0 = r6.getActionIndex()
            int r1 = r6.getPointerId(r0)
            int r4 = r5.mActivePointerId
            if (r1 != r4) goto Lf7
            if (r0 != 0) goto L32
            r2 = r3
        L32:
            float r1 = r5.mTotalY
            float r0 = r6.getY(r0)
            float r4 = r5.mDownY
            float r0 = r0 - r4
            float r1 = r1 + r0
            r5.mTotalY = r1
            float r0 = r6.getY(r2)
            r5.mDownY = r0
            int r0 = r6.getPointerId(r2)
            r5.mActivePointerId = r0
            goto Lf7
        L4c:
            int r0 = r5.mState
            if (r0 != 0) goto Lf7
            int r0 = r6.getActionIndex()
            com.android.quickstep.RecentsAnimationDeviceState r2 = r5.mDeviceState
            boolean r2 = r2.isInSwipeUpTouchRegion(r6, r0)
            if (r2 == 0) goto L75
            com.android.quickstep.InputConsumer r2 = r5.mDelegate
            boolean r2 = r2.allowInterceptByParent()
            if (r2 == 0) goto L75
            r5.setActive(r6)
            int r1 = r6.getPointerId(r0)
            r5.mActivePointerId = r1
            float r0 = r6.getY(r0)
            r5.mDownY = r0
            goto Lf7
        L75:
            r5.mState = r1
            goto Lf7
        L79:
            int r0 = r5.mState
            if (r0 != r3) goto Lf7
            com.android.quickstep.RecentsAnimationDeviceState r0 = r5.mDeviceState
            boolean r0 = r0.isAccessibilityMenuShortcutAvailable()
            if (r0 == 0) goto Lf7
            int r0 = r5.mActivePointerId
            int r0 = r6.findPointerIndex(r0)
            r1 = -1
            if (r0 != r1) goto L8f
            goto Lf7
        L8f:
            com.android.quickstep.util.MotionPauseDetector r1 = r5.mMotionPauseDetector
            r1.addPosition(r6, r0)
            goto Lf7
        L95:
            int r0 = r5.mState
            if (r0 != r3) goto Led
            com.android.quickstep.RecentsAnimationDeviceState r0 = r5.mDeviceState
            boolean r0 = r0.isAccessibilityMenuShortcutAvailable()
            if (r0 == 0) goto Lb7
            com.android.quickstep.util.MotionPauseDetector r0 = r5.mMotionPauseDetector
            boolean r0 = r0.isPaused()
            if (r0 == 0) goto Lb7
            com.android.launcher3.util.MainThreadInitializedObject<com.android.quickstep.SystemUiProxy> r0 = com.android.quickstep.SystemUiProxy.INSTANCE
            android.content.Context r1 = r5.mContext
            java.lang.Object r0 = r0.lambda$get$0$MainThreadInitializedObject(r1)
            com.android.quickstep.SystemUiProxy r0 = (com.android.quickstep.SystemUiProxy) r0
            r0.notifyAccessibilityButtonLongClicked()
            goto Led
        Lb7:
            float r0 = r5.mTotalY
            float r1 = r6.getY()
            float r4 = r5.mDownY
            float r1 = r1 - r4
            float r0 = r0 + r1
            r5.mTotalY = r0
            android.view.VelocityTracker r0 = r5.mVelocityTracker
            r1 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r1)
            float r0 = r5.mTotalY
            float r0 = -r0
            float r1 = r5.mMinGestureDistance
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto Le0
            android.view.VelocityTracker r0 = r5.mVelocityTracker
            float r0 = r0.getYVelocity()
            float r0 = -r0
            float r1 = r5.mMinFlingVelocity
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto Led
        Le0:
            com.android.launcher3.util.MainThreadInitializedObject<com.android.quickstep.SystemUiProxy> r0 = com.android.quickstep.SystemUiProxy.INSTANCE
            android.content.Context r1 = r5.mContext
            java.lang.Object r0 = r0.lambda$get$0$MainThreadInitializedObject(r1)
            com.android.quickstep.SystemUiProxy r0 = (com.android.quickstep.SystemUiProxy) r0
            r0.notifyAccessibilityButtonClicked(r2)
        Led:
            android.view.VelocityTracker r0 = r5.mVelocityTracker
            r0.recycle()
            com.android.quickstep.util.MotionPauseDetector r0 = r5.mMotionPauseDetector
            r0.clear()
        Lf7:
            int r0 = r5.mState
            if (r0 == r3) goto L100
            com.android.quickstep.InputConsumer r0 = r5.mDelegate
            r0.onMotionEvent(r6)
        L100:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.inputconsumers.AccessibilityInputConsumer.onMotionEvent(android.view.MotionEvent):void");
    }
}
