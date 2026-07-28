package com.android.launcher3;

import com.android.launcher3.Workspace;

/* JADX INFO: compiled from: WorkspaceStateTransitionAnimation.java */
/* JADX INFO: loaded from: classes.dex */
class TransitionStates {
    final boolean allAppsToWorkspace;
    final boolean oldStateIsNormal;
    final boolean oldStateIsNormalHidden;
    final boolean oldStateIsOverview;
    final boolean oldStateIsOverviewHidden;
    final boolean oldStateIsSpringLoaded;
    final boolean overviewToAllApps;
    final boolean overviewToWorkspace;
    final boolean stateIsNormal;
    final boolean stateIsNormalHidden;
    final boolean stateIsOverview;
    final boolean stateIsOverviewHidden;
    final boolean stateIsSpringLoaded;
    final boolean workspaceToAllApps;
    final boolean workspaceToOverview;

    public TransitionStates(final Workspace.State fromState, final Workspace.State toState) {
        boolean z = fromState == Workspace.State.NORMAL;
        this.oldStateIsNormal = z;
        this.oldStateIsSpringLoaded = fromState == Workspace.State.SPRING_LOADED;
        this.oldStateIsNormalHidden = fromState == Workspace.State.NORMAL_HIDDEN;
        this.oldStateIsOverviewHidden = fromState == Workspace.State.OVERVIEW_HIDDEN;
        boolean z2 = fromState == Workspace.State.OVERVIEW;
        this.oldStateIsOverview = z2;
        boolean z3 = toState == Workspace.State.NORMAL;
        this.stateIsNormal = z3;
        this.stateIsSpringLoaded = toState == Workspace.State.SPRING_LOADED;
        boolean z4 = toState == Workspace.State.NORMAL_HIDDEN;
        this.stateIsNormalHidden = z4;
        boolean z5 = toState == Workspace.State.OVERVIEW_HIDDEN;
        this.stateIsOverviewHidden = z5;
        boolean z6 = toState == Workspace.State.OVERVIEW;
        this.stateIsOverview = z6;
        this.workspaceToOverview = z && z6;
        this.workspaceToAllApps = z && z4;
        this.overviewToWorkspace = z2 && z3;
        this.overviewToAllApps = z2 && z5;
        this.allAppsToWorkspace = z4 && z3;
    }
}
