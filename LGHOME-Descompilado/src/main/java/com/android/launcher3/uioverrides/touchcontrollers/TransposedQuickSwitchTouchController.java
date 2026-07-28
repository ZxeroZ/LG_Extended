package com.android.launcher3.uioverrides.touchcontrollers;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.touch.SingleAxisSwipeDetector;

/* JADX INFO: loaded from: classes.dex */
public class TransposedQuickSwitchTouchController extends QuickSwitchTouchController {
    public TransposedQuickSwitchTouchController(Launcher launcher) {
        super(launcher, SingleAxisSwipeDetector.VERTICAL);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.QuickSwitchTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        return super.getTargetState(fromState, isDragTowardPositive ^ this.mLauncher.getDeviceProfile().isSeascape());
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.QuickSwitchTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animComponents) {
        float fInitCurrentAnimation = super.initCurrentAnimation(animComponents);
        return this.mLauncher.getDeviceProfile().isSeascape() ? fInitCurrentAnimation : -fInitCurrentAnimation;
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.QuickSwitchTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float getShiftRange() {
        return this.mLauncher.getDeviceProfile().heightPx / 2.0f;
    }
}
