package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class OverviewWithoutFocusInputConsumer implements InputConsumer, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private final Context mContext;
    private final GestureState mGestureState;
    private final InputMonitorCompat mInputMonitor;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 128;
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUpCancelled() {
    }

    public OverviewWithoutFocusInputConsumer(Context context, RecentsAnimationDeviceState deviceState, GestureState gestureState, InputMonitorCompat inputMonitor, boolean disableHorizontalSwipe) {
        this.mContext = context;
        this.mGestureState = gestureState;
        this.mInputMonitor = inputMonitor;
        this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(context, disableHorizontalSwipe, deviceState.getNavBarPosition(), new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$OverviewWithoutFocusInputConsumer$kZETER4dHOB1cTpC9FREdzxtBhw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onInterceptTouch();
            }
        }, this);
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return !this.mTriggerSwipeUpTracker.interceptedTouch();
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        this.mTriggerSwipeUpTracker.onMotionEvent(ev);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInterceptTouch() {
        if (this.mInputMonitor != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
            this.mInputMonitor.pilferPointers();
        }
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUp(boolean wasFling, PointF finalVelocity) {
        this.mContext.startActivity(new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456));
        ActiveGestureLog.INSTANCE.addLog("startQuickstep");
        BaseActivity baseActivityFromContext = BaseDraggingActivity.fromContext(this.mContext);
        GestureState gestureState = this.mGestureState;
        baseActivityFromContext.getUserEventDispatcher().logActionOnContainer(wasFling ? 4 : 3, 1, (gestureState == null || gestureState.getEndTarget() == null) ? 1 : this.mGestureState.getEndTarget().containerType, -1);
        baseActivityFromContext.getUserEventDispatcher().setPreviousHomeGesture(true);
        baseActivityFromContext.getStatsLogManager().logger().withSrcState(2).withDstState(2).withContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(-1)).build()).log(StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE);
    }
}
