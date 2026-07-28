package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.systemui.shared.system.InputMonitorCompat;

/* JADX INFO: loaded from: classes.dex */
public class SysUiOverlayInputConsumer implements InputConsumer, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private final Context mContext;
    private final InputMonitorCompat mInputMonitor;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 1024;
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUpCancelled() {
    }

    public SysUiOverlayInputConsumer(Context context, RecentsAnimationDeviceState deviceState, InputMonitorCompat inputMonitor) {
        this.mContext = context;
        this.mInputMonitor = inputMonitor;
        this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(context, true, deviceState.getNavBarPosition(), new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$SysUiOverlayInputConsumer$twTViC_GmZz-cZuyN339_1BFNb0
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
        this.mContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
}
