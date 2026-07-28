package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.MultiStateCallback;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class DeviceLockedInputConsumer implements InputConsumer, RecentsAnimationCallbacks.RecentsAnimationListener, TransformParams.BuilderProxy {
    private static final String[] STATE_NAMES = null;
    private final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Point mDisplaySize;
    private final GestureState mGestureState;
    private final InputMonitorCompat mInputMonitorCompat;
    private final float mMaxTranslationY;
    private RecentsAnimationController mRecentsAnimationController;
    private final MultiStateCallback mStateCallback;
    private final TaskAnimationManager mTaskAnimationManager;
    private final float mTouchSlopSquared;
    private VelocityTracker mVelocityTracker;
    private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
    private static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");
    private final PointF mTouchDown = new PointF();
    private final Matrix mMatrix = new Matrix();
    private final AnimatedFloat mProgress = new AnimatedFloat(new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$DeviceLockedInputConsumer$FHlgMKJUiGUn87oIIlGDaKrujk4
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.applyTransform();
        }
    });
    private boolean mThresholdCrossed = false;
    private boolean mHomeLaunched = false;
    private final TransformParams mTransformParams = new TransformParams();

    private static int getFlagForIndex(int index, String name) {
        return 1 << index;
    }

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 16;
    }

    public DeviceLockedInputConsumer(Context context, RecentsAnimationDeviceState deviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputMonitorCompat inputMonitorCompat) {
        this.mContext = context;
        this.mDeviceState = deviceState;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mTouchSlopSquared = Utilities.squaredTouchSlop(context);
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mMaxTranslationY = context.getResources().getDimensionPixelSize(R.dimen.device_locked_y_offset);
        this.mDisplaySize = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo().currentSize;
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        multiStateCallback.runOnceAtState(STATE_TARGET_RECEIVED | STATE_HANDLER_INVALIDATED, new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$DeviceLockedInputConsumer$ZgCxbg7REHv7tduAgg8Ae6xIfXI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.endRemoteAnimation();
            }
        });
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            return;
        }
        velocityTracker.addMovement(ev);
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        if (action == 0) {
            this.mTouchDown.set(x, y);
            return;
        }
        if (action != 1) {
            if (action == 2) {
                if (this.mThresholdCrossed) {
                    this.mProgress.updateValue(Math.max(this.mTouchDown.y - y, 0.0f) / this.mDisplaySize.y);
                    return;
                } else {
                    if (Utilities.squaredHypot(x - this.mTouchDown.x, y - this.mTouchDown.y) > this.mTouchSlopSquared) {
                        startRecentsTransition();
                        return;
                    }
                    return;
                }
            }
            if (action != 3) {
                if (action == 5 && !this.mThresholdCrossed) {
                    if (this.mDeviceState.isInSwipeUpTouchRegion(ev, ev.getActionIndex())) {
                        return;
                    }
                    int action2 = ev.getAction();
                    ev.setAction(3);
                    finishTouchTracking(ev);
                    ev.setAction(action2);
                    return;
                }
                return;
            }
        }
        finishTouchTracking(ev);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x006a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void finishTouchTracking(android.view.MotionEvent r5) {
        /*
            r4 = this;
            boolean r0 = r4.mThresholdCrossed
            if (r0 == 0) goto L6a
            int r5 = r5.getAction()
            r0 = 1
            if (r5 != r0) goto L6a
            android.view.VelocityTracker r5 = r4.mVelocityTracker
            r1 = 1000(0x3e8, float:1.401E-42)
            android.content.Context r2 = r4.mContext
            android.view.ViewConfiguration r2 = android.view.ViewConfiguration.get(r2)
            int r2 = r2.getScaledMaximumFlingVelocity()
            float r2 = (float) r2
            r5.computeCurrentVelocity(r1, r2)
            android.view.VelocityTracker r5 = r4.mVelocityTracker
            float r5 = r5.getYVelocity()
            android.content.Context r1 = r4.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131166180(0x7f0703e4, float:1.7946598E38)
            float r1 = r1.getDimension(r2)
            float r2 = java.lang.Math.abs(r5)
            int r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            r2 = 0
            r3 = 0
            if (r1 <= 0) goto L41
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 >= 0) goto L3f
            goto L4c
        L3f:
            r0 = r2
            goto L4c
        L41:
            com.android.quickstep.AnimatedFloat r5 = r4.mProgress
            float r5 = r5.value
            r1 = 1050253722(0x3e99999a, float:0.3)
            int r5 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r5 < 0) goto L3f
        L4c:
            com.android.quickstep.AnimatedFloat r5 = r4.mProgress
            float r1 = r5.value
            android.animation.ObjectAnimator r5 = r5.animateToValue(r1, r3)
            r1 = 100
            r5.setDuration(r1)
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.ACCEL
            r5.setInterpolator(r1)
            com.android.quickstep.inputconsumers.DeviceLockedInputConsumer$1 r1 = new com.android.quickstep.inputconsumers.DeviceLockedInputConsumer$1
            r1.<init>()
            r5.addListener(r1)
            r5.start()
            goto L71
        L6a:
            com.android.quickstep.MultiStateCallback r5 = r4.mStateCallback
            int r0 = com.android.quickstep.inputconsumers.DeviceLockedInputConsumer.STATE_HANDLER_INVALIDATED
            r5.lambda$setStateOnUiThread$0$MultiStateCallback(r0)
        L71:
            android.view.VelocityTracker r5 = r4.mVelocityTracker
            r5.recycle()
            r5 = 0
            r4.mVelocityTracker = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.inputconsumers.DeviceLockedInputConsumer.finishTouchTracking(android.view.MotionEvent):void");
    }

    private void startRecentsTransition() {
        this.mThresholdCrossed = true;
        this.mHomeLaunched = false;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitorCompat.pilferPointers();
        this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, this.mGestureState.getHomeIntent().putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId()), this);
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
        this.mRecentsAnimationController = controller;
        this.mTransformParams.setTargetSet(targets);
        applyTransform();
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_TARGET_RECEIVED);
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        this.mRecentsAnimationController = null;
        this.mTransformParams.setTargetSet(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endRemoteAnimation() {
        if (this.mHomeLaunched) {
            ActivityManagerWrapper.getInstance().cancelRecentsAnimation(false);
            return;
        }
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finishController(false, null, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyTransform() {
        this.mTransformParams.setProgress(this.mProgress.value);
        if (this.mTransformParams.getTargetSet() != null) {
            TransformParams transformParams = this.mTransformParams;
            transformParams.applySurfaceParams(transformParams.createSurfaceParams(this));
        }
    }

    @Override // com.android.quickstep.util.TransformParams.BuilderProxy
    public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
        this.mMatrix.setTranslate(0.0f, this.mProgress.value * this.mMaxTranslationY);
        builder.withMatrix(this.mMatrix);
    }

    @Override // com.android.quickstep.InputConsumer
    public void onConsumerAboutToBeSwitched() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_HANDLER_INVALIDATED);
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return !this.mThresholdCrossed;
    }
}
