package com.android.launcher3.uioverrides.states;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.LoggerUtils;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class OverviewState extends LauncherState {
    protected static final Rect sTempRect = new Rect();
    private static final int STATE_FLAGS = (((((FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED | 2) | FLAG_OVERVIEW_UI) | FLAG_WORKSPACE_INACCESSIBLE) | FLAG_CLOSE_POPUPS) | FLAG_USE_BLUR) | FLAG_HIDE_FREEFORM_POPUPS;

    @Override // com.android.launcher3.LauncherState
    protected float getDepthUnchecked(Context context) {
        return 1.0f;
    }

    @Override // com.android.launcher3.LauncherState
    public float getOverviewScrimAlpha(Launcher launcher) {
        return 0.5f;
    }

    public OverviewState(int id) {
        this(id, STATE_FLAGS);
    }

    protected OverviewState(int id, int stateFlags) {
        this(id, 12, stateFlags);
    }

    protected OverviewState(int id, int logContainer, int stateFlags) {
        super(id, logContainer, 380, stateFlags);
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return ((SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getMode() == SysUINavigationMode.Mode.NO_BUTTON) && FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) ? 380 : 250;
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        Workspace workspace = launcher.getWorkspace();
        View pageAt = workspace != null ? workspace.getPageAt(workspace.getCurrentPage()) : null;
        int width = (pageAt == null || pageAt.getWidth() == 0) ? launcher.getDeviceProfile().availableWidthPx : pageAt.getWidth();
        recentsView.getTaskSize(sTempRect);
        return new LauncherState.ScaleAndTranslation(r2.width() / width, 0.0f, (-getDefaultSwipeHeight(launcher)) * 0.5f);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        if ((getVisibleElements(launcher) & 1) != 0) {
            DeviceProfile deviceProfile = launcher.getDeviceProfile();
            if (deviceProfile.allAppsIconSizePx >= deviceProfile.iconSizePx) {
                return new LauncherState.ScaleAndTranslation(1.0f, 0.0f, 0.0f);
            }
            float f = deviceProfile.allAppsIconSizePx / deviceProfile.iconSizePx;
            return new LauncherState.ScaleAndTranslation(f, 0.0f, ((deviceProfile.heightPx / 2) - deviceProfile.hotseatBarBottomPaddingPx) * (1.0f - f));
        }
        return getWorkspaceScaleAndTranslation(launcher);
    }

    @Override // com.android.launcher3.LauncherState
    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return new float[]{1.0f, 0.0f};
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getQsbScaleAndTranslation(Launcher launcher) {
        if (this == OVERVIEW && FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(launcher)) {
            return getHotseatScaleAndTranslation(launcher);
        }
        return super.getQsbScaleAndTranslation(launcher);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        return new LauncherState.PageAlphaProvider(Interpolators.DEACCEL_2) { // from class: com.android.launcher3.uioverrides.states.OverviewState.1
            @Override // com.android.launcher3.LauncherState.PageAlphaProvider
            public float getPageAlpha(int pageIndex) {
                return 0.0f;
            }
        };
    }

    @Override // com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        if ((FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(launcher)) || (recentsView != null && SysUINavigationMode.hideShelfInTwoButtonLandscape(launcher, recentsView.getPagedOrientationHandler()))) {
            if (recentsView != null ? recentsView.isVisibleClearAllButton() : false) {
                return LauncherAnimUtils.ALL_APPS_TRANSITION_MS;
            }
            return 256;
        }
        if (launcher.getDeviceProfile().isVerticalBarLayout()) {
            return 96;
        }
        return (launcher.getAppsView() != null ? 8 : 1) | 98;
    }

    @Override // com.android.launcher3.LauncherState
    public float getVerticalProgress(Launcher launcher) {
        if ((getVisibleElements(launcher) & 8) == 0) {
            return super.getVerticalProgress(launcher);
        }
        return getDefaultVerticalProgress(launcher);
    }

    public static float getDefaultVerticalProgress(Launcher launcher) {
        return 1.0f - (getDefaultSwipeHeight(launcher) / launcher.getAllAppsController().getShiftRange());
    }

    @Override // com.android.launcher3.LauncherState
    public String getDescription(Launcher launcher) {
        return launcher.getString(R.string.accessibility_recent_apps);
    }

    public static float getDefaultSwipeHeight(Launcher launcher) {
        return LayoutUtils.getDefaultSwipeHeight(launcher, launcher.getDeviceProfile());
    }

    @Override // com.android.launcher3.LauncherState
    public void onBackPressed(Launcher launcher) {
        TaskView runningTaskView = ((RecentsView) launcher.getOverviewPanel()).getRunningTaskView();
        if (runningTaskView != null) {
            launcher.getUserEventDispatcher().logActionCommand(1, LoggerUtils.newContainerTarget(6));
            runningTaskView.launchTaskAnimated();
        } else {
            super.onBackPressed(launcher);
        }
    }

    public static OverviewState newBackgroundState(int id) {
        return new BackgroundAppState(id);
    }

    public static OverviewState newPeekState(int id) {
        return new OverviewPeekState(id);
    }

    public static OverviewState newSwitchState(int id) {
        return new QuickSwitchState(id);
    }

    public static OverviewState newModalTaskState(int id) {
        return new OverviewModalTaskState(id);
    }

    public static OverviewState newSplitSelectState(int id) {
        return new SplitScreenSelectState(id);
    }
}
