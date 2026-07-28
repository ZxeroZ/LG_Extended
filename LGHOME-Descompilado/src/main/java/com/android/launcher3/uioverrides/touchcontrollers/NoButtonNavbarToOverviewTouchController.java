package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.VibratorWrapper;
import com.android.quickstep.util.StaggeredWorkspaceAnim;
import com.android.quickstep.views.RecentsView;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class NoButtonNavbarToOverviewTouchController extends FlingAndHoldTouchController {
    private static final float OVERVIEW_MOVEMENT_FACTOR = 0.25f;
    private static final long TRANSLATION_ANIM_MIN_DURATION_MS = 80;
    private static final float TRANSLATION_ANIM_VELOCITY_DP_PER_MS = 0.8f;
    private boolean mDidTouchStartInNavBar;
    private boolean mIsHomeStaggeredAnimFinished;
    private boolean mIsOverviewRehidden;
    private ObjectAnimator mNormalToHintOverviewScrimAnimator;
    private boolean mReachedOverview;
    private final RecentsView mRecentsView;
    private PointF mStartDisplacement;

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController
    protected float getMotionPauseMaxDisplacement() {
        return Float.MAX_VALUE;
    }

    public NoButtonNavbarToOverviewTouchController(Launcher l) {
        super(l);
        this.mStartDisplacement = new PointF();
        this.mRecentsView = (RecentsView) l.getOverviewPanel();
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NoButtonNavbarToOverviewTouchController.ctor");
        }
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected boolean canInterceptTouch(MotionEvent ev) {
        this.mDidTouchStartInNavBar = (ev.getEdgeFlags() & 256) != 0;
        return super.canInterceptTouch(ev);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        if (fromState == LauncherState.NORMAL && this.mDidTouchStartInNavBar) {
            return LauncherState.HINT_STATE;
        }
        if (fromState == LauncherState.OVERVIEW && isDragTowardPositive) {
            return LauncherState.OVERVIEW;
        }
        return super.getTargetState(fromState, isDragTowardPositive);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected float initCurrentAnimation(int animComponents) {
        return this.mToState == LauncherState.HINT_STATE ? (-1.0f) / getShiftRange() : super.initCurrentAnimation(animComponents);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        super.onDragStart(start, startDisplacement);
        this.mReachedOverview = false;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected void updateProgress(float fraction) {
        super.updateProgress(fraction);
        ObjectAnimator objectAnimator = this.mNormalToHintOverviewScrimAnimator;
        if (objectAnimator != null) {
            objectAnimator.setCurrentFraction(fraction);
        }
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        super.onDragEnd(velocity);
        if (DisplayManagerHelper.isMultiDisplayDevice() && (this.mToState == LauncherState.OVERVIEW || this.mToState == LauncherState.HINT_STATE)) {
            WindowUtils.sendDualRecentsIntent(this.mLauncher, false);
        }
        this.mNormalToHintOverviewScrimAnimator = null;
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected void updateSwipeCompleteAnimation(ValueAnimator animator, long expectedDuration, LauncherState targetState, float velocity, boolean isFling) {
        super.updateSwipeCompleteAnimation(animator, expectedDuration, targetState, velocity, isFling);
        if (targetState == LauncherState.HINT_STATE) {
            animator.setDuration(LauncherState.HINT_STATE.getTransitionDuration(this.mLauncher));
        }
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController
    protected void onMotionPauseChanged(boolean isPaused) {
        if (this.mCurrentAnimation == null) {
            return;
        }
        this.mNormalToHintOverviewScrimAnimator = null;
        this.mCurrentAnimation.dispatchOnCancelWithoutCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonNavbarToOverviewTouchController$0mIcsDe9F7bj-JNIfQB-nav4djU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onMotionPauseChanged$1$NoButtonNavbarToOverviewTouchController();
            }
        });
        if (this.mLauncher != null) {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, this.mLauncher.getRootView());
        }
    }

    public /* synthetic */ void lambda$onMotionPauseChanged$1$NoButtonNavbarToOverviewTouchController() {
        this.mLauncher.getStateManager().goToState(LauncherState.OVERVIEW, true, new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonNavbarToOverviewTouchController$gAoOBezv4tuX-dJwSwwSdlpqoSI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onMotionPauseChanged$0$NoButtonNavbarToOverviewTouchController();
            }
        });
    }

    public /* synthetic */ void lambda$onMotionPauseChanged$0$NoButtonNavbarToOverviewTouchController() {
        this.mReachedOverview = true;
        maybeSwipeInteractionToOverviewComplete();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeSwipeInteractionToOverviewComplete() {
        if (this.mReachedOverview && this.mDetector.isSettlingState()) {
            lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState.OVERVIEW, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeSwipeInteractionToHomeComplete() {
        if (this.mIsHomeStaggeredAnimFinished && this.mIsOverviewRehidden) {
            lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState.NORMAL, 4);
        }
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController
    protected boolean handlingOverviewAnim() {
        return this.mDidTouchStartInNavBar && super.handlingOverviewAnim();
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float yDisplacement, float xDisplacement, MotionEvent event) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "NoButtonNavbarToOverviewTouchController");
        }
        if (this.mMotionPauseDetector.isPaused()) {
            if (this.mReachedOverview) {
                return true;
            }
            this.mStartDisplacement.set(xDisplacement, yDisplacement);
            return true;
        }
        return super.onDrag(yDisplacement, xDisplacement, event);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController
    protected void goToOverviewOnDragEnd(float velocity) {
        boolean z = Math.abs(dpiFromPx(velocity)) > 1.0f;
        StateManager<LauncherState> stateManager = this.mLauncher.getStateManager();
        if (z) {
            if (velocity > 0.0f) {
                stateManager.goToState(LauncherState.NORMAL, true, new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonNavbarToOverviewTouchController$twNe1xWBGeYZTd5JZPvyfH3oTdA
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$goToOverviewOnDragEnd$2$NoButtonNavbarToOverviewTouchController();
                    }
                });
            } else {
                this.mIsOverviewRehidden = false;
                this.mIsHomeStaggeredAnimFinished = false;
                new StaggeredWorkspaceAnim(this.mLauncher, velocity, false).addAnimatorListener(new AnimationSuccessListener() { // from class: com.android.launcher3.uioverrides.touchcontrollers.NoButtonNavbarToOverviewTouchController.1
                    @Override // com.android.launcher3.anim.AnimationSuccessListener
                    public void onAnimationSuccess(Animator animator) {
                        NoButtonNavbarToOverviewTouchController.this.mIsHomeStaggeredAnimFinished = true;
                        NoButtonNavbarToOverviewTouchController.this.maybeSwipeInteractionToHomeComplete();
                    }
                }).start();
                stateManager.cancelAnimation();
                StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
                stateAnimationConfig.duration = LauncherState.OVERVIEW.getTransitionDuration(this.mLauncher);
                stateAnimationConfig.animFlags = 6;
                AnimatorSet animatorSetCreateAtomicAnimation = stateManager.createAtomicAnimation((LauncherState) stateManager.getState(), LauncherState.NORMAL, stateAnimationConfig);
                animatorSetCreateAtomicAnimation.addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonNavbarToOverviewTouchController$sKOYa3LrxyxYfd4paF36gF1olPY
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$goToOverviewOnDragEnd$3$NoButtonNavbarToOverviewTouchController();
                    }
                }));
                animatorSetCreateAtomicAnimation.start();
            }
        }
        if (this.mReachedOverview) {
            this.mRecentsView.animate().translationX(0.0f).translationY(0.0f).setInterpolator(Interpolators.ACCEL_DEACCEL).setDuration((long) Math.max(80.0f, dpiFromPx(Math.max(Math.abs(this.mRecentsView.getTranslationX()), Math.abs(this.mRecentsView.getTranslationY()))) / 0.8f)).withEndAction(z ? null : new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$NoButtonNavbarToOverviewTouchController$wXhCOUSC9LcVTGjjY4Rl3i668eM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.maybeSwipeInteractionToOverviewComplete();
                }
            });
        }
    }

    public /* synthetic */ void lambda$goToOverviewOnDragEnd$2$NoButtonNavbarToOverviewTouchController() {
        lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState.NORMAL, 4);
    }

    public /* synthetic */ void lambda$goToOverviewOnDragEnd$3$NoButtonNavbarToOverviewTouchController() {
        this.mIsOverviewRehidden = true;
        maybeSwipeInteractionToHomeComplete();
    }

    private float dpiFromPx(float pixels) {
        return Utilities.dpiFromPx(pixels, this.mLauncher.getResources().getDisplayMetrics());
    }
}
