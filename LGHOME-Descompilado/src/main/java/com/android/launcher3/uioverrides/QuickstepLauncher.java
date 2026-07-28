package com.android.launcher3.uioverrides;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHost;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.uioverrides.states.QuickstepAtomicAnimationFactory;
import com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.IntegratedSearchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.LandscapeEdgeSwipeController;
import com.android.launcher3.uioverrides.touchcontrollers.NavBarToHomeTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.NoButtonNavbarToOverviewTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.OverviewToAllAppsTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.QuickSwitchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.StatusBarTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.TransposedQuickSwitchTouchController;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.UiThreadHelper;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public class QuickstepLauncher extends BaseQuickstepLauncher {
    public static final boolean GO_LOW_RAM_RECENTS_ENABLED = false;
    public static final UiThreadHelper.AsyncCommand SET_SHELF_HEIGHT = new UiThreadHelper.AsyncCommand() { // from class: com.android.launcher3.uioverrides.-$$Lambda$QuickstepLauncher$v3E47r-lTMv8PsAbyA0BrjKhkzc
        @Override // com.android.launcher3.util.UiThreadHelper.AsyncCommand
        public final void execute(Context context, int i, int i2) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).setShelfHeight(i != 0, i2);
        }
    };

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher
    protected void setupViews() {
        super.setupViews();
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onStateOrResumeChanging(false);
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity
    /* JADX INFO: renamed from: startActivitySafely */
    public boolean lambda$startActivitySafely$4$Launcher(View v, Intent intent, ItemInfo item) {
        return super.lambda$startActivitySafely$4$Launcher(v, intent, item);
    }

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.BaseActivity
    protected void onActivityFlagsChanged(int changeBits) {
        super.onActivityFlagsChanged(changeBits);
        if ((changeBits & 85) != 0) {
            onStateOrResumeChanging((getActivityFlags() & 64) == 0);
        }
    }

    @Override // com.android.launcher3.Launcher
    public void folderCreatedFromItem(Folder folder, WorkspaceItemInfo itemInfo) {
        super.folderCreatedFromItem(folder, itemInfo);
    }

    @Override // com.android.launcher3.Launcher
    public void folderConvertedToItem(Folder folder, WorkspaceItemInfo itemInfo) {
        super.folderConvertedToItem(folder, itemInfo);
    }

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher
    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        return super.getSupportedShortcuts();
    }

    private void onStateOrResumeChanging(boolean inTransition) {
        LauncherState launcherState = (LauncherState) getStateManager().getState();
        DeviceProfile deviceProfile = getDeviceProfile();
        UiThreadHelper.runAsyncCommand(this, SET_SHELF_HEIGHT, (!(launcherState == LauncherState.NORMAL || launcherState == LauncherState.OVERVIEW) || !(((getActivityFlags() & 32) != 0) || isUserActive()) || deviceProfile.isVerticalBarLayout() || !deviceProfile.isPhone || deviceProfile.isLandscape) ? 0 : 1, deviceProfile.hotseatBarSizePx);
        if (launcherState != LauncherState.NORMAL || inTransition) {
            return;
        }
        ((RecentsView) getOverviewPanel()).setSwipeDownShouldLaunchApp(false);
    }

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    /* JADX DEBUG: Method merged with bridge method: onStateSetEnd(Lcom/android/launcher3/statemanager/BaseState;)V */
    @Override // com.android.launcher3.Launcher, com.android.launcher3.statemanager.StatefulActivity
    public void onStateSetEnd(LauncherState state) {
        super.onStateSetEnd(state);
        int i = state.ordinal;
        if (i == 2) {
            DiscoveryBounce.showForOverviewIfNeeded(this, ((RecentsView) getOverviewPanel()).getPagedOrientationHandler());
            RecentsView recentsView = (RecentsView) getOverviewPanel();
            AccessibilityManagerCompat.sendCustomAccessibilityEvent(recentsView.getPageAt(recentsView.getCurrentPage()), 8, null);
            return;
        }
        boolean z = false;
        if (i == 5) {
            TaskView taskViewAt = ((RecentsView) getOverviewPanel()).getTaskViewAt(0);
            if (taskViewAt != null) {
                taskViewAt.launchTask(new Consumer() { // from class: com.android.launcher3.uioverrides.-$$Lambda$QuickstepLauncher$vxaF0BR6XK2pGnN3XPB1m__XmUQ
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        this.f$0.lambda$onStateSetEnd$1$QuickstepLauncher((Boolean) obj);
                    }
                });
                return;
            } else {
                getStateManager().goToState(LauncherState.NORMAL);
                return;
            }
        }
        if (i != 8) {
            return;
        }
        final Workspace workspace = getWorkspace();
        if (workspace != null && workspace.getNextPage() != workspace.getDefaultPage()) {
            z = true;
        }
        getStateManager().goToState(LauncherState.NORMAL, !z, (Runnable) null);
        if (z) {
            Objects.requireNonNull(workspace);
            workspace.post(new Runnable() { // from class: com.android.launcher3.uioverrides.-$$Lambda$g_PWBejC70ihpe8wyanMHZpVuUc
                @Override // java.lang.Runnable
                public final void run() {
                    workspace.moveToDefaultScreen();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onStateSetEnd$1$QuickstepLauncher(Boolean bool) {
        if (!bool.booleanValue()) {
            getStateManager().goToState(LauncherState.OVERVIEW);
        } else {
            getStateManager().moveToRestState();
        }
    }

    @Override // com.android.launcher3.Launcher
    public TouchController[] createTouchControllers() {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "createTouchControllers.1");
        }
        SysUINavigationMode.Mode mode = SysUINavigationMode.getMode(this);
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).setTypeControllers(mode.resValue);
        LGLog.i("Launcher", "createTouchControllers : " + mode);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getDragController());
        if (mode == SysUINavigationMode.Mode.NO_BUTTON) {
            arrayList.add(new NoButtonQuickSwitchTouchController(this));
            arrayList.add(new NavBarToHomeTouchController(this));
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.PAUSE_NOT_DETECTED, "createTouchControllers.2");
            }
            if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) {
                if (TestProtocol.sDebugTracing) {
                    Log.d(TestProtocol.PAUSE_NOT_DETECTED, "createTouchControllers.3");
                }
                arrayList.add(new NoButtonNavbarToOverviewTouchController(this));
            } else {
                arrayList.add(new FlingAndHoldTouchController(this));
            }
        } else if (getDeviceProfile().isVerticalBarLayout()) {
            arrayList.add(new OverviewToAllAppsTouchController(this));
            arrayList.add(new LandscapeEdgeSwipeController(this));
            if (mode.hasGestures) {
                arrayList.add(new TransposedQuickSwitchTouchController(this));
            }
        } else {
            arrayList.add(new PortraitStatesTouchController(this, mode.hasGestures));
            if (mode.hasGestures) {
                arrayList.add(new QuickSwitchTouchController(this));
            }
        }
        if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue() && !getDeviceProfile().isMultiWindowMode) {
            arrayList.add(new StatusBarTouchController(this));
            arrayList.add(new IntegratedSearchTouchController(this));
        } else if (LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue() && !getDeviceProfile().isMultiWindowMode) {
            arrayList.add(new IntegratedSearchTouchController(this));
        }
        arrayList.add(new LauncherTaskViewController(this));
        return (TouchController[]) arrayList.toArray(new TouchController[arrayList.size()]);
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public StateManager.AtomicAnimationFactory createAtomicAnimationFactory() {
        return new QuickstepAtomicAnimationFactory(this);
    }

    @Override // com.android.launcher3.Launcher
    protected LauncherAppWidgetHost createAppWidgetHost() {
        LauncherAppWidgetHost launcherAppWidgetHostCreateAppWidgetHost = super.createAppWidgetHost();
        launcherAppWidgetHostCreateAppWidgetHost.setInteractionHandler(new QuickstepInteractionHandler(this));
        return launcherAppWidgetHostCreateAppWidgetHost;
    }

    private static final class LauncherTaskViewController extends TaskViewTouchController<Launcher> {
        LauncherTaskViewController(Launcher activity) {
            super(activity);
        }

        @Override // com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController
        protected boolean isRecentsInteractive() {
            return ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW) || ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK);
        }

        @Override // com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController
        protected boolean isRecentsModal() {
            return ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK);
        }

        @Override // com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController
        protected void onUserControlledAnimationCreated(AnimatorPlaybackController animController) {
            ((Launcher) this.mActivity).getStateManager().setCurrentUserControlledAnimation(animController);
        }
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.BaseActivity, android.app.Activity
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        RecentsView recentsView = (RecentsView) getOverviewPanel();
        writer.println("\nQuickstepLauncher:");
        writer.println(prefix + "\tmOrientationState: " + (recentsView == null ? "recentsNull" : recentsView.getPagedViewOrientedState()));
    }
}
