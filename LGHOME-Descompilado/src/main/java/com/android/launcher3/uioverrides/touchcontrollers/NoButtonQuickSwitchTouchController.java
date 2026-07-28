package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.BothAxesSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.VibratorWrapper;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.ShelfPeekAnim;
import com.android.quickstep.util.StaggeredWorkspaceAnim;
import com.android.quickstep.views.LauncherRecentsView;
import com.android.quickstep.views.RecentsView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class NoButtonQuickSwitchTouchController implements TouchController, BothAxesSwipeDetector.Listener, MotionPauseDetector.OnMotionPauseListener {
    private static final String TAG = "NoButtonQuickSwitchTouchController";
    private static final float Y_ANIM_MIN_PROGRESS = 0.15f;
    private boolean mIsHomeScreenVisible = true;
    private final BaseQuickstepLauncher mLauncher;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private boolean mNoIntercept;
    private AnimatorPlaybackController mNonOverviewAnim;
    private final LauncherRecentsView mRecentsView;
    private final ShelfPeekAnim mShelfPeekAnim;
    private LauncherState mStartState;
    private final BothAxesSwipeDetector mSwipeDetector;
    private AnimatorPlaybackController mXOverviewAnim;
    private final float mXRange;
    private AnimatorPlaybackController mYOverviewAnim;
    private final float mYRange;
    private static final Interpolator FADE_OUT_INTERPOLATOR = Interpolators.DEACCEL_5;
    private static final Interpolator TRANSLATE_OUT_INTERPOLATOR = Interpolators.ACCEL_0_75;
    private static final Interpolator SCALE_DOWN_INTERPOLATOR = Interpolators.DEACCEL;

    public NoButtonQuickSwitchTouchController(BaseQuickstepLauncher launcher) {
        this.mLauncher = launcher;
        this.mSwipeDetector = new BothAxesSwipeDetector(launcher, this);
        this.mShelfPeekAnim = launcher.getShelfPeekAnim();
        this.mRecentsView = (LauncherRecentsView) launcher.getOverviewPanel();
        this.mXRange = launcher.getDeviceProfile().widthPx / 2.0f;
        this.mYRange = LayoutUtils.getShelfTrackingDistance(launcher, launcher.getDeviceProfile(), r0.getPagedOrientationHandler());
        this.mMotionPauseDetector = new MotionPauseDetector(launcher);
        this.mMotionPauseMinDisplacement = launcher.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            boolean z = !canInterceptTouch(ev);
            this.mNoIntercept = z;
            if (z) {
                return false;
            }
            this.mSwipeDetector.setDetectableScrollConditions(2, false);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(ev);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        return this.mSwipeDetector.onTouchEvent(ev);
    }

    private boolean canInterceptTouch(MotionEvent ev) {
        return this.mLauncher.isInState(LauncherState.NORMAL) && (this.mLauncher.getWorkspace() == null || this.mLauncher.getWorkspace().getState() == Workspace.State.NORMAL) && AbstractFloatingView.getOpenView(this.mLauncher, 1) == null && (ev.getEdgeFlags() & 256) != 0 && (SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) == 0;
    }

    @Override // com.android.launcher3.touch.BothAxesSwipeDetector.Listener
    public void onDragStart(boolean start) {
        this.mMotionPauseDetector.clear();
        if (start) {
            this.mStartState = (LauncherState) this.mLauncher.getStateManager().getState();
            this.mMotionPauseDetector.setOnMotionPauseListener(this);
            this.mSwipeDetector.setDetectableScrollConditions(3, false);
            setupAnimators();
        }
    }

    @Override // com.android.quickstep.util.MotionPauseDetector.OnMotionPauseListener
    public void onMotionPauseChanged(boolean isPaused) {
        if (this.mLauncher != null) {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, this.mLauncher.getRootView());
        }
        if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) {
            return;
        }
        ShelfPeekAnim.ShelfAnimState shelfAnimState = isPaused ? ShelfPeekAnim.ShelfAnimState.PEEK : ShelfPeekAnim.ShelfAnimState.HIDE;
        if (shelfAnimState == ShelfPeekAnim.ShelfAnimState.PEEK) {
            this.mLauncher.getAllAppsController().setAlphas(LauncherState.NORMAL, new StateAnimationConfig(), PropertySetter.NO_ANIM_PROPERTY_SETTER);
            if ((LauncherState.OVERVIEW.getVisibleElements(this.mLauncher) & 1) != 0) {
                this.mLauncher.getHotseat().setAlpha(1.0f);
            }
        }
        this.mShelfPeekAnim.setShelfState(shelfAnimState, ShelfPeekAnim.INTERPOLATOR, 240L);
    }

    private void setupAnimators() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        Interpolator interpolator = FADE_OUT_INTERPOLATOR;
        stateAnimationConfig.setInterpolator(3, interpolator);
        stateAnimationConfig.setInterpolator(10, interpolator);
        Interpolator interpolator2 = TRANSLATE_OUT_INTERPOLATOR;
        stateAnimationConfig.setInterpolator(2, interpolator2);
        stateAnimationConfig.setInterpolator(0, interpolator2);
        updateNonOverviewAnim(LauncherState.QUICK_SWITCH, stateAnimationConfig);
        this.mNonOverviewAnim.dispatchOnStart();
        if (this.mRecentsView.getTaskViewCount() == 0) {
            this.mRecentsView.setOnEmptyMessageUpdatedListener(new RecentsView.OnEmptyMessageUpdatedListener() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonQuickSwitchTouchController$biupeyjepd3xJoPfonCUbMI-Zss
                @Override // com.android.quickstep.views.RecentsView.OnEmptyMessageUpdatedListener
                public final void onEmptyMessageUpdated(boolean z) {
                    this.f$0.lambda$setupAnimators$0$NoButtonQuickSwitchTouchController(z);
                }
            });
        }
        setupOverviewAnimators();
    }

    public /* synthetic */ void lambda$setupAnimators$0$NoButtonQuickSwitchTouchController(boolean z) {
        if (z || !this.mSwipeDetector.isDraggingState()) {
            return;
        }
        setupOverviewAnimators();
    }

    private void updateNonOverviewAnim(LauncherState toState, StateAnimationConfig config) {
        config.duration = (long) (Math.max(this.mXRange, this.mYRange) * 2.0f);
        config.animFlags |= 8;
        this.mNonOverviewAnim = this.mLauncher.getStateManager().createAnimationToNewWorkspace(toState, config).setOnCancelRunnable(new $$Lambda$NoButtonQuickSwitchTouchController$AmYA0hzEBfUTV0AWo5VAgT7o8Ck(this));
    }

    private void setupOverviewAnimators() {
        LauncherState launcherState = LauncherState.QUICK_SWITCH;
        LauncherState launcherState2 = LauncherState.OVERVIEW;
        LauncherAnimUtils.SCALE_PROPERTY.set(this.mRecentsView, Float.valueOf(launcherState.getOverviewScaleAndOffset(this.mLauncher)[0]));
        RecentsView.ADJACENT_PAGE_OFFSET.set(this.mRecentsView, Float.valueOf(1.0f));
        this.mRecentsView.setContentAlpha(1.0f);
        this.mRecentsView.setFullscreenProgress(launcherState.getOverviewFullscreenProgress());
        float[] overviewScaleAndOffset = launcherState2.getOverviewScaleAndOffset(this.mLauncher);
        PendingAnimation pendingAnimation = new PendingAnimation((long) (this.mXRange * 2.0f));
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_OFFSET, overviewScaleAndOffset[1], Interpolators.LINEAR);
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimation.createPlaybackController();
        this.mXOverviewAnim = animatorPlaybackControllerCreatePlaybackController;
        animatorPlaybackControllerCreatePlaybackController.dispatchOnStart();
        PendingAnimation pendingAnimation2 = new PendingAnimation((long) (this.mYRange * 2.0f));
        LauncherRecentsView launcherRecentsView = this.mRecentsView;
        FloatProperty<View> floatProperty = LauncherAnimUtils.SCALE_PROPERTY;
        float f = overviewScaleAndOffset[0];
        Interpolator interpolator = SCALE_DOWN_INTERPOLATOR;
        pendingAnimation2.setFloat(launcherRecentsView, floatProperty, f, interpolator);
        pendingAnimation2.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, launcherState2.getOverviewFullscreenProgress(), interpolator);
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController2 = pendingAnimation2.createPlaybackController();
        this.mYOverviewAnim = animatorPlaybackControllerCreatePlaybackController2;
        animatorPlaybackControllerCreatePlaybackController2.dispatchOnStart();
    }

    @Override // com.android.launcher3.touch.BothAxesSwipeDetector.Listener
    public boolean onDrag(PointF displacement, MotionEvent ev) {
        AnimatorPlaybackController animatorPlaybackController;
        float fMax = Math.max(0.0f, displacement.x) / this.mXRange;
        float fMapRange = Utilities.mapRange(Math.max(0.0f, -displacement.y) / this.mYRange, Y_ANIM_MIN_PROGRESS, 1.0f);
        boolean z = this.mIsHomeScreenVisible;
        if (z && (animatorPlaybackController = this.mNonOverviewAnim) != null) {
            animatorPlaybackController.setPlayFraction(fMax);
        }
        boolean z2 = FADE_OUT_INTERPOLATOR.getInterpolation(fMax) <= 0.99f;
        this.mIsHomeScreenVisible = z2;
        if (z && !z2) {
            this.mShelfPeekAnim.setShelfState(ShelfPeekAnim.ShelfAnimState.HIDE, Interpolators.LINEAR, 0L);
        }
        this.mMotionPauseDetector.setDisallowPause(this.mIsHomeScreenVisible || (-displacement.y) < this.mMotionPauseMinDisplacement);
        this.mMotionPauseDetector.addPosition(ev);
        if (this.mIsHomeScreenVisible) {
            this.mShelfPeekAnim.setShelfState(ShelfPeekAnim.ShelfAnimState.CANCEL, Interpolators.LINEAR, 0L);
        }
        AnimatorPlaybackController animatorPlaybackController2 = this.mXOverviewAnim;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.setPlayFraction(fMax);
        }
        AnimatorPlaybackController animatorPlaybackController3 = this.mYOverviewAnim;
        if (animatorPlaybackController3 != null) {
            animatorPlaybackController3.setPlayFraction(fMapRange);
        }
        return true;
    }

    @Override // com.android.launcher3.touch.BothAxesSwipeDetector.Listener
    public void onDragEnd(PointF velocity) {
        final LauncherState launcherState;
        float f;
        boolean zIsFling = this.mSwipeDetector.isFling(velocity.x);
        boolean zIsFling2 = this.mSwipeDetector.isFling(velocity.y);
        boolean z = (zIsFling || zIsFling2) ? false : true;
        final int i = z ? 3 : 4;
        if (this.mMotionPauseDetector.isPaused() && z) {
            cancelAnimations();
            Animator animatorCreateStateElementAnimation = this.mLauncher.createAtomicAnimationFactory().createStateElementAnimation(3, new float[0]);
            animatorCreateStateElementAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NoButtonQuickSwitchTouchController.this.lambda$onDragEnd$1$NoButtonQuickSwitchTouchController(LauncherState.OVERVIEW, i);
                }
            });
            animatorCreateStateElementAnimation.start();
            return;
        }
        if (zIsFling && zIsFling2) {
            if (velocity.x < 0.0f) {
                launcherState = LauncherState.NORMAL;
            } else {
                launcherState = (velocity.y <= 0.0f && Math.abs(velocity.x) <= Math.abs(velocity.y)) ? LauncherState.NORMAL : LauncherState.QUICK_SWITCH;
            }
        } else if (zIsFling) {
            launcherState = velocity.x > 0.0f ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
        } else if (zIsFling2) {
            launcherState = velocity.y > 0.0f ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
        } else {
            launcherState = (this.mXOverviewAnim.getInterpolatedProgress() > 0.5f ? 1 : (this.mXOverviewAnim.getInterpolatedProgress() == 0.5f ? 0 : -1)) > 0 ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
        }
        float fBoundToRange = Utilities.boundToRange(this.mXOverviewAnim.getProgressFraction() + ((velocity.x * DisplayController.getSingleFrameMs(this.mLauncher)) / this.mXRange), 0.0f, 1.0f);
        float f2 = launcherState == LauncherState.NORMAL ? 0.0f : 1.0f;
        long jCalculateDuration = BaseSwipeDetector.calculateDuration(velocity.x, Math.abs(f2 - fBoundToRange));
        ValueAnimator animationPlayer = this.mXOverviewAnim.getAnimationPlayer();
        animationPlayer.setFloatValues(fBoundToRange, f2);
        animationPlayer.setDuration(jCalculateDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity.x));
        this.mXOverviewAnim.dispatchOnStart();
        boolean z2 = zIsFling2 && velocity.y < 0.0f && launcherState == LauncherState.NORMAL;
        float fBoundToRange2 = Utilities.boundToRange(this.mYOverviewAnim.getProgressFraction() - ((velocity.y * DisplayController.getSingleFrameMs(this.mLauncher)) / this.mYRange), 0.0f, 1.0f);
        if (z2) {
            f = 1.0f;
        } else {
            f = launcherState == LauncherState.NORMAL ? fBoundToRange2 : 0.0f;
        }
        long jCalculateDuration2 = BaseSwipeDetector.calculateDuration(velocity.y, Math.abs(f - fBoundToRange2));
        ValueAnimator animationPlayer2 = this.mYOverviewAnim.getAnimationPlayer();
        animationPlayer2.setFloatValues(fBoundToRange2, f);
        animationPlayer2.setDuration(jCalculateDuration2);
        this.mYOverviewAnim.dispatchOnStart();
        ValueAnimator animationPlayer3 = this.mNonOverviewAnim.getAnimationPlayer();
        if (z2 && !this.mIsHomeScreenVisible) {
            StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
            stateAnimationConfig.animFlags = 0;
            updateNonOverviewAnim(launcherState, stateAnimationConfig);
            animationPlayer3 = this.mNonOverviewAnim.getAnimationPlayer();
            new StaggeredWorkspaceAnim(this.mLauncher, velocity.y, false).start();
        } else {
            boolean z3 = launcherState == LauncherState.NORMAL;
            if (z3) {
                this.mNonOverviewAnim.dispatchOnCancelWithoutCancelRunnable();
            }
            animationPlayer3.setFloatValues(this.mNonOverviewAnim.getProgressFraction(), z3 ? 0.0f : 1.0f);
            this.mNonOverviewAnim.dispatchOnStart();
        }
        animationPlayer3.setDuration(Math.max(jCalculateDuration, jCalculateDuration2));
        this.mNonOverviewAnim.setEndAction(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonQuickSwitchTouchController$C5DwD0-Atc97KVkGDDQChCIctE8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDragEnd$1$NoButtonQuickSwitchTouchController(launcherState, i);
            }
        });
        cancelAnimations();
        animationPlayer.start();
        animationPlayer2.start();
        animationPlayer3.start();
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$onDragEnd$1$NoButtonQuickSwitchTouchController(Lcom/android/launcher3/LauncherState;I)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: onAnimationToStateCompleted, reason: merged with bridge method [inline-methods] */
    public void lambda$onDragEnd$1$NoButtonQuickSwitchTouchController(LauncherState targetState, int logAction) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (this.mLauncher.getWorkspace() != null) {
            this.mLauncher.getUserEventDispatcher().logStateChangeAction(logAction, getDirectionForLog(), this.mSwipeDetector.getDownX(), this.mSwipeDetector.getDownY(), 11, this.mStartState.containerType, targetState.containerType, this.mLauncher.getWorkspace().getCurrentPage());
        } else {
            LGLog.w(TAG, "Launcher workspace is null.", new int[0]);
        }
        StatsLogManager.StatsLogger statsLoggerWithDstState = this.mLauncher.getStatsLogManager().logger().withSrcState(2).withDstState(StatsLogManager.containerTypeToAtomState(targetState.containerType));
        int i = this.mStartState.containerType;
        int i2 = targetState.containerType;
        if (targetState.ordinal > this.mStartState.ordinal) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEUP;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEDOWN;
        }
        statsLoggerWithDstState.log(StatsLogManager.getLauncherAtomEvent(i, i2, launcherEvent));
        this.mLauncher.getStateManager().goToState(targetState, false, (Runnable) new $$Lambda$NoButtonQuickSwitchTouchController$AmYA0hzEBfUTV0AWo5VAgT7o8Ck(this));
    }

    private int getDirectionForLog() {
        return Utilities.isRtl(this.mLauncher.getResources()) ? 3 : 4;
    }

    private void cancelAnimations() {
        AnimatorPlaybackController animatorPlaybackController = this.mNonOverviewAnim;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().cancel();
        }
        AnimatorPlaybackController animatorPlaybackController2 = this.mXOverviewAnim;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.getAnimationPlayer().cancel();
        }
        AnimatorPlaybackController animatorPlaybackController3 = this.mYOverviewAnim;
        if (animatorPlaybackController3 != null) {
            animatorPlaybackController3.getAnimationPlayer().cancel();
        }
        this.mShelfPeekAnim.setShelfState(ShelfPeekAnim.ShelfAnimState.CANCEL, Interpolators.LINEAR, 0L);
        this.mMotionPauseDetector.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearState() {
        cancelAnimations();
        this.mNonOverviewAnim = null;
        this.mXOverviewAnim = null;
        this.mYOverviewAnim = null;
        this.mIsHomeScreenVisible = true;
        this.mSwipeDetector.finishedScrolling();
        this.mRecentsView.setOnEmptyMessageUpdatedListener(null);
    }
}
