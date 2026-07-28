package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.graphics.WorkspaceAndHotseatScrim;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.VibratorWrapper;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.views.RecentsView;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class FlingAndHoldTouchController extends PortraitStatesTouchController {
    private static final float MAX_DISPLACEMENT_PERCENT = 0.75f;
    private static final long PEEK_IN_ANIM_DURATION = 240;
    private static final long PEEK_OUT_ANIM_DURATION = 100;
    protected final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMaxDisplacement;
    private final float mMotionPauseMinDisplacement;
    private AnimatorSet mPeekAnim;

    public interface FeedbackHandler {
        void resetFeedback();
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    protected long getAtomicDuration() {
        return 300L;
    }

    public FlingAndHoldTouchController(Launcher l) {
        super(l, false);
        this.mMotionPauseDetector = new MotionPauseDetector(l);
        this.mMotionPauseMinDisplacement = ViewConfiguration.get(l).getScaledTouchSlop();
        this.mMotionPauseMaxDisplacement = getMotionPauseMaxDisplacement();
    }

    protected float getMotionPauseMaxDisplacement() {
        return getShiftRange() * 0.75f;
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        this.mMotionPauseDetector.clear();
        super.onDragStart(start, startDisplacement);
        if (handlingOverviewAnim()) {
            this.mMotionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$OjYoHJ-UoUUstrW23BTts1ZSkOE
                @Override // com.android.quickstep.util.MotionPauseDetector.OnMotionPauseListener
                public final void onMotionPauseChanged(boolean z) {
                    this.f$0.onMotionPauseChanged(z);
                }
            });
        }
        if (this.mAtomicAnim != null) {
            this.mAtomicAnim.cancel();
        }
    }

    protected void onMotionPauseChanged(boolean isPaused) {
        ((RecentsView) this.mLauncher.getOverviewPanel()).setOverviewStateEnabled(isPaused);
        AnimatorSet animatorSet = this.mPeekAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        LauncherState launcherState = isPaused ? LauncherState.NORMAL : LauncherState.OVERVIEW_PEEK;
        LauncherState launcherState2 = isPaused ? LauncherState.OVERVIEW_PEEK : LauncherState.NORMAL;
        long j = isPaused ? 240L : PEEK_OUT_ANIM_DURATION;
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.duration = j;
        stateAnimationConfig.animFlags = 4;
        AnimatorSet animatorSetCreateAtomicAnimation = this.mLauncher.getStateManager().createAtomicAnimation(launcherState, launcherState2, stateAnimationConfig);
        this.mPeekAnim = animatorSetCreateAtomicAnimation;
        animatorSetCreateAtomicAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FlingAndHoldTouchController.this.mPeekAnim = null;
            }
        });
        this.mPeekAnim.start();
        if (this.mLauncher != null) {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, this.mLauncher.getRootView());
        }
        WorkspaceAndHotseatScrim scrim = this.mLauncher.getDragLayer().getScrim();
        float[] fArr = new float[1];
        fArr[0] = isPaused ? 0.0f : 1.0f;
        scrim.createSysuiMultiplierAnim(fArr).setDuration(j).start();
    }

    protected boolean handlingOverviewAnim() {
        return this.mStartState == LauncherState.NORMAL && (SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) == 0;
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController, com.android.launcher3.touch.AbstractStateChangeTouchController
    protected StateAnimationConfig getConfigForStates(LauncherState fromState, LauncherState toState) {
        if (fromState == LauncherState.NORMAL && toState == LauncherState.ALL_APPS) {
            StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
            LauncherState.NORMAL.getVerticalProgress(this.mLauncher);
            LauncherState.OVERVIEW.getVerticalProgress(this.mLauncher);
            stateAnimationConfig.setInterpolator(12, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.08f));
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                stateAnimationConfig.setInterpolator(10, Interpolators.FAST_OUT_SLOW_IN);
            } else {
                stateAnimationConfig.setInterpolator(10, Interpolators.DEACCEL_2);
            }
            stateAnimationConfig.setInterpolator(1, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(2, Interpolators.DEACCEL_3);
            stateAnimationConfig.setInterpolator(3, Interpolators.DEACCEL_3);
            return stateAnimationConfig;
        }
        if (fromState == LauncherState.ALL_APPS && toState == LauncherState.NORMAL) {
            StateAnimationConfig stateAnimationConfig2 = new StateAnimationConfig();
            LauncherState.OVERVIEW.getVerticalProgress(this.mLauncher);
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                stateAnimationConfig2.setInterpolator(10, Interpolators.FAST_OUT_SLOW_IN);
            } else {
                stateAnimationConfig2.setInterpolator(10, Interpolators.DEACCEL_2);
            }
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                stateAnimationConfig2.setInterpolator(1, Interpolators.ACCEL);
                stateAnimationConfig2.setInterpolator(2, Interpolators.ACCEL);
                stateAnimationConfig2.setInterpolator(3, Interpolators.ACCEL);
            } else {
                stateAnimationConfig2.setInterpolator(12, Interpolators.clampToProgress(Interpolators.DEACCEL, 0.92f, 1.0f));
            }
            return stateAnimationConfig2;
        }
        return super.getConfigForStates(fromState, toState);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement, MotionEvent event) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "FlingAndHoldTouchController");
        }
        float f = -displacement;
        this.mMotionPauseDetector.setDisallowPause(!handlingOverviewAnim() || f < this.mMotionPauseMinDisplacement || f > this.mMotionPauseMaxDisplacement);
        this.mMotionPauseDetector.addPosition(event);
        return super.onDrag(displacement, event);
    }

    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController, com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        if (this.mMotionPauseDetector.isPaused() && handlingOverviewAnim()) {
            goToOverviewOnDragEnd(velocity);
        } else {
            super.onDragEnd(velocity);
        }
        if (this.mLauncher.getAppsView() != null) {
            KeyEvent.Callback searchView = this.mLauncher.getAppsView().getSearchView();
            if (searchView instanceof FeedbackHandler) {
                ((FeedbackHandler) searchView).resetFeedback();
            }
        }
        this.mMotionPauseDetector.clear();
    }

    protected void goToOverviewOnDragEnd(float velocity) {
        AnimatorSet animatorSet = this.mPeekAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator animatorCreateStateElementAnimation = this.mLauncher.createAtomicAnimationFactory().createStateElementAnimation(3, new float[0]);
        this.mAtomicAnim = new AnimatorSet();
        this.mAtomicAnim.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController.2
            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                FlingAndHoldTouchController.this.lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState.OVERVIEW, 3);
            }

            @Override // com.android.launcher3.anim.AnimationSuccessListener, android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (this.mCancelled) {
                    StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
                    stateAnimationConfig.animFlags = 4;
                    stateAnimationConfig.duration = FlingAndHoldTouchController.PEEK_OUT_ANIM_DURATION;
                    FlingAndHoldTouchController flingAndHoldTouchController = FlingAndHoldTouchController.this;
                    flingAndHoldTouchController.mPeekAnim = flingAndHoldTouchController.mLauncher.getStateManager().createAtomicAnimation(FlingAndHoldTouchController.this.mFromState, FlingAndHoldTouchController.this.mToState, stateAnimationConfig);
                    FlingAndHoldTouchController.this.mPeekAnim.start();
                }
                FlingAndHoldTouchController.this.mAtomicAnim = null;
            }
        });
        this.mAtomicAnim.play(animatorCreateStateElementAnimation);
        this.mAtomicAnim.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.launcher3.touch.AbstractStateChangeTouchController
    public void goToTargetState(final LauncherState targetState, final int logAction) {
        AnimatorSet animatorSet = this.mPeekAnim;
        if (animatorSet != null && animatorSet.isStarted()) {
            this.mPeekAnim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.uioverrides.touchcontrollers.FlingAndHoldTouchController.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    FlingAndHoldTouchController.super.goToTargetState(targetState, logAction);
                }
            });
        } else {
            super.goToTargetState(targetState, logAction);
        }
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController
    protected int updateAnimComponentsOnReinit(int animComponents) {
        return handlingOverviewAnim() ? animComponents | 8 : animComponents;
    }
}
