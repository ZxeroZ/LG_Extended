package com.android.launcher3.uioverrides.touchcontrollers;

import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class OverviewToAllAppsTouchController extends PortraitStatesTouchController {
    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getLogContainerTypeForNormalState(MotionEvent ev) {
        return 1;
    }

    public OverviewToAllAppsTouchController(Launcher l) {
        super(l, true);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        if (this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            return this.mLauncher.getAppsView().shouldContainerScroll(ev);
        }
        if (this.mLauncher.isInState(LauncherState.NORMAL)) {
            return (ev.getEdgeFlags() & 256) == 0;
        }
        if (!this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            return false;
        }
        RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
        return ev.getY() > ((float) (recentsView.getBottom() - recentsView.getPaddingBottom()));
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        return (fromState != LauncherState.ALL_APPS || isDragTowardPositive) ? isDragTowardPositive ? LauncherState.ALL_APPS : fromState : TouchInteractionService.isConnected() ? (LauncherState) this.mLauncher.getStateManager().getLastState() : LauncherState.NORMAL;
    }
}
