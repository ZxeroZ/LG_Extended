package com.android.launcher3.uioverrides.states;

import android.content.Context;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class BackgroundAppState extends OverviewState {
    private static final int STATE_FLAGS = (((FLAG_OVERVIEW_UI | 2) | FLAG_WORKSPACE_INACCESSIBLE) | 1) | FLAG_CLOSE_POPUPS;

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    protected float getDepthUnchecked(Context context) {
        return 1.0f;
    }

    @Override // com.android.launcher3.LauncherState
    public float getOverviewFullscreenProgress() {
        return 1.0f;
    }

    public BackgroundAppState(int id) {
        this(id, 12);
    }

    protected BackgroundAppState(int id, int logContainer) {
        super(id, logContainer, STATE_FLAGS);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public float getVerticalProgress(Launcher launcher) {
        if (launcher.getDeviceProfile().isVerticalBarLayout()) {
            return super.getVerticalProgress(launcher);
        }
        return super.getVerticalProgress(launcher) + (LayoutUtils.getShelfTrackingDistance(launcher, launcher.getDeviceProfile(), ((RecentsView) launcher.getOverviewPanel()).getPagedOrientationHandler()) / Math.max(launcher.getAllAppsController().getShiftRange(), 1.0f));
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return getOverviewScaleAndOffsetForBackgroundState(launcher);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return super.getVisibleElements(launcher) & (-65) & (-33) & (-257);
    }

    @Override // com.android.launcher3.uioverrides.states.OverviewState, com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        if ((getVisibleElements(launcher) & 1) != 0) {
            RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
            LauncherState.ScaleAndTranslation hotseatScaleAndTranslation = super.getHotseatScaleAndTranslation(launcher);
            hotseatScaleAndTranslation.translationY += LayoutUtils.getShelfTrackingDistance(launcher, launcher.getDeviceProfile(), recentsView.getPagedOrientationHandler());
            return hotseatScaleAndTranslation;
        }
        return super.getHotseatScaleAndTranslation(launcher);
    }

    public static float[] getOverviewScaleAndOffsetForBackgroundState(BaseDraggingActivity activity) {
        return new float[]{((RecentsView) activity.getOverviewPanel()).getMaxScaleForFullScreen(), 0.0f};
    }
}
