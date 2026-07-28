package com.android.quickstep.inputconsumers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.MultiStateCallback;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputMonitorCompat;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class ProgressDelegateInputConsumer implements InputConsumer, RecentsAnimationCallbacks.RecentsAnimationListener, SingleAxisSwipeDetector.Listener {
    private static final String[] STATE_NAMES = null;
    private static final float SWIPE_DISTANCE_THRESHOLD = 0.2f;
    private final Context mContext;
    private final Point mDisplaySize;
    private boolean mDragStarted = false;
    private Boolean mFlingEndsOnHome;
    private final GestureState mGestureState;
    private final InputMonitorCompat mInputMonitorCompat;
    private final AnimatedFloat mProgress;
    private RecentsAnimationController mRecentsAnimationController;
    private final MultiStateCallback mStateCallback;
    private final SingleAxisSwipeDetector mSwipeDetector;
    private final TaskAnimationManager mTaskAnimationManager;
    private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
    private static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");
    private static final int STATE_FLING_FINISHED = getFlagForIndex(2, "STATE_FLING_FINISHED");

    private static int getFlagForIndex(int index, String name) {
        return 1 << index;
    }

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 512;
    }

    public ProgressDelegateInputConsumer(Context context, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputMonitorCompat inputMonitorCompat, AnimatedFloat progress) {
        this.mContext = context;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mProgress = progress;
        this.mDisplaySize = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo().currentSize;
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        int i = STATE_TARGET_RECEIVED;
        multiStateCallback.runOnceAtState(STATE_HANDLER_INVALIDATED | i, new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$ProgressDelegateInputConsumer$BNYXIISo6hjrdGPyr9fgOyN9t64
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.endRemoteAnimation();
            }
        });
        multiStateCallback.runOnceAtState(i | STATE_FLING_FINISHED, new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$ProgressDelegateInputConsumer$YpTffzFFZCgZCt17IotGI8muEdQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onFlingFinished();
            }
        });
        SingleAxisSwipeDetector singleAxisSwipeDetector = new SingleAxisSwipeDetector(context, this, SingleAxisSwipeDetector.VERTICAL);
        this.mSwipeDetector = singleAxisSwipeDetector;
        singleAxisSwipeDetector.setDetectableScrollConditions(1, false);
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        if (this.mFlingEndsOnHome == null) {
            this.mSwipeDetector.onTouchEvent(ev);
        }
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        this.mDragStarted = true;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitorCompat.pilferPointers();
        this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, this.mGestureState.getHomeIntent().putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId()), this);
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement) {
        if (this.mDisplaySize.y <= 0) {
            return true;
        }
        this.mProgress.updateValue(displacement / (-this.mDisplaySize.y));
        return true;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        boolean z = true;
        if (!this.mSwipeDetector.isFling(velocity) ? this.mProgress.value <= 0.2f : velocity >= 0.0f) {
            z = false;
        }
        float f = z ? 1.0f : 0.0f;
        long jCalculateDuration = BaseSwipeDetector.calculateDuration(velocity, f - this.mProgress.value);
        this.mFlingEndsOnHome = Boolean.valueOf(z);
        ObjectAnimator objectAnimatorAnimateToValue = this.mProgress.animateToValue(f);
        objectAnimatorAnimateToValue.setDuration(jCalculateDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity));
        objectAnimatorAnimateToValue.addListener(AnimatorListeners.forSuccessCallback(new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$ProgressDelegateInputConsumer$dba59EQdGakcRzK4IjZLyiS_fo4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDragEnd$0$ProgressDelegateInputConsumer();
            }
        }));
        objectAnimatorAnimateToValue.start();
    }

    public /* synthetic */ void lambda$onDragEnd$0$ProgressDelegateInputConsumer() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_FLING_FINISHED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFlingFinished() {
        if (this.mRecentsAnimationController != null) {
            Boolean bool = this.mFlingEndsOnHome;
            this.mRecentsAnimationController.finishController(bool == null ? true : bool.booleanValue(), null, false);
        }
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
        this.mRecentsAnimationController = controller;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_TARGET_RECEIVED);
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        this.mRecentsAnimationController = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endRemoteAnimation() {
        onDragEnd(Float.MIN_VALUE);
    }

    @Override // com.android.quickstep.InputConsumer
    public void onConsumerAboutToBeSwitched() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_HANDLER_INVALIDATED);
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return !this.mDragStarted;
    }
}
