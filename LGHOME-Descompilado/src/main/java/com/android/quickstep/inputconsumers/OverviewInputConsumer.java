package com.android.quickstep.inputconsumers;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class OverviewInputConsumer<T extends StatefulActivity<?>> implements InputConsumer {
    private final T mActivity;
    private final BaseActivityInterface<?, T> mActivityInterface;
    private final InputMonitorCompat mInputMonitor;
    private final int[] mLocationOnScreen;
    private final boolean mStartingInActivityBounds;
    private final BaseDragLayer mTarget;
    private boolean mTargetHandledTouch;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 2;
    }

    public OverviewInputConsumer(GestureState gestureState, T activity, InputMonitorCompat inputMonitor, boolean startingInActivityBounds) {
        int[] iArr = new int[2];
        this.mLocationOnScreen = iArr;
        this.mActivity = activity;
        this.mInputMonitor = inputMonitor;
        this.mStartingInActivityBounds = startingInActivityBounds;
        this.mActivityInterface = gestureState.getActivityInterface();
        BaseDragLayer dragLayer = activity.getDragLayer();
        this.mTarget = dragLayer;
        dragLayer.getLocationOnScreen(iArr);
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return !this.mTargetHandledTouch;
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        int edgeFlags = ev.getEdgeFlags();
        if (!this.mStartingInActivityBounds) {
            ev.setEdgeFlags(edgeFlags | 256);
        }
        int[] iArr = this.mLocationOnScreen;
        ev.offsetLocation(-iArr[0], -iArr[1]);
        if (ev.getDisplayId() == 0) {
            WindowUtils.checkGestureHome(ev, this.mActivity);
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "OverviewInputConsumer");
        }
        boolean zProxyTouchEvent = this.mTarget.proxyTouchEvent(ev, this.mStartingInActivityBounds);
        int[] iArr2 = this.mLocationOnScreen;
        ev.offsetLocation(iArr2[0], iArr2[1]);
        ev.setEdgeFlags(edgeFlags);
        if (this.mTargetHandledTouch || !zProxyTouchEvent) {
            return;
        }
        this.mTargetHandledTouch = true;
        if (!this.mStartingInActivityBounds) {
            this.mActivityInterface.closeOverlay();
            ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
            ActiveGestureLog.INSTANCE.addLog("startQuickstep");
        }
        if (this.mInputMonitor != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
            this.mInputMonitor.pilferPointers();
        }
    }

    @Override // com.android.quickstep.InputConsumer
    public void onKeyEvent(KeyEvent ev) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivity.dispatchKeyEvent(ev);
        }
    }
}
