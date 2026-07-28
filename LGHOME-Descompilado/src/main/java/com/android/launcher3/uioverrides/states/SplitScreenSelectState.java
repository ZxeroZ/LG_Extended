package com.android.launcher3.uioverrides.states;

import com.android.launcher3.Launcher;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class SplitScreenSelectState extends OverviewState {
    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return 1024;
    }

    public SplitScreenSelectState(int id) {
        super(id);
    }

    @Override // com.android.launcher3.LauncherState
    public float getSplitSelectTranslation(Launcher launcher) {
        return ((RecentsView) launcher.getOverviewPanel()).getSplitSelectTranslation();
    }
}
