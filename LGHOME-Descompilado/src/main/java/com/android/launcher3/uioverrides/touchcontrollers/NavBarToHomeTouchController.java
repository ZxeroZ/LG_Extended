package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ValueAnimator;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.util.AssistantUtilities;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class NavBarToHomeTouchController implements TouchController, SingleAxisSwipeDetector.Listener {
    private static final Interpolator PULLBACK_INTERPOLATOR = Interpolators.DEACCEL_3;
    private AnimatorPlaybackController mCurrentAnimation;
    private LauncherState mEndState = LauncherState.NORMAL;
    private final Launcher mLauncher;
    private boolean mNoIntercept;
    private final float mPullbackDistance;
    private LauncherState mStartState;
    private final SingleAxisSwipeDetector mSwipeDetector;

    public NavBarToHomeTouchController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mSwipeDetector = new SingleAxisSwipeDetector(launcher, this, SingleAxisSwipeDetector.VERTICAL);
        this.mPullbackDistance = launcher.getResources().getDimension(R.dimen.home_pullback_distance);
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mStartState = (LauncherState) this.mLauncher.getStateManager().getState();
            boolean z = !canInterceptTouch(ev);
            this.mNoIntercept = z;
            if (z) {
                return false;
            }
            this.mSwipeDetector.setDetectableScrollConditions(1, false);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(ev);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    private boolean canInterceptTouch(MotionEvent ev) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NavBarToHomeTouchController.canInterceptTouch " + ev);
        }
        if (!((ev.getEdgeFlags() & 256) != 0)) {
            return false;
        }
        if (this.mStartState.overviewUi || this.mStartState == LauncherState.ALL_APPS) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NavBarToHomeTouchController.canInterceptTouch true 1 " + this.mStartState.overviewUi + " " + (this.mStartState == LauncherState.ALL_APPS));
            }
            return true;
        }
        if (AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, FeatureFlags.ENABLE_ALL_APPS_EDU.get() ? 3583 : AbstractFloatingView.TYPE_ALL) != null) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NavBarToHomeTouchController.canInterceptTouch true 2 " + AbstractFloatingView.getTopOpenView(this.mLauncher), new Exception());
            }
            return true;
        }
        if (!FeatureFlags.ASSISTANT_GIVES_LAUNCHER_FOCUS.get() || !AssistantUtilities.isExcludedAssistantRunning()) {
            return false;
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NavBarToHomeTouchController.canInterceptTouch true 3");
        }
        return true;
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerTouchEvent(MotionEvent ev) {
        return this.mSwipeDetector.onTouchEvent(ev);
    }

    private float getShiftRange() {
        return this.mLauncher.getDeviceProfile().heightPx;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        initCurrentAnimation();
    }

    private void initCurrentAnimation() {
        long shiftRange = (long) (getShiftRange() * 2.0f);
        PendingAnimation pendingAnimation = new PendingAnimation(shiftRange);
        if (this.mStartState.overviewUi) {
            final RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
            pendingAnimation.setFloat(recentsView, RecentsView.ADJACENT_PAGE_OFFSET, (-this.mPullbackDistance) / recentsView.getPageOffsetScale(), PULLBACK_INTERPOLATOR);
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                pendingAnimation.addOnFrameCallback(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NavBarToHomeTouchController$ZlFF7Vf04WUrfs4zvwzXloWT_PU
                    @Override // java.lang.Runnable
                    public final void run() {
                        recentsView.redrawLiveTile(false);
                    }
                });
            }
        } else if (this.mStartState == LauncherState.ALL_APPS && !this.mLauncher.getAllAppsHost().isInTransition()) {
            AllAppsTransitionController allAppsController = this.mLauncher.getAllAppsController();
            FloatProperty<AllAppsTransitionController> floatProperty = AllAppsTransitionController.ALL_APPS_PROGRESS;
            float shiftRange2 = (-this.mPullbackDistance) / allAppsController.getShiftRange();
            Interpolator interpolator = PULLBACK_INTERPOLATOR;
            pendingAnimation.setFloat(allAppsController, floatProperty, shiftRange2, interpolator);
            StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
            stateAnimationConfig.duration = shiftRange;
            stateAnimationConfig.setInterpolator(10, Interpolators.mapToProgress(interpolator, 0.0f, 0.5f));
            allAppsController.setAlphas(this.mEndState, stateAnimationConfig, pendingAnimation);
        }
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            topOpenView.addHintCloseAnim(this.mPullbackDistance, PULLBACK_INTERPOLATOR, pendingAnimation);
        }
        this.mCurrentAnimation = pendingAnimation.createPlaybackController().setOnCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NavBarToHomeTouchController$xWf4jn6IoSAyy4lgMQPl-rf_V8Y
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.clearState();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearState() {
        this.mCurrentAnimation = null;
        this.mSwipeDetector.finishedScrolling();
        this.mSwipeDetector.setDetectableScrollConditions(0, false);
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement) {
        this.mCurrentAnimation.setPlayFraction(Utilities.getProgress(Math.min(0.0f, displacement), 0.0f, getShiftRange()));
        return true;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        boolean zIsFling = this.mSwipeDetector.isFling(velocity);
        int i = zIsFling ? 4 : 3;
        float progressFraction = this.mCurrentAnimation.getProgressFraction();
        if (PULLBACK_INTERPOLATOR.getInterpolation(progressFraction) >= 0.5f || (velocity < 0.0f && zIsFling)) {
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                final RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
                recentsView.switchToScreenshot(null, new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NavBarToHomeTouchController$ea71K-RJzom9hF4y1TOsQYokzfg
                    @Override // java.lang.Runnable
                    public final void run() {
                        recentsView.finishRecentsAnimation(true, null);
                    }
                });
            }
            this.mLauncher.getStateManager().goToState(this.mEndState, true, new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NavBarToHomeTouchController$PslxWr9A7bqG7b0w3AfnLXMDGRE
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onDragEnd$2$NavBarToHomeTouchController();
                }
            });
            LauncherState launcherState = this.mStartState;
            if (launcherState != this.mEndState) {
                logStateChange(launcherState.containerType, i);
            }
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
            if (topOpenView != null) {
                AbstractFloatingView.closeAllOpenViews(this.mLauncher, Boolean.valueOf(!this.mLauncher.isInState(LauncherState.ALL_APPS) || this.mLauncher.getWorkspace().getOpenFolder() == null).booleanValue());
                logStateChange(topOpenView.getLogContainerType(), i);
            }
            ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
            return;
        }
        ValueAnimator animationPlayer = this.mCurrentAnimation.getAnimationPlayer();
        animationPlayer.setFloatValues(progressFraction, 0.0f);
        animationPlayer.addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NavBarToHomeTouchController$h4E_zfvoQSXrjOZihv5BOsaLQEY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDragEnd$3$NavBarToHomeTouchController();
            }
        }));
        animationPlayer.setDuration(80L).start();
    }

    public /* synthetic */ void lambda$onDragEnd$2$NavBarToHomeTouchController() {
        onSwipeInteractionCompleted(this.mEndState);
    }

    public /* synthetic */ void lambda$onDragEnd$3$NavBarToHomeTouchController() {
        onSwipeInteractionCompleted(this.mStartState);
    }

    private void onSwipeInteractionCompleted(LauncherState targetState) {
        clearState();
        this.mLauncher.getStateManager().goToState(targetState, false);
        AccessibilityManagerCompat.sendStateEventToTest(this.mLauncher, targetState.ordinal);
    }

    private void logStateChange(int startContainerType, int logAction) {
        this.mLauncher.getUserEventDispatcher().logStateChangeAction(logAction, 1, this.mSwipeDetector.getDownX(), this.mSwipeDetector.getDownY(), 11, startContainerType, this.mEndState.containerType, this.mLauncher.getWorkspace().getCurrentPage());
        this.mLauncher.getStatsLogManager().logger().withSrcState(StatsLogManager.containerTypeToAtomState(this.mStartState.containerType)).withDstState(StatsLogManager.containerTypeToAtomState(this.mEndState.containerType)).log(StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE);
    }
}
