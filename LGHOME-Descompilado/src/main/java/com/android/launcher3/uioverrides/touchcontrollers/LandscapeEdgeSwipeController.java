package com.android.launcher3.uioverrides.touchcontrollers;

import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.quickstep.SystemUiProxy;

/* JADX INFO: loaded from: classes.dex */
public class LandscapeEdgeSwipeController extends AbstractStateChangeTouchController {
    private static final String TAG = "LandscapeEdgeSwipeCtrl";

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getLogContainerTypeForNormalState(MotionEvent ev) {
        return 11;
    }

    public LandscapeEdgeSwipeController(Launcher l) {
        super(l, SingleAxisSwipeDetector.HORIZONTAL);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        return this.mLauncher.isInState(LauncherState.NORMAL) && (ev.getEdgeFlags() & 256) != 0;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        return this.mLauncher.getDeviceProfile().isSeascape() == isDragTowardPositive ? LauncherState.OVERVIEW : LauncherState.NORMAL;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float getShiftRange() {
        return this.mLauncher.getDragLayer().getWidth();
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animComponent) {
        float shiftRange = getShiftRange();
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, (long) (2.0f * shiftRange), animComponent);
        return (this.mLauncher.getDeviceProfile().isSeascape() ? 2 : -2) / shiftRange;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getDirectionForLog() {
        return this.mLauncher.getDeviceProfile().isSeascape() ? 4 : 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    /* JADX INFO: renamed from: onSwipeInteractionCompleted */
    public void lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState targetState, int logAction) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(targetState, logAction);
        if (this.mStartState == LauncherState.NORMAL && targetState == LauncherState.OVERVIEW) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).onOverviewShown(true, TAG);
        }
    }
}
