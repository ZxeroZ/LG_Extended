package com.android.launcher3.touch;

import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSwipeController extends AbstractStateChangeTouchController {
    private MotionEvent mTouchDownEvent;

    public AllAppsSwipeController(Launcher l) {
        super(l, SingleAxisSwipeDetector.VERTICAL);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mTouchDownEvent = ev;
        }
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        if (this.mLauncher.isInState(LauncherState.NORMAL) || this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            return !this.mLauncher.isInState(LauncherState.ALL_APPS) || this.mLauncher.getAppsView().shouldContainerScroll(ev);
        }
        return false;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        if (fromState == LauncherState.NORMAL && isDragTowardPositive) {
            return LauncherState.ALL_APPS;
        }
        return (fromState != LauncherState.ALL_APPS || isDragTowardPositive) ? fromState : LauncherState.NORMAL;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getLogContainerTypeForNormalState(MotionEvent ev) {
        return this.mLauncher.getDragLayer().isEventOverView(this.mLauncher.getHotseat(), this.mTouchDownEvent) ? 2 : 1;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animComponents) {
        float shiftRange = getShiftRange();
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, (long) (2.0f * shiftRange), animComponents);
        return 1.0f / ((this.mToState.getVerticalProgress(this.mLauncher) * shiftRange) - (this.mFromState.getVerticalProgress(this.mLauncher) * shiftRange));
    }
}
