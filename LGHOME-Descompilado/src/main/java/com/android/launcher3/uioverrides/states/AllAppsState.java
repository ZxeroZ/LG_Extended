package com.android.launcher3.uioverrides.states;

import android.content.Context;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsState extends LauncherState {
    private static final float TRANSITION_Y = -50.0f;
    private static final int STATE_FLAGS = (FLAG_WORKSPACE_INACCESSIBLE | FLAG_CLOSE_POPUPS) | FLAG_USE_BLUR;
    private static final LauncherState.PageAlphaProvider PAGE_ALPHA_PROVIDER = new LauncherState.PageAlphaProvider(Interpolators.DEACCEL_2) { // from class: com.android.launcher3.uioverrides.states.AllAppsState.1
        @Override // com.android.launcher3.LauncherState.PageAlphaProvider
        public float getPageAlpha(int pageIndex) {
            return 0.0f;
        }
    };

    @Override // com.android.launcher3.LauncherState
    protected float getDepthUnchecked(Context context) {
        return 1.0f;
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return LauncherAnimUtils.ALL_APPS_TRANSITION_MS;
    }

    @Override // com.android.launcher3.LauncherState
    public float getVerticalProgress(Launcher launcher) {
        return 0.0f;
    }

    @Override // com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return 28;
    }

    public AllAppsState(int id) {
        super(id, 4, LauncherAnimUtils.ALL_APPS_TRANSITION_MS, STATE_FLAGS);
    }

    @Override // com.android.launcher3.LauncherState
    public String getDescription(Launcher launcher) {
        return launcher.getAppsView().getDescription();
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        LauncherState.ScaleAndTranslation workspaceScaleAndTranslation;
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            workspaceScaleAndTranslation = new LauncherState.ScaleAndTranslation(1.0f, 0.0f, TRANSITION_Y);
        } else {
            workspaceScaleAndTranslation = LauncherState.OVERVIEW.getWorkspaceScaleAndTranslation(launcher);
        }
        if (SysUINavigationMode.getMode(launcher) == SysUINavigationMode.Mode.NO_BUTTON && !FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) {
            workspaceScaleAndTranslation.scale = (workspaceScaleAndTranslation.scale + 1.0f) / 2.0f;
        } else {
            workspaceScaleAndTranslation.scale = 1.0f;
        }
        return workspaceScaleAndTranslation;
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        return PAGE_ALPHA_PROVIDER;
    }

    @Override // com.android.launcher3.LauncherState
    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return new float[]{0.9f, 0.0f};
    }

    /* JADX DEBUG: Method merged with bridge method: getHistoryForState(Lcom/android/launcher3/statemanager/BaseState;)Lcom/android/launcher3/statemanager/BaseState; */
    @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
    public LauncherState getHistoryForState(LauncherState previousState) {
        return previousState == OVERVIEW ? OVERVIEW : NORMAL;
    }
}
