package com.android.launcher3.uioverrides.states;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.WorkspaceStateTransitionAnimation;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.RecentsAtomicAnimationFactory;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class QuickstepAtomicAnimationFactory extends RecentsAtomicAnimationFactory<Launcher, LauncherState> {
    public static final long ATOMIC_DURATION_FROM_PAUSED_TO_OVERVIEW = 300;
    public static final int INDEX_PAUSE_TO_OVERVIEW_ANIM = 3;
    public static final int INDEX_SHELF_ANIM = 2;
    private static final int MY_ANIM_COUNT = 2;
    protected static final int NEXT_INDEX = 4;
    private static final float RECENTS_PREPARE_SCALE = 1.33f;
    private int mHintToNormalDuration;

    public QuickstepAtomicAnimationFactory(QuickstepLauncher activity) {
        super(activity, 2);
        this.mHintToNormalDuration = -1;
    }

    public /* synthetic */ void lambda$createStateElementAnimation$0$QuickstepAtomicAnimationFactory(float f, float f2, LauncherState.ScaleAndTranslation scaleAndTranslation, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (fFloatValue >= f || ((Launcher) this.mActivity).isInState(LauncherState.BACKGROUND_APP)) {
            ((Launcher) this.mActivity).getHotseat().setTranslationY(((fFloatValue - f) * f2) + scaleAndTranslation.translationY);
        }
    }

    @Override // com.android.quickstep.util.RecentsAtomicAnimationFactory, com.android.launcher3.statemanager.StateManager.AtomicAnimationFactory
    public Animator createStateElementAnimation(int index, float... values) {
        if (index != 2) {
            if (index == 3) {
                StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
                stateAnimationConfig.duration = 300L;
                stateAnimationConfig.setInterpolator(0, Interpolators.OVERSHOOT_1_2);
                stateAnimationConfig.setInterpolator(10, Interpolators.DEACCEL_3);
                if ((LauncherState.OVERVIEW.getVisibleElements((Launcher) this.mActivity) & 1) != 0) {
                    stateAnimationConfig.setInterpolator(4, Interpolators.OVERSHOOT_1_2);
                    stateAnimationConfig.setInterpolator(5, Interpolators.OVERSHOOT_1_2);
                }
                StateManager<LauncherState> stateManager = ((Launcher) this.mActivity).getStateManager();
                return stateManager.createAtomicAnimation((LauncherState) stateManager.getCurrentStableState(), LauncherState.OVERVIEW, stateAnimationConfig);
            }
            return super.createStateElementAnimation(index, values);
        }
        AllAppsTransitionController allAppsController = ((Launcher) this.mActivity).getAllAppsController();
        Animator animatorCreateSpringAnimation = allAppsController.createSpringAnimation(values);
        if ((LauncherState.OVERVIEW.getVisibleElements((Launcher) this.mActivity) & 1) == 0) {
            return animatorCreateSpringAnimation;
        }
        final float verticalProgress = LauncherState.OVERVIEW.getVerticalProgress((Launcher) this.mActivity);
        final LauncherState.ScaleAndTranslation hotseatScaleAndTranslation = LauncherState.OVERVIEW.getHotseatScaleAndTranslation((Launcher) this.mActivity);
        final float shiftRange = allAppsController.getShiftRange();
        if (values.length == 1) {
            values = new float[]{allAppsController.getProgress(), values[0]};
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(values);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.uioverrides.states.-$$Lambda$QuickstepAtomicAnimationFactory$G-v3Kc0Hahm9m9mjDquStVO3GOo
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$createStateElementAnimation$0$QuickstepAtomicAnimationFactory(verticalProgress, shiftRange, hotseatScaleAndTranslation, valueAnimator);
            }
        });
        valueAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
        valueAnimatorOfFloat.setDuration(animatorCreateSpringAnimation.getDuration());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimatorOfFloat);
        animatorSet.play(animatorCreateSpringAnimation);
        return animatorSet;
    }

    /* JADX DEBUG: Method merged with bridge method: prepareForAtomicAnimation(Ljava/lang/Object;Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;)V */
    @Override // com.android.launcher3.statemanager.StateManager.AtomicAnimationFactory
    public void prepareForAtomicAnimation(LauncherState fromState, LauncherState toState, StateAnimationConfig config) {
        Interpolator interpolator;
        if (toState == LauncherState.NORMAL && fromState == LauncherState.OVERVIEW) {
            config.setInterpolator(1, Interpolators.DEACCEL);
            config.setInterpolator(3, Interpolators.ACCEL);
            config.setInterpolator(10, Interpolators.ACCEL);
            config.setInterpolator(6, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.9f));
            config.setInterpolator(7, Interpolators.ACCEL);
            config.setInterpolator(9, Interpolators.DEACCEL_1_7);
            Workspace workspace = ((Launcher) this.mActivity).getWorkspace();
            boolean z = workspace.getVisibility() == 0;
            if (z) {
                CellLayout cellLayout = (CellLayout) workspace.getChildAt(workspace.getCurrentPage());
                z = cellLayout != null && cellLayout.getVisibility() == 0 && cellLayout.getShortcutsAndWidgets().getAlpha() > 0.0f;
            }
            if (!z) {
                workspace.setScaleX(0.92f);
                workspace.setScaleY(0.92f);
            }
            Hotseat hotseat = ((Launcher) this.mActivity).getHotseat();
            if (hotseat.getVisibility() == 0) {
                int i = (hotseat.getAlpha() > 0.0f ? 1 : (hotseat.getAlpha() == 0.0f ? 0 : -1));
                return;
            }
            return;
        }
        if (toState == LauncherState.NORMAL && fromState == LauncherState.OVERVIEW_PEEK) {
            config.setInterpolator(9, Interpolators.FINAL_FRAME);
            return;
        }
        if (toState == LauncherState.OVERVIEW_PEEK && fromState == LauncherState.NORMAL) {
            config.setInterpolator(9, Interpolators.INSTANT);
            config.setInterpolator(7, Interpolators.DEACCEL_2);
            config.setInterpolator(11, Interpolators.FAST_OUT_SLOW_IN);
            return;
        }
        if ((fromState == LauncherState.NORMAL || fromState == LauncherState.HINT_STATE) && toState == LauncherState.OVERVIEW) {
            if (SysUINavigationMode.getMode(this.mActivity) == SysUINavigationMode.Mode.NO_BUTTON) {
                config.setInterpolator(1, fromState == LauncherState.NORMAL ? Interpolators.ACCEL : Interpolators.OVERSHOOT_1_2);
                config.setInterpolator(2, Interpolators.ACCEL);
            } else {
                config.setInterpolator(1, Interpolators.OVERSHOOT_1_2);
                RecentsView recentsView = (RecentsView) ((Launcher) this.mActivity).getOverviewPanel();
                if (recentsView.getVisibility() != 0 || recentsView.getContentAlpha() == 0.0f) {
                    LauncherAnimUtils.SCALE_PROPERTY.set(recentsView, Float.valueOf(RECENTS_PREPARE_SCALE));
                }
            }
            config.setInterpolator(3, Interpolators.OVERSHOOT_1_2);
            config.setInterpolator(10, Interpolators.OVERSHOOT_1_2);
            config.setInterpolator(6, Interpolators.OVERSHOOT_1_2);
            config.setInterpolator(14, Interpolators.OVERSHOOT_1_2);
            if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(this.mActivity)) {
                interpolator = Interpolators.DEACCEL_2;
            } else {
                interpolator = Interpolators.OVERSHOOT_1_7;
            }
            config.setInterpolator(7, interpolator);
            config.setInterpolator(8, interpolator);
            config.setInterpolator(9, Interpolators.OVERSHOOT_1_2);
            return;
        }
        if (fromState == LauncherState.HINT_STATE && toState == LauncherState.NORMAL) {
            config.setInterpolator(14, Interpolators.DEACCEL_3);
            if (this.mHintToNormalDuration == -1) {
                this.mHintToNormalDuration = (int) WorkspaceStateTransitionAnimation.getSpringScaleAnimator((Launcher) this.mActivity, ((Launcher) this.mActivity).getWorkspace(), toState.getWorkspaceScaleAndTranslation((Launcher) this.mActivity).scale).getDuration();
            }
            config.duration = Math.max(config.duration, this.mHintToNormalDuration);
        }
    }
}
