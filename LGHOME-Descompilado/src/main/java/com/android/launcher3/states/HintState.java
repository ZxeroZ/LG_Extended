package com.android.launcher3.states;

import android.content.Context;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;

/* JADX INFO: loaded from: classes.dex */
public class HintState extends LauncherState {
    private static final int STATE_FLAGS = ((FLAG_WORKSPACE_INACCESSIBLE | 2) | FLAG_HAS_SYS_UI_SCRIM) | FLAG_HIDE_BACK_BUTTON;

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return 80;
    }

    public HintState(int id) {
        super(id, 0, 80, STATE_FLAGS);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(0.9f, 0.0f, 0.0f);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getQsbScaleAndTranslation(Launcher launcher) {
        return getHotseatScaleAndTranslation(launcher);
    }
}
