package com.android.quickstep;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherInitListener;
import com.android.launcher3.LauncherState;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.util.ShelfPeekAnim;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.plugins.shared.LauncherOverlayManager;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherActivityInterface extends BaseActivityInterface<LauncherState, BaseQuickstepLauncher> {
    public static final LauncherActivityInterface INSTANCE = new LauncherActivityInterface();

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean allowMinimizeSplitScreen() {
        return true;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target) {
        return homeBounds;
    }

    private LauncherActivityInterface() {
        super(true, LauncherState.OVERVIEW, LauncherState.BACKGROUND_APP);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context, Rect outRect, PagedOrientationHandler orientationHandler) {
        calculateTaskSize(context, dp, outRect, orientationHandler);
        if (dp.isVerticalBarLayout() && SysUINavigationMode.getMode(context) != SysUINavigationMode.Mode.NO_BUTTON) {
            Rect insets = dp.getInsets();
            return dp.hotseatBarSizePx + (dp.isSeascape() ? insets.left : insets.right);
        }
        return LayoutUtils.getShelfTrackingDistance(context, dp, orientationHandler);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onSwipeUpToRecentsComplete() {
        super.onSwipeUpToRecentsComplete();
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null) {
            DiscoveryBounce.showForOverviewIfNeeded(createdActivity, ((RecentsView) createdActivity.getOverviewPanel()).getPagedOrientationHandler());
        }
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onSwipeUpToHomeComplete(RecentsAnimationDeviceState deviceState) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.getStateManager().reapplyState();
        createdActivity.getRootView().setForceHideBackArrow(false);
        notifyRecentsOfOrientation(deviceState);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onAssistantVisibilityChanged(float visibility) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.onAssistantVisibilityChanged(visibility);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public BaseActivityInterface.AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState deviceState, boolean activityVisible, Consumer<AnimatorPlaybackController> callback) {
        notifyRecentsOfOrientation(deviceState);
        BaseActivityInterface<LauncherState, BaseQuickstepLauncher>.DefaultAnimationFactory defaultAnimationFactory = new BaseActivityInterface<LauncherState, BaseQuickstepLauncher>.DefaultAnimationFactory(callback) { // from class: com.android.quickstep.LauncherActivityInterface.1
            @Override // com.android.quickstep.BaseActivityInterface.AnimationFactory
            public void setShelfState(ShelfPeekAnim.ShelfAnimState shelfState, Interpolator interpolator, long duration) {
                ((BaseQuickstepLauncher) this.mActivity).getShelfPeekAnim().setShelfState(shelfState, interpolator, duration);
            }

            /* JADX DEBUG: Method merged with bridge method: createBackgroundToOverviewAnim(Lcom/android/launcher3/statemanager/StatefulActivity;Lcom/android/launcher3/anim/PendingAnimation;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.quickstep.BaseActivityInterface.DefaultAnimationFactory
            public void createBackgroundToOverviewAnim(BaseQuickstepLauncher activity, PendingAnimation pa) {
                super.createBackgroundToOverviewAnim(activity, pa);
                if (!activity.getDeviceProfile().isVerticalBarLayout() && SysUINavigationMode.getMode(activity) != SysUINavigationMode.Mode.NO_BUTTON) {
                    pa.add(activity.getStateManager().createStateElementAnimation(2, LauncherState.BACKGROUND_APP.getVerticalProgress(activity), LauncherState.OVERVIEW.getVerticalProgress(activity)));
                }
                float depth = LauncherState.BACKGROUND_APP.getDepth(activity);
                float depth2 = LauncherState.OVERVIEW.getDepth(activity);
                pa.addFloat(LauncherActivityInterface.this.getDepthController(), new DepthController.ClampedDepthProperty(depth, depth2), depth, depth2, Interpolators.LINEAR);
            }
        };
        ((BaseQuickstepLauncher) defaultAnimationFactory.initUI()).getAllAppsHost().getContentView().setVisibility(8);
        return defaultAnimationFactory;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public ActivityInitListener createActivityInitListener(final Predicate<Boolean> onInitListener) {
        return new LauncherInitListener(new BiPredicate() { // from class: com.android.quickstep.-$$Lambda$LauncherActivityInterface$gxea7Uz9d621AnaH-OOC77wffKs
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                return onInitListener.test((Boolean) obj2);
            }
        });
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void setOnDeferredActivityLaunchCallback(Runnable r) {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.setOnDeferredActivityLaunchCallback(r);
    }

    /* JADX DEBUG: Method merged with bridge method: getCreatedActivity()Lcom/android/launcher3/statemanager/StatefulActivity; */
    @Override // com.android.quickstep.BaseActivityInterface
    public BaseQuickstepLauncher getCreatedActivity() {
        return (BaseQuickstepLauncher) BaseQuickstepLauncher.ACTIVITY_TRACKER.getCreatedActivity();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public DepthController getDepthController() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return null;
        }
        return createdActivity.getDepthController();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public RecentsView getVisibleRecentsView() {
        Launcher visibleLauncher = getVisibleLauncher();
        if (visibleLauncher == null || !((LauncherState) visibleLauncher.getStateManager().getState()).overviewUi) {
            return null;
        }
        return (RecentsView) visibleLauncher.getOverviewPanel();
    }

    private Launcher getVisibleLauncher() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity != null && createdActivity.isStarted() && createdActivity.hasWindowFocus()) {
            return createdActivity;
        }
        return null;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean switchToRecentsIfVisible(Runnable onCompleteCallback) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.OVERIEW_NOT_ALLAPPS, "switchToRecentsIfVisible");
        }
        Launcher visibleLauncher = getVisibleLauncher();
        if (visibleLauncher == null) {
            return false;
        }
        visibleLauncher.getUserEventDispatcher().logActionCommand(6, getContainerType(), 12);
        visibleLauncher.getStateManager().goToState(LauncherState.OVERVIEW, visibleLauncher.getStateManager().shouldAnimateStateChange(), onCompleteCallback);
        return true;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onExitOverview(final RecentsAnimationDeviceState deviceState, final Runnable exitRunnable) {
        final StateManager<LauncherState> stateManager = getCreatedActivity().getStateManager();
        stateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.quickstep.LauncherActivityInterface.2
            /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
            @Override // com.android.launcher3.statemanager.StateManager.StateListener
            public void onStateTransitionComplete(LauncherState toState) {
                if (toState == LauncherState.NORMAL || toState == LauncherState.ALL_APPS) {
                    exitRunnable.run();
                    LauncherActivityInterface.this.notifyRecentsOfOrientation(deviceState);
                    stateManager.removeStateListener(this);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyRecentsOfOrientation(RecentsAnimationDeviceState deviceState) {
        ((RecentsView) getCreatedActivity().getOverviewPanel()).setLayoutRotation(deviceState.getCurrentActiveRotation(), deviceState.getDisplayRotation());
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void updateOverviewPredictionState() {
        if (getCreatedActivity() == null) {
        }
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public int getContainerType() {
        Launcher visibleLauncher = getVisibleLauncher();
        if (visibleLauncher != null) {
            return ((LauncherState) visibleLauncher.getStateManager().getState()).containerType;
        }
        return 13;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean isInLiveTileMode() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.getStateManager().getState() == LauncherState.OVERVIEW && createdActivity.isStarted();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onLaunchTaskFailed() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.getStateManager().goToState(LauncherState.OVERVIEW);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void closeOverlay() {
        BaseQuickstepLauncher createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        LauncherOverlayManager overlayManager = createdActivity.getOverlayManager();
        if (!createdActivity.isStarted() || createdActivity.isForceInvisible()) {
            overlayManager.hideOverlay(false);
        } else {
            overlayManager.hideOverlay(150);
        }
    }

    @Override // com.android.quickstep.BaseActivityInterface
    protected float getExtraSpace(Context context, DeviceProfile dp, PagedOrientationHandler orientationHandler) {
        return dp.hotseatBarSizePx + dp.verticalDragHandleSizePx;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean shouldCancelCurrentGesture() {
        return super.shouldCancelCurrentGesture();
    }
}
