package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityManagerWrapper;

/* JADX INFO: loaded from: classes.dex */
public class QuickSwitchTouchController extends AbstractStateChangeTouchController {
    protected final RecentsView mOverviewPanel;

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getLogContainerTypeForNormalState(MotionEvent ev) {
        return 11;
    }

    public QuickSwitchTouchController(Launcher launcher) {
        this(launcher, SingleAxisSwipeDetector.HORIZONTAL);
    }

    protected QuickSwitchTouchController(Launcher l, SingleAxisSwipeDetector.Direction dir) {
        super(l, dir);
        this.mOverviewPanel = (RecentsView) l.getOverviewPanel();
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (this.mCurrentAnimation != null) {
            return true;
        }
        return this.mLauncher.isInState(LauncherState.NORMAL) && (ev.getEdgeFlags() & 256) != 0;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        if ((SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) != 0) {
            return LauncherState.NORMAL;
        }
        return isDragTowardPositive ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        super.onDragStart(start, startDisplacement);
        this.mStartContainerType = 11;
        ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    /* JADX INFO: renamed from: onSwipeInteractionCompleted */
    public void lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState targetState, int logAction) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(targetState, logAction);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animComponents) {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        setupInterpolators(stateAnimationConfig);
        stateAnimationConfig.duration = (long) (getShiftRange() * 2.0f);
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, stateAnimationConfig).setOnCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$QuickSwitchTouchController$7kFI-eXr7qTiIQVUgTpfvyoZyVo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.clearState();
            }
        });
        this.mCurrentAnimation.getAnimationPlayer().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$QuickSwitchTouchController$vp-8RjuE4gekVbOoRyR1PkPogRY
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$initCurrentAnimation$0$QuickSwitchTouchController(valueAnimator);
            }
        });
        return 1.0f / getShiftRange();
    }

    public /* synthetic */ void lambda$initCurrentAnimation$0$QuickSwitchTouchController(ValueAnimator valueAnimator) {
        updateFullscreenProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setupInterpolators(StateAnimationConfig stateAnimationConfig) {
        stateAnimationConfig.setInterpolator(3, Interpolators.DEACCEL_2);
        stateAnimationConfig.setInterpolator(10, Interpolators.DEACCEL_2);
        if (SysUINavigationMode.getMode(this.mLauncher) == SysUINavigationMode.Mode.NO_BUTTON) {
            stateAnimationConfig.setInterpolator(2, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(0, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(6, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(8, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(9, Interpolators.INSTANT);
            return;
        }
        stateAnimationConfig.setInterpolator(2, Interpolators.LINEAR);
        stateAnimationConfig.setInterpolator(0, Interpolators.LINEAR);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected void updateProgress(float progress) {
        super.updateProgress(progress);
        updateFullscreenProgress(Utilities.boundToRange(progress, 0.0f, 1.0f));
    }

    private void updateFullscreenProgress(float progress) {
        TaskView taskViewAt;
        this.mOverviewPanel.setFullscreenProgress(progress);
        int sysUiStatusNavFlags = 0;
        if (progress > 0.85f && (taskViewAt = this.mOverviewPanel.getTaskViewAt(0)) != null) {
            sysUiStatusNavFlags = taskViewAt.getThumbnail().getSysUiStatusNavFlags();
        }
        this.mLauncher.getSystemUiController().updateUiState(4, sysUiStatusNavFlags);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float getShiftRange() {
        return this.mLauncher.getDeviceProfile().widthPx / 2.0f;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getDirectionForLog() {
        return Utilities.isRtl(this.mLauncher.getResources()) ? 3 : 4;
    }
}
