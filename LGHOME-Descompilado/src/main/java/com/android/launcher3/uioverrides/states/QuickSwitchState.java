package com.android.launcher3.uioverrides.states;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;

/* JADX INFO: loaded from: classes.dex */
public class QuickSwitchState extends BackgroundAppState {
    @Override // com.android.launcher3.uioverrides.states.BackgroundAppState, com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return 0;
    }

    public QuickSwitchState(int id) {
        super(id, 13);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(1.0f, 0.0f, (getVerticalProgress(launcher) - NORMAL.getVerticalProgress(launcher)) * launcher.getAllAppsController().getShiftRange());
    }
}
