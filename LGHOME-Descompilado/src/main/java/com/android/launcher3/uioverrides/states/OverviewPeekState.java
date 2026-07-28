package com.android.launcher3.uioverrides.states;

import com.android.launcher3.Launcher;

/* JADX INFO: loaded from: classes.dex */
public class OverviewPeekState extends OverviewState {
    private static final float OVERVIEW_OFFSET = 0.7f;

    public OverviewPeekState(int id) {
        super(id);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return new float[]{1.0f, 0.7f};
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return super.getVisibleElements(launcher) & (-129) & (-257);
    }
}
