package com.android.launcher3.uioverrides.states;

import android.content.Context;
import android.graphics.Rect;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class OverviewModalTaskState extends OverviewState {
    private static final int STATE_FLAGS = (FLAG_OVERVIEW_UI | 2) | FLAG_WORKSPACE_INACCESSIBLE;

    @Override // com.android.launcher3.LauncherState
    public float getOverviewModalness() {
        return 1.0f;
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context launcher) {
        return 300;
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return 64;
    }

    public OverviewModalTaskState(int id) {
        super(id, 6, STATE_FLAGS);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return getOverviewScaleAndOffsetForModalState(launcher);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public void onBackPressed(Launcher launcher) {
        launcher.getStateManager().goToState(LauncherState.OVERVIEW);
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        if (recentsView != null) {
            recentsView.resetModalVisuals();
        } else {
            super.onBackPressed(launcher);
        }
    }

    public static float[] getOverviewScaleAndOffsetForModalState(BaseDraggingActivity activity) {
        Rect rect = new Rect();
        ((RecentsView) activity.getOverviewPanel()).getTaskSize(rect);
        int iHeight = rect.height();
        ((RecentsView) activity.getOverviewPanel()).getModalTaskSize(rect);
        return new float[]{rect.height() / iHeight, 0.0f};
    }
}
