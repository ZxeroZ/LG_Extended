package com.android.launcher3.touch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.FlingBlockCheck;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractStateChangeTouchController implements TouchController, SingleAxisSwipeDetector.Listener {
    public static final float ATOMIC_OVERVIEW_ANIM_THRESHOLD = 0.5f;
    public static final float SUCCESS_TRANSITION_PROGRESS = 0.5f;
    private static final String TAG = "ASCTouchController";
    protected AnimatorSet mAtomicAnim;
    private AutoPlayAtomicAnimationInfo mAtomicAnimAutoPlayInfo;
    private AnimatorPlaybackController mAtomicComponentsController;
    private float mAtomicComponentsStartProgress;
    private boolean mCanBlockFling;
    protected AnimatorPlaybackController mCurrentAnimation;
    protected final SingleAxisSwipeDetector mDetector;
    private float mDisplacementShift;
    protected LauncherState mFromState;
    private boolean mIsLogContainerSet;
    protected final Launcher mLauncher;
    private boolean mNoIntercept;
    private boolean mPassedOverviewAtomicThreshold;
    protected PendingAnimation mPendingAnimation;
    private float mProgressMultiplier;
    private boolean mScheduleResumeAtomicComponent;
    protected int mStartContainerType;
    private float mStartProgress;
    protected LauncherState mStartState;
    protected final SingleAxisSwipeDetector.Direction mSwipeDirection;
    protected LauncherState mToState;
    protected final long ATOMIC_DURATION = getAtomicDuration();
    private FlingBlockCheck mFlingBlockCheck = new FlingBlockCheck();
    private LauncherState mAtomicComponentsTargetState = LauncherState.NORMAL;

    protected abstract boolean canInterceptTouch(MotionEvent ev);

    protected long getAtomicDuration() {
        return 200L;
    }

    protected abstract int getLogContainerTypeForNormalState(MotionEvent ev);

    protected abstract LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive);

    protected abstract float initCurrentAnimation(int animComponents);

    public AbstractStateChangeTouchController(Launcher l, SingleAxisSwipeDetector.Direction dir) {
        this.mLauncher = l;
        this.mDetector = new SingleAxisSwipeDetector(l, this, dir);
        this.mSwipeDirection = dir;
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        int swipeDirection;
        boolean z = true;
        if (ev.getPointerCount() > 1 || (this.mLauncher.getWorkspace() != null && this.mLauncher.getWorkspace().getState() != Workspace.State.NORMAL)) {
            this.mNoIntercept = true;
            return false;
        }
        if (ev.getAction() == 0) {
            boolean z2 = !canInterceptTouch(ev);
            this.mNoIntercept = z2;
            if (z2) {
                return false;
            }
            if (this.mCurrentAnimation != null) {
                swipeDirection = 3;
            } else {
                swipeDirection = getSwipeDirection();
                if (swipeDirection == 0) {
                    this.mNoIntercept = true;
                    return false;
                }
                z = false;
            }
            this.mDetector.setDetectableScrollConditions(swipeDirection, z);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(ev);
        return this.mDetector.isDraggingOrSettling();
    }

    private int getSwipeDirection() {
        LauncherState launcherState = (LauncherState) this.mLauncher.getStateManager().getState();
        int i = getTargetState(launcherState, true) == launcherState ? 0 : 1;
        return getTargetState(launcherState, false) != launcherState ? i | 2 : i;
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerTouchEvent(MotionEvent ev) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "onControllerTouchEvent");
        }
        return this.mDetector.onTouchEvent(ev);
    }

    protected float getShiftRange() {
        return this.mLauncher.getAllAppsController().getShiftRange();
    }

    private boolean reinitCurrentAnimation(boolean reachedToState, boolean isDragTowardPositive) {
        AnimatorPlaybackController animatorPlaybackController;
        LauncherState launcherState = this.mFromState;
        if (launcherState == null) {
            launcherState = (LauncherState) this.mLauncher.getStateManager().getState();
        } else if (reachedToState) {
            launcherState = this.mToState;
        }
        LauncherState targetState = getTargetState(launcherState, isDragTowardPositive);
        if ((targetState instanceof InAppsState) && (animatorPlaybackController = this.mCurrentAnimation) != null && animatorPlaybackController.getProgressFraction() >= 1.0f) {
            targetState = LauncherState.NORMAL;
        }
        if ((launcherState == this.mFromState && targetState == this.mToState) || launcherState == targetState) {
            return false;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && targetState == LauncherState.HINT_STATE && SysUINavigationMode.getMode(this.mLauncher) != SysUINavigationMode.Mode.NO_BUTTON && this.mLauncher.getDeviceProfile().isLandscape) {
            targetState = LauncherState.ALL_APPS;
        }
        this.mFromState = launcherState;
        this.mToState = targetState;
        LGLog.d(TAG, "[RecentsAnimation] reinitCurrentAnimation : mToState = " + targetState + ", mFromState = " + launcherState);
        this.mStartProgress = 0.0f;
        this.mPassedOverviewAtomicThreshold = false;
        AnimatorPlaybackController animatorPlaybackController2 = this.mCurrentAnimation;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.setOnCancelRunnable(null);
        }
        int i = goingBetweenNormalAndOverview(this.mFromState, this.mToState) ? 1 : 7;
        this.mScheduleResumeAtomicComponent = false;
        if (this.mAtomicAnim != null) {
            this.mScheduleResumeAtomicComponent = true;
            i = 1;
        }
        if (goingBetweenNormalAndOverview(this.mFromState, this.mToState) || this.mAtomicComponentsTargetState != this.mToState) {
            cancelAtomicComponentsController();
        }
        if (this.mAtomicComponentsController != null) {
            i &= -3;
        }
        this.mProgressMultiplier = initCurrentAnimation(i);
        this.mCurrentAnimation.dispatchOnStart();
        if (this.mToState == LauncherState.OVERVIEW) {
            if (Utilities.getCoverDisplayState() != 2) {
                Utilities.getCoverDisplayState();
            }
            this.mLauncher.sendBroadcast(new Intent(LauncherConst.ACTION_CONTROL_DUAL_RECENT).addFlags(16777216));
        }
        return true;
    }

    protected boolean goingBetweenNormalAndOverview(LauncherState fromState, LauncherState toState) {
        return (fromState == LauncherState.NORMAL || fromState == LauncherState.OVERVIEW) && (toState == LauncherState.NORMAL || toState == LauncherState.OVERVIEW) && this.mPendingAnimation == null;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        LauncherState launcherState = (LauncherState) this.mLauncher.getStateManager().getState();
        this.mStartState = launcherState;
        this.mIsLogContainerSet = false;
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController == null) {
            this.mFromState = launcherState;
            this.mToState = null;
            cancelAnimationControllers();
            reinitCurrentAnimation(false, this.mDetector.wasInitialTouchPositive());
            this.mDisplacementShift = 0.0f;
        } else {
            animatorPlaybackController.pause();
            this.mStartProgress = this.mCurrentAnimation.getProgressFraction();
            this.mAtomicAnimAutoPlayInfo = null;
            AnimatorPlaybackController animatorPlaybackController2 = this.mAtomicComponentsController;
            if (animatorPlaybackController2 != null) {
                animatorPlaybackController2.pause();
            }
        }
        this.mCanBlockFling = this.mFromState == LauncherState.NORMAL;
        this.mFlingBlockCheck.unblockFling();
        if (this.mToState == LauncherState.ALL_APPS || this.mToState == LauncherState.NORMAL) {
            this.mLauncher.getAllAppsController().onDragStart(this.mToState == LauncherState.ALL_APPS);
        }
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement) {
        float f = (this.mProgressMultiplier * (displacement - this.mDisplacementShift)) + this.mStartProgress;
        updateProgress(f);
        boolean zIsPositive = this.mSwipeDirection.isPositive(displacement - this.mDisplacementShift);
        if (f <= 0.0f) {
            if (reinitCurrentAnimation(false, zIsPositive)) {
                this.mDisplacementShift = displacement;
                if (this.mCanBlockFling) {
                    this.mFlingBlockCheck.blockFling();
                }
            }
        } else if (f >= 1.0f) {
            if (reinitCurrentAnimation(true, zIsPositive)) {
                this.mDisplacementShift = displacement;
                if (this.mCanBlockFling) {
                    this.mFlingBlockCheck.blockFling();
                }
            }
        } else {
            this.mFlingBlockCheck.onEvent();
        }
        return true;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement, MotionEvent ev) {
        if (!this.mIsLogContainerSet) {
            if (this.mStartState == LauncherState.ALL_APPS) {
                this.mStartContainerType = 4;
            } else if (this.mStartState == LauncherState.NORMAL) {
                this.mStartContainerType = getLogContainerTypeForNormalState(ev);
            } else if (this.mStartState == LauncherState.OVERVIEW) {
                this.mStartContainerType = 12;
            }
            this.mIsLogContainerSet = true;
        }
        return onDrag(displacement);
    }

    protected void updateProgress(float fraction) {
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.setPlayFraction(fraction);
        }
        if (this.mAtomicComponentsController != null) {
            float fMin = Math.min(this.mAtomicComponentsStartProgress, 0.9f);
            this.mAtomicComponentsController.setPlayFraction((fraction - fMin) / (1.0f - fMin));
        }
        maybeUpdateAtomicAnim(this.mFromState, this.mToState, fraction);
    }

    private void maybeUpdateAtomicAnim(LauncherState fromState, LauncherState toState, float progress) {
        if (goingBetweenNormalAndOverview(fromState, toState)) {
            LauncherState launcherState = LauncherState.OVERVIEW;
            boolean z = progress >= 0.5f;
            if (z != this.mPassedOverviewAtomicThreshold) {
                LauncherState launcherState2 = z ? fromState : toState;
                LauncherState launcherState3 = z ? toState : fromState;
                if (launcherState3 == LauncherState.NORMAL) {
                    launcherState3.skipAtomicAnim = true;
                }
                this.mPassedOverviewAtomicThreshold = z;
                AnimatorSet animatorSet = this.mAtomicAnim;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSetCreateAtomicAnimForState = createAtomicAnimForState(launcherState2, launcherState3, this.ATOMIC_DURATION);
                this.mAtomicAnim = animatorSetCreateAtomicAnimForState;
                animatorSetCreateAtomicAnimForState.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.touch.AbstractStateChangeTouchController.1
                    @Override // com.android.launcher3.anim.AnimationSuccessListener, android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        AbstractStateChangeTouchController.this.mAtomicAnim = null;
                        AbstractStateChangeTouchController.this.mScheduleResumeAtomicComponent = false;
                    }

                    @Override // com.android.launcher3.anim.AnimationSuccessListener
                    public void onAnimationSuccess(Animator animator) {
                        if (AbstractStateChangeTouchController.this.mScheduleResumeAtomicComponent) {
                            AbstractStateChangeTouchController.this.cancelAtomicComponentsController();
                            if (AbstractStateChangeTouchController.this.mCurrentAnimation != null) {
                                AbstractStateChangeTouchController abstractStateChangeTouchController = AbstractStateChangeTouchController.this;
                                abstractStateChangeTouchController.mAtomicComponentsStartProgress = abstractStateChangeTouchController.mCurrentAnimation.getProgressFraction();
                                long shiftRange = (long) (AbstractStateChangeTouchController.this.getShiftRange() * 2.0f);
                                AbstractStateChangeTouchController abstractStateChangeTouchController2 = AbstractStateChangeTouchController.this;
                                abstractStateChangeTouchController2.mAtomicComponentsController = AnimatorPlaybackController.wrap(abstractStateChangeTouchController2.createAtomicAnimForState(abstractStateChangeTouchController2.mFromState, AbstractStateChangeTouchController.this.mToState, shiftRange), shiftRange);
                                AbstractStateChangeTouchController.this.mAtomicComponentsController.dispatchOnStart();
                                AbstractStateChangeTouchController abstractStateChangeTouchController3 = AbstractStateChangeTouchController.this;
                                abstractStateChangeTouchController3.mAtomicComponentsTargetState = abstractStateChangeTouchController3.mToState;
                                AbstractStateChangeTouchController.this.maybeAutoPlayAtomicComponentsAnim();
                            }
                        }
                    }
                });
                this.mAtomicAnim.start();
                if (fromState != toState) {
                    this.mLauncher.getDragLayer().performHapticFeedback(1);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AnimatorSet createAtomicAnimForState(LauncherState fromState, LauncherState targetState, long duration) {
        StateAnimationConfig configForStates = getConfigForStates(fromState, targetState);
        configForStates.animFlags = 2;
        configForStates.duration = duration;
        return this.mLauncher.getStateManager().createAtomicAnimation(fromState, targetState, configForStates);
    }

    protected StateAnimationConfig getConfigForStates(LauncherState fromState, LauncherState toState) {
        return new StateAnimationConfig();
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        LauncherState launcherState;
        float fBoundToRange;
        float f;
        if (this.mCurrentAnimation == null) {
            LGLog.i(TAG, "onDragEnd : mCurrentAnimation is null. skip onDragEnd. velocity = " + velocity);
            return;
        }
        boolean zIsFling = this.mDetector.isFling(velocity);
        final int i = zIsFling ? 4 : 3;
        boolean z = zIsFling && this.mFlingBlockCheck.isBlocked();
        if (z) {
            zIsFling = false;
        }
        float progressFraction = this.mCurrentAnimation.getProgressFraction();
        float f2 = this.mProgressMultiplier * velocity;
        float interpolatedProgress = this.mCurrentAnimation.getInterpolatedProgress();
        if (zIsFling) {
            launcherState = Float.compare(Math.signum(velocity), Math.signum(this.mProgressMultiplier)) == 0 ? this.mToState : this.mFromState;
        } else {
            launcherState = interpolatedProgress > ((this.mToState == LauncherState.ALL_APPS || ((this.mToState instanceof InAppsState) && com.android.launcher3.Utilities.isIntegratedSearchBySwipingUpHome(this.mLauncher.getApplicationContext()))) ? 0.4f : 0.5f) ? this.mToState : this.mFromState;
        }
        final LauncherState launcherState2 = launcherState;
        int iBlockedFlingDurationFactor = (z && launcherState2 == this.mFromState) ? LauncherAnimUtils.blockedFlingDurationFactor(velocity) : 1;
        long jCalculateDuration = 0;
        if (launcherState2 != this.mToState) {
            this.mCurrentAnimation.dispatchOnCancelWithoutCancelRunnable();
            if (progressFraction <= 0.0f) {
                f = 0.0f;
                fBoundToRange = f;
            } else {
                fBoundToRange = com.android.launcher3.Utilities.boundToRange((f2 * DisplayController.getSingleFrameMs(this.mLauncher)) + progressFraction, 0.0f, 1.0f);
                jCalculateDuration = BaseSwipeDetector.calculateDuration(velocity, Math.min(progressFraction, 1.0f) - 0.0f) * ((long) iBlockedFlingDurationFactor);
                f = 0.0f;
            }
        } else if (progressFraction >= 1.0f) {
            f = 1.0f;
            fBoundToRange = f;
        } else {
            fBoundToRange = com.android.launcher3.Utilities.boundToRange((f2 * DisplayController.getSingleFrameMs(this.mLauncher)) + progressFraction, 0.0f, 1.0f);
            jCalculateDuration = BaseSwipeDetector.calculateDuration(velocity, 1.0f - Math.max(progressFraction, 0.0f)) * ((long) iBlockedFlingDurationFactor);
            f = 1.0f;
        }
        this.mCurrentAnimation.setEndAction(new Runnable() { // from class: com.android.launcher3.touch.-$$Lambda$AbstractStateChangeTouchController$E6zxyyhpepApPfWYvmgRg_N0rwA
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDragEnd$0$AbstractStateChangeTouchController(launcherState2, i);
            }
        });
        ValueAnimator animationPlayer = this.mCurrentAnimation.getAnimationPlayer();
        animationPlayer.setFloatValues(fBoundToRange, f);
        maybeUpdateAtomicAnim(this.mFromState, launcherState2, launcherState2 != this.mToState ? 0.0f : 1.0f);
        updateSwipeCompleteAnimation(animationPlayer, Math.max(jCalculateDuration, getRemainingAtomicDuration()), launcherState2, velocity, zIsFling);
        this.mCurrentAnimation.dispatchOnStart();
        if (zIsFling && launcherState2 == LauncherState.ALL_APPS && !FeatureFlags.UNSTABLE_SPRINGS.get()) {
            this.mLauncher.getAllAppsHost().addSpringFromFlingUpdateListener(animationPlayer, velocity);
        }
        animationPlayer.start();
        this.mAtomicAnimAutoPlayInfo = new AutoPlayAtomicAnimationInfo(f, animationPlayer.getDuration());
        maybeAutoPlayAtomicComponentsAnim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAutoPlayAtomicComponentsAnim() {
        final AnimatorPlaybackController animatorPlaybackController = this.mAtomicComponentsController;
        if (animatorPlaybackController == null || this.mAtomicAnimAutoPlayInfo == null) {
            return;
        }
        ValueAnimator animationPlayer = animatorPlaybackController.getAnimationPlayer();
        animationPlayer.setFloatValues(animatorPlaybackController.getProgressFraction(), this.mAtomicAnimAutoPlayInfo.toProgress);
        long jElapsedRealtime = this.mAtomicAnimAutoPlayInfo.endTime - SystemClock.elapsedRealtime();
        this.mAtomicAnimAutoPlayInfo = null;
        if (jElapsedRealtime <= 0) {
            animationPlayer.start();
            animationPlayer.end();
            this.mAtomicComponentsController = null;
        } else {
            animationPlayer.setDuration(jElapsedRealtime);
            animationPlayer.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.touch.AbstractStateChangeTouchController.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AbstractStateChangeTouchController.this.mAtomicComponentsController == animatorPlaybackController) {
                        AbstractStateChangeTouchController.this.mAtomicComponentsController = null;
                    }
                }
            });
            animationPlayer.start();
        }
    }

    private long getRemainingAtomicDuration() {
        long jMax = 0;
        if (this.mAtomicAnim == null) {
            return 0L;
        }
        if (com.android.launcher3.Utilities.ATLEAST_OREO) {
            return this.mAtomicAnim.getTotalDuration() - this.mAtomicAnim.getCurrentPlayTime();
        }
        Iterator<Animator> it = this.mAtomicAnim.getChildAnimations().iterator();
        while (it.hasNext()) {
            jMax = Math.max(jMax, it.next().getDuration());
        }
        return jMax;
    }

    protected void updateSwipeCompleteAnimation(ValueAnimator animator, long expectedDuration, LauncherState targetState, float velocity, boolean isFling) {
        animator.setDuration(expectedDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity));
    }

    protected int getDirectionForLog() {
        return this.mToState.ordinal > this.mFromState.ordinal ? 1 : 2;
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$onDragEnd$0$AbstractStateChangeTouchController(Lcom/android/launcher3/LauncherState;I)V */
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: onSwipeInteractionCompleted, reason: merged with bridge method [inline-methods] */
    public void lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState targetState, int logAction) {
        AnimatorPlaybackController animatorPlaybackController = this.mAtomicComponentsController;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().end();
            this.mAtomicComponentsController = null;
        }
        clearState();
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        boolean z = true;
        if (pendingAnimation != null) {
            boolean z2 = this.mToState == targetState;
            pendingAnimation.finish(z2, logAction);
            this.mPendingAnimation = null;
            z = true ^ z2;
        }
        if (z) {
            if (targetState != this.mStartState) {
                logReachedState(logAction, targetState);
            }
            this.mLauncher.getStateManager().goToState(targetState, false);
        }
    }

    protected void goToTargetState(LauncherState targetState, int logAction) {
        if (targetState != this.mStartState) {
            logReachedState(logAction, targetState);
        }
        if (!this.mLauncher.isInState(targetState)) {
            this.mLauncher.getStateManager().goToState(targetState, false);
        }
        this.mLauncher.getDragLayer().getScrim().createSysuiMultiplierAnim(1.0f).setDuration(0L).start();
    }

    private void logReachedState(int logAction, LauncherState targetState) {
        StatsLogManager.LauncherEvent launcherEvent;
        this.mLauncher.getUserEventDispatcher().logStateChangeAction(logAction, getDirectionForLog(), this.mStartContainerType, this.mStartState.containerType, targetState.containerType, this.mLauncher.getWorkspace() != null ? this.mLauncher.getWorkspace().getCurrentPage() : 0);
        StatsLogManager.StatsLogger statsLoggerWithContainerInfo = this.mLauncher.getStatsLogManager().logger().withSrcState(StatsLogManager.containerTypeToAtomState(this.mStartState.containerType)).withDstState(StatsLogManager.containerTypeToAtomState(targetState.containerType)).withContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(this.mLauncher.getWorkspace().getCurrentPage())).build());
        int i = this.mStartState.containerType;
        int i2 = targetState.containerType;
        if (this.mToState.ordinal > this.mFromState.ordinal) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEUP;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEDOWN;
        }
        statsLoggerWithContainerInfo.log(StatsLogManager.getLauncherAtomEvent(i, i2, launcherEvent));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearState() {
        cancelAnimationControllers();
        AnimatorSet animatorSet = this.mAtomicAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mAtomicAnim = null;
        }
        this.mScheduleResumeAtomicComponent = false;
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
    }

    private void cancelAnimationControllers() {
        this.mCurrentAnimation = null;
        cancelAtomicComponentsController();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAtomicComponentsController() {
        AnimatorPlaybackController animatorPlaybackController = this.mAtomicComponentsController;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().cancel();
            this.mAtomicComponentsController = null;
        }
        this.mAtomicAnimAutoPlayInfo = null;
    }

    public void finishAtomicComponentsController() {
        LGLog.d(TAG, "finishAtomicComponentsController : " + this.mAtomicComponentsController + ", " + this.mAtomicAnimAutoPlayInfo);
        AnimatorPlaybackController animatorPlaybackController = this.mAtomicComponentsController;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().end();
            this.mAtomicComponentsController = null;
        }
        this.mAtomicAnimAutoPlayInfo = null;
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
    }

    private static class AutoPlayAtomicAnimationInfo {
        public final long endTime;
        public final float toProgress;

        AutoPlayAtomicAnimationInfo(float toProgress, long duration) {
            this.toProgress = toProgress;
            this.endTime = duration + SystemClock.elapsedRealtime();
        }
    }
}
