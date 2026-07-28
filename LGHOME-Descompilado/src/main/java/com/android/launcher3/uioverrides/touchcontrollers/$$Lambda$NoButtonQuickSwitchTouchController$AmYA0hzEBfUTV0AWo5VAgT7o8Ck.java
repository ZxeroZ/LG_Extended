package com.android.launcher3.uioverrides.touchcontrollers;

/* JADX INFO: renamed from: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonQuickSwitchTouchController$AmYA0hzEBfUTV0AWo5VAgT7o8Ck, reason: invalid class name */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$NoButtonQuickSwitchTouchController$AmYA0hzEBfUTV0AWo5VAgT7o8Ck implements Runnable {
    public final /* synthetic */ NoButtonQuickSwitchTouchController f$0;

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController.onAnimationToStateCompleted(com.android.launcher3.LauncherState, int):void, com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController.updateNonOverviewAnim(com.android.launcher3.LauncherState, com.android.launcher3.states.StateAnimationConfig):void] */
    public /* synthetic */ $$Lambda$NoButtonQuickSwitchTouchController$AmYA0hzEBfUTV0AWo5VAgT7o8Ck(NoButtonQuickSwitchTouchController noButtonQuickSwitchTouchController) {
        this.f$0 = noButtonQuickSwitchTouchController;
    }

    /* JADX DEBUG: Class process forced to load method for inline: com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController.lambda$AmYA0hzEBfUTV0AWo5VAgT7o8Ck(com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController):void */
    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.clearState();
    }
}
