package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.uioverrides.states.OverviewState;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class PortraitStatesTouchController extends AbstractStateChangeTouchController {
    protected static final float ALL_APPS_CONTENT_FADE_THRESHOLD = 0.08f;
    private static final float RECENTS_FADE_THRESHOLD = 0.88f;
    private static final String TAG = "PortraitStatesTouchCtrl";
    private final InterpolatorWrapper mAllAppsInterpolatorWrapper;
    private final boolean mAllowDragToOverview;
    public int mBoundOfHomeKey;
    private boolean mFinishFastOnSecondTouch;
    private final PortraitOverviewStateTouchHelper mOverviewPortraitStateTouchHelper;
    protected boolean mSwipedFromHomeKey;
    private boolean mSwipedFromHotseat;

    protected int updateAnimComponentsOnReinit(int animComponents) {
        return animComponents;
    }

    public PortraitStatesTouchController(Launcher l, boolean allowDragToOverview) {
        super(l, SingleAxisSwipeDetector.VERTICAL);
        this.mAllAppsInterpolatorWrapper = new InterpolatorWrapper();
        this.mSwipedFromHotseat = false;
        this.mSwipedFromHomeKey = false;
        this.mBoundOfHomeKey = 0;
        this.mOverviewPortraitStateTouchHelper = new PortraitOverviewStateTouchHelper(l);
        this.mAllowDragToOverview = allowDragToOverview;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (ev.getAction() == 0) {
            int homekeyBound = WindowUtils.getHomekeyBound(this.mLauncher);
            this.mBoundOfHomeKey = homekeyBound;
            LGLog.d(TAG, "bound of Home key = " + homekeyBound);
        }
        if (ev.getRawY() >= this.mBoundOfHomeKey) {
            this.mSwipedFromHomeKey = true;
        } else {
            this.mSwipedFromHomeKey = false;
        }
        if (this.mCurrentAnimation != null) {
            SysUINavigationMode.Mode mode = this.mLauncher != null ? SysUINavigationMode.getMode(this.mLauncher) : null;
            if (SysUINavigationMode.Mode.NO_BUTTON == mode) {
                if (this.mCurrentAnimation.getAnimationPlayer() != null) {
                    this.mCurrentAnimation.getAnimationPlayer().end();
                    this.mCurrentAnimation = null;
                }
                if (this.mAtomicAnim != null) {
                    this.mAtomicAnim.end();
                }
            } else if (SysUINavigationMode.Mode.TWO_BUTTONS == mode) {
                if (this.mAtomicAnim != null) {
                    this.mAtomicAnim.end();
                }
                if (this.mCurrentAnimation.getAnimationPlayer() != null) {
                    this.mCurrentAnimation.getAnimationPlayer().end();
                    this.mCurrentAnimation = null;
                }
            }
            finishAtomicComponentsController();
            return false;
        }
        if (this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            if (this.mLauncher.getAppsView() != null && !this.mLauncher.getAppsView().shouldContainerScroll(ev)) {
                return false;
            }
        } else if (this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            if (!this.mOverviewPortraitStateTouchHelper.canInterceptTouch(ev)) {
                return false;
            }
        } else {
            if (this.mLauncher.isInState(LauncherState.NORMAL) && this.mLauncher.getWorkspace() != null && (this.mLauncher.getWorkspace().getState() != Workspace.State.NORMAL || (this.mLauncher.hasCustomContentToLeft() && this.mLauncher.getWorkspace().getCurrentPage() == 0 && !this.mSwipedFromHomeKey))) {
                return false;
            }
            if (!(this.mLauncher.isInState(LauncherState.NORMAL) && !this.mAllowDragToOverview) && !isTouchOverHotseat(this.mLauncher, ev)) {
                this.mSwipedFromHotseat = false;
                if (!this.mLauncher.isInState(LauncherState.NORMAL)) {
                    return false;
                }
            } else {
                this.mSwipedFromHotseat = true;
            }
        }
        return AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, AbstractFloatingView.TYPE_ACCESSIBLE) == null;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        boolean z = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || !LGHomeFeature.isEnableDefaultHome();
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.OVERIEW_NOT_ALLAPPS, "PortraitStatesTouchController.getTargetState");
        }
        if (fromState == LauncherState.ALL_APPS && !isDragTowardPositive) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.OVERIEW_NOT_ALLAPPS, "PortraitStatesTouchController.getTargetState 1");
            }
            return TouchInteractionService.isConnected() ? (LauncherState) this.mLauncher.getStateManager().getLastState() : LauncherState.NORMAL;
        }
        if (fromState == LauncherState.OVERVIEW) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.OVERIEW_NOT_ALLAPPS, "PortraitStatesTouchController.getTargetState 2");
            }
            LauncherState launcherState = LauncherState.ALL_APPS;
            if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(this.mLauncher)) {
                launcherState = LauncherState.OVERVIEW;
            }
            return isDragTowardPositive ? launcherState : LauncherState.NORMAL;
        }
        if (fromState != LauncherState.NORMAL || !isDragTowardPositive) {
            return (fromState != LauncherState.NORMAL || isDragTowardPositive || this.mLauncher.getWorkspace() == null || !this.mLauncher.getWorkspace().getInAppsEnabled()) ? fromState : LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue() ? (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() || !LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue()) ? fromState : ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || HomeSettingsSharedPreferences.getSwipeDownHome(this.mLauncher.getApplicationContext()) != Utilities.SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH) && !(LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && HomeSettingsSharedPreferences.getSwipeDownSwivelHome(this.mLauncher.getApplicationContext()) == Utilities.SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH)) ? fromState : LauncherState.INAPPS : (!LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue() && HomeSettingsSharedPreferences.getGoogleInAppsEnabled(this.mLauncher.getApplicationContext())) ? LauncherState.INAPPS : fromState;
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.OVERIEW_NOT_ALLAPPS, "PortraitStatesTouchController.getTargetState 3");
        }
        int lastSystemUiStateFlags = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags();
        if (this.mAllowDragToOverview && TouchInteractionService.isConnected() && this.mSwipedFromHomeKey && (lastSystemUiStateFlags & 128) == 0) {
            return LauncherState.OVERVIEW;
        }
        return z ? LauncherState.ALL_APPS : LauncherState.NORMAL;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected int getLogContainerTypeForNormalState(MotionEvent ev) {
        return isTouchOverHotseat(this.mLauncher, ev) ? 2 : 1;
    }

    private StateAnimationConfig getNormalToOverviewAnimation() {
        this.mAllAppsInterpolatorWrapper.baseInterpolator = Interpolators.LINEAR;
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.setInterpolator(0, this.mAllAppsInterpolatorWrapper);
        return stateAnimationConfig;
    }

    private static StateAnimationConfig getOverviewToAllAppsAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.setInterpolator(10, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, ALL_APPS_CONTENT_FADE_THRESHOLD));
        stateAnimationConfig.setInterpolator(9, Interpolators.clampToProgress(Interpolators.DEACCEL, RECENTS_FADE_THRESHOLD, 1.0f));
        return stateAnimationConfig;
    }

    private StateAnimationConfig getAllAppsToOverviewAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.setInterpolator(10, Interpolators.clampToProgress(Interpolators.DEACCEL, 0.92f, 1.0f));
        stateAnimationConfig.setInterpolator(9, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.120000005f));
        return stateAnimationConfig;
    }

    private StateAnimationConfig getNormalToAllAppsAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            stateAnimationConfig.setInterpolator(10, Interpolators.FAST_OUT_SLOW_IN);
            stateAnimationConfig.setInterpolator(1, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(2, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(3, Interpolators.DEACCEL_3);
        } else {
            stateAnimationConfig.setInterpolator(10, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, ALL_APPS_CONTENT_FADE_THRESHOLD));
        }
        return stateAnimationConfig;
    }

    private StateAnimationConfig getAllAppsToNormalAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            stateAnimationConfig.setInterpolator(10, Interpolators.FAST_OUT_SLOW_IN);
            stateAnimationConfig.setInterpolator(1, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(2, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(3, Interpolators.DEACCEL_3);
        } else {
            stateAnimationConfig.setInterpolator(10, Interpolators.clampToProgress(Interpolators.DEACCEL, 0.92f, 1.0f));
        }
        return stateAnimationConfig;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected StateAnimationConfig getConfigForStates(LauncherState fromState, LauncherState toState) {
        if (fromState == LauncherState.NORMAL && toState == LauncherState.OVERVIEW) {
            return getNormalToOverviewAnimation();
        }
        if (fromState == LauncherState.OVERVIEW && toState == LauncherState.ALL_APPS) {
            return getOverviewToAllAppsAnimation();
        }
        if (fromState == LauncherState.ALL_APPS && toState == LauncherState.OVERVIEW) {
            return getAllAppsToOverviewAnimation();
        }
        if (fromState == LauncherState.NORMAL && toState == LauncherState.ALL_APPS) {
            return getNormalToAllAppsAnimation();
        }
        if (fromState == LauncherState.ALL_APPS && toState == LauncherState.NORMAL) {
            return getAllAppsToNormalAnimation();
        }
        return new StateAnimationConfig();
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animFlags) {
        StateAnimationConfig configForStates;
        float shiftRange = getShiftRange();
        long j = (long) (2.0f * shiftRange);
        float verticalProgress = (this.mToState.getVerticalProgress(this.mLauncher) * shiftRange) - (this.mFromState.getVerticalProgress(this.mLauncher) * shiftRange);
        if (verticalProgress == 0.0f) {
            configForStates = new StateAnimationConfig();
        } else {
            configForStates = getConfigForStates(this.mFromState, this.mToState);
        }
        configForStates.animFlags = updateAnimComponentsOnReinit(animFlags);
        configForStates.duration = j;
        cancelPendingAnim();
        if (this.mFromState == LauncherState.OVERVIEW && this.mToState == LauncherState.NORMAL && this.mOverviewPortraitStateTouchHelper.shouldSwipeDownReturnToApp()) {
            this.mLauncher.getStateManager().goToState(LauncherState.OVERVIEW, false);
            this.mPendingAnimation = this.mOverviewPortraitStateTouchHelper.createSwipeDownToTaskAppAnimation(j, Interpolators.LINEAR);
            this.mCurrentAnimation = this.mPendingAnimation.createPlaybackController().setOnCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$PortraitStatesTouchController$wEHFdGsBQrelVBsf4RAKU717KaI
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$initCurrentAnimation$0$PortraitStatesTouchController();
                }
            });
            this.mLauncher.getStateManager().setCurrentUserControlledAnimation(this.mCurrentAnimation);
            verticalProgress = LayoutUtils.getShelfTrackingDistance(this.mLauncher, this.mLauncher.getDeviceProfile(), ((RecentsView) this.mLauncher.getOverviewPanel()).getPagedOrientationHandler());
        } else {
            this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, configForStates).setOnCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$PortraitStatesTouchController$kJAMsQ9OjriewgltpTxexMEqZsU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.clearState();
                }
            });
        }
        if (verticalProgress == 0.0f) {
            verticalProgress = Math.signum(this.mFromState.ordinal - this.mToState.ordinal) * OverviewState.getDefaultSwipeHeight(this.mLauncher);
        }
        return 1.0f / verticalProgress;
    }

    public /* synthetic */ void lambda$initCurrentAnimation$0$PortraitStatesTouchController() {
        cancelPendingAnim();
        clearState();
    }

    private void cancelPendingAnim() {
        if (this.mPendingAnimation != null) {
            this.mPendingAnimation.finish(false, 3);
            this.mPendingAnimation = null;
        }
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected void updateSwipeCompleteAnimation(ValueAnimator animator, long expectedDuration, LauncherState targetState, float velocity, boolean isFling) {
        super.updateSwipeCompleteAnimation(animator, expectedDuration, targetState, velocity, isFling);
        handleFirstSwipeToOverview(animator, expectedDuration, targetState, velocity, isFling);
    }

    private void handleFirstSwipeToOverview(final ValueAnimator animator, final long expectedDuration, final LauncherState targetState, final float velocity, final boolean isFling) {
        if (FeatureFlags.UNSTABLE_SPRINGS.get() && this.mFromState == LauncherState.OVERVIEW && this.mToState == LauncherState.ALL_APPS && targetState == LauncherState.OVERVIEW) {
            this.mFinishFastOnSecondTouch = true;
            return;
        }
        if (this.mFromState == LauncherState.NORMAL && this.mToState == LauncherState.OVERVIEW && targetState == LauncherState.OVERVIEW) {
            this.mFinishFastOnSecondTouch = true;
            if (!isFling || expectedDuration == 0) {
                return;
            }
            float progressFraction = this.mCurrentAnimation.getProgressFraction();
            if (progressFraction >= 1.0f) {
                progressFraction = 0.0f;
            }
            this.mAllAppsInterpolatorWrapper.baseInterpolator = Interpolators.clampToProgress(Interpolators.overshootInterpolatorForVelocity(velocity), progressFraction, 1.0f);
            animator.setDuration(Math.min(expectedDuration, this.ATOMIC_DURATION)).setInterpolator(Interpolators.LINEAR);
            return;
        }
        this.mFinishFastOnSecondTouch = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    /* JADX INFO: renamed from: onSwipeInteractionCompleted */
    public void lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState targetState, int logAction) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(targetState, logAction);
        if (this.mStartState == LauncherState.NORMAL && targetState == LauncherState.OVERVIEW) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).onOverviewShown(true, TAG);
        }
        if ((this.mStartState == LauncherState.NORMAL || (this.mStartState instanceof InAppsState) || this.mStartState == LauncherState.ALL_APPS) && (targetState instanceof InAppsState) && HomeSettingsSharedPreferences.getGoogleInAppsEnabled(this.mLauncher)) {
            InAppsState.enterInApps(this.mLauncher);
        }
    }

    static boolean isTouchOverHotseat(Launcher launcher, MotionEvent ev) {
        return ev.getY() >= ((float) getHotseatTop(launcher));
    }

    public static int getHotseatTop(Launcher launcher) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        return launcher.getDragLayer().getHeight() - (deviceProfile.hotseatBarSizePx + deviceProfile.getInsets().bottom);
    }

    private static class InterpolatorWrapper implements Interpolator {
        public TimeInterpolator baseInterpolator;

        private InterpolatorWrapper() {
            this.baseInterpolator = Interpolators.LINEAR;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float v) {
            return this.baseInterpolator.getInterpolation(v);
        }
    }
}
