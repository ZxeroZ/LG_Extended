package com.android.quickstep.util;

import android.content.SharedPreferences;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.views.AllAppsEduView;

/* JADX INFO: loaded from: classes.dex */
public class QuickstepOnboardingPrefs extends OnboardingPrefs<BaseQuickstepLauncher> {
    public QuickstepOnboardingPrefs(final BaseQuickstepLauncher launcher, SharedPreferences sharedPrefs) {
        super(launcher, sharedPrefs);
        final StateManager<LauncherState> stateManager = launcher.getStateManager();
        if (!getBoolean("launcher.apps_view_shown")) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.util.QuickstepOnboardingPrefs.1
                /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionComplete(LauncherState finalState) {
                    boolean z = SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(QuickstepOnboardingPrefs.this.mLauncher).getMode().hasGestures;
                    LauncherState launcherState = (LauncherState) stateManager.getLastState();
                    if ((z && finalState == LauncherState.OVERVIEW) || ((!z && finalState == LauncherState.ALL_APPS && launcherState == LauncherState.NORMAL) || QuickstepOnboardingPrefs.this.hasReachedMaxCount("launcher.home_bounce_count"))) {
                        QuickstepOnboardingPrefs.this.mSharedPrefs.edit().putBoolean("launcher.apps_view_shown", true).apply();
                        stateManager.removeStateListener(this);
                    }
                }
            });
        }
        boolean z = getBoolean("launcher.shelf_bounce_seen");
        if (!z && FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(launcher)) {
            z = true;
            this.mSharedPrefs.edit().putBoolean("launcher.shelf_bounce_seen", true).apply();
        }
        if (!z) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.util.QuickstepOnboardingPrefs.2
                /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionComplete(LauncherState finalState) {
                    LauncherState launcherState = (LauncherState) stateManager.getLastState();
                    if ((finalState == LauncherState.ALL_APPS && launcherState == LauncherState.OVERVIEW) || QuickstepOnboardingPrefs.this.hasReachedMaxCount("launcher.shelf_bounce_count")) {
                        QuickstepOnboardingPrefs.this.mSharedPrefs.edit().putBoolean("launcher.shelf_bounce_seen", true).apply();
                        stateManager.removeStateListener(this);
                    }
                }
            });
        }
        if (!hasReachedMaxCount(OnboardingPrefs.ALL_APPS_COUNT)) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.util.QuickstepOnboardingPrefs.3
                /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionComplete(LauncherState finalState) {
                    if (finalState == LauncherState.ALL_APPS && QuickstepOnboardingPrefs.this.incrementEventCount(OnboardingPrefs.ALL_APPS_COUNT)) {
                        stateManager.removeStateListener(this);
                    }
                }
            });
        }
        if (FeatureFlags.ENABLE_HYBRID_HOTSEAT.get() && !hasReachedMaxCount(OnboardingPrefs.HOTSEAT_DISCOVERY_TIP_COUNT)) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.util.QuickstepOnboardingPrefs.4
                boolean mFromAllApps = false;

                /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionComplete(LauncherState finalState) {
                }

                /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionStart(LauncherState toState) {
                    this.mFromAllApps = ((BaseQuickstepLauncher) QuickstepOnboardingPrefs.this.mLauncher).getStateManager().getCurrentStableState() == LauncherState.ALL_APPS;
                }
            });
        }
        if (SysUINavigationMode.getMode(launcher) == SysUINavigationMode.Mode.NO_BUTTON && FeatureFlags.ENABLE_ALL_APPS_EDU.get()) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.util.QuickstepOnboardingPrefs.5
                private static final int MAX_NUM_SWIPES_TO_TRIGGER_EDU = 3;
                private int mCount = 0;
                private boolean mShouldIncreaseCount;

                /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionStart(LauncherState toState) {
                    if (toState == LauncherState.NORMAL) {
                        return;
                    }
                    this.mShouldIncreaseCount = toState == LauncherState.HINT_STATE && launcher.getWorkspace().getNextPage() == 0;
                }

                /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
                @Override // com.android.launcher3.statemanager.StateManager.StateListener
                public void onStateTransitionComplete(LauncherState finalState) {
                    AllAppsEduView allAppsEduView;
                    if (finalState == LauncherState.NORMAL) {
                        if (this.mCount >= 3) {
                            if (AbstractFloatingView.getOpenView(QuickstepOnboardingPrefs.this.mLauncher, 512) == null) {
                                AllAppsEduView.show(launcher);
                            }
                            this.mCount = 0;
                            return;
                        }
                        return;
                    }
                    if (this.mShouldIncreaseCount && finalState == LauncherState.HINT_STATE) {
                        this.mCount++;
                    } else {
                        this.mCount = 0;
                    }
                    if (finalState != LauncherState.ALL_APPS || (allAppsEduView = (AllAppsEduView) AbstractFloatingView.getOpenView(QuickstepOnboardingPrefs.this.mLauncher, 512)) == null) {
                        return;
                    }
                    allAppsEduView.close(false);
                }
            });
        }
    }
}
