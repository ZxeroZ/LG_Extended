package com.android.launcher3.states;

import android.content.Context;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;

/* JADX INFO: loaded from: classes.dex */
public class SpringLoadedState extends LauncherState {
    private static final int STATE_FLAGS = (((((FLAG_MULTI_PAGE | FLAG_DISABLE_ACCESSIBILITY) | 2) | FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED) | FLAG_DISABLE_PAGE_CLIPPING) | FLAG_PAGE_BACKGROUNDS) | FLAG_HIDE_BACK_BUTTON;

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return 150;
    }

    @Override // com.android.launcher3.LauncherState
    public float getWorkspaceScrimAlpha(Launcher launcher) {
        return 0.3f;
    }

    @Override // com.android.launcher3.LauncherState
    public void onStateDisabled(final Launcher launcher) {
    }

    public SpringLoadedState(int id) {
        super(id, 6, 150, STATE_FLAGS);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        Workspace workspace = launcher.getWorkspace();
        if (workspace.getChildCount() == 0) {
            return super.getWorkspaceScaleAndTranslation(launcher);
        }
        if (deviceProfile.isVerticalBarLayout()) {
            return new LauncherState.ScaleAndTranslation(deviceProfile.workspaceSpringLoadShrinkFactor, 0.0f, 0.0f);
        }
        float f = deviceProfile.workspaceSpringLoadShrinkFactor;
        float f2 = launcher.getDragLayer().getInsets().top + deviceProfile.dropTargetBarSizePx;
        float measuredHeight = f2 + ((((((workspace.getMeasuredHeight() - r8.bottom) - deviceProfile.workspacePadding.bottom) - deviceProfile.workspaceSpringLoadedBottomSpace) - f2) - (workspace.getNormalChildHeight() * f)) / 2.0f);
        float height = workspace.getHeight() / 2;
        return new LauncherState.ScaleAndTranslation(f, 0.0f, (measuredHeight - ((workspace.getTop() + height) - ((height - workspace.getChildAt(0).getTop()) * f))) / f);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(1.0f, 0.0f, 0.0f);
    }

    @Override // com.android.launcher3.LauncherState
    public void onStateEnabled(Launcher launcher) {
        launcher.getWorkspace();
    }
}
