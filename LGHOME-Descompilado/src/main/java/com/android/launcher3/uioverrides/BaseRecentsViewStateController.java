package com.android.launcher3.uioverrides;

import android.util.FloatProperty;
import android.view.animation.Interpolator;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.views.RecentsView;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseRecentsViewStateController<T extends RecentsView> implements StateManager.StateHandler<LauncherState> {
    private static final boolean DEBUG = false;
    private static final String TAG = "BaseRecentsViewStateController";
    public final int OVERVIEW_EXPAND_DURATION = 400;
    protected final BaseQuickstepLauncher mLauncher;
    protected final T mRecentsView;

    abstract FloatProperty getContentAlphaProperty();

    abstract FloatProperty getTaskModalnessProperty();

    public BaseRecentsViewStateController(BaseQuickstepLauncher launcher) {
        this.mLauncher = launcher;
        this.mRecentsView = (T) launcher.getOverviewPanel();
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState state) {
        float[] overviewScaleAndOffset = state.getOverviewScaleAndOffset(this.mLauncher);
        LauncherAnimUtils.SCALE_PROPERTY.set(this.mRecentsView, Float.valueOf(overviewScaleAndOffset[0]));
        RecentsView.ADJACENT_PAGE_OFFSET.set(this.mRecentsView, Float.valueOf(overviewScaleAndOffset[1]));
        getContentAlphaProperty().set(this.mRecentsView, Float.valueOf(state.overviewUi ? 1.0f : 0.0f));
        if (LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue()) {
            setRecommendedLayoutAlpha(true, state, null, null);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation builder) {
        if (!config.hasAnimationFlag(6)) {
            LGLog.d(TAG, "[RecentsAnimation] setStateWithAnimation: skip because playAtomicOverviewComponent.  playAtomicOverviewScaleComponent() = " + config.playAtomicOverviewScaleComponent());
            return;
        }
        if (config.hasAnimationFlag(8)) {
            LGLog.d(TAG, "[RecentsAnimation] setStateWithAnimation: skip has FLAG_DONT_ANIMATE_OVERVIEW");
        } else {
            setStateWithAnimationInternal(toState, config, builder);
        }
    }

    void setStateWithAnimationInternal(final LauncherState toState, StateAnimationConfig config, PendingAnimation setter) {
        LGLog.d(TAG, "[RecentsAnimation] setStateWithAnimationInternal: toState.overviewUi = " + toState.overviewUi + ", " + toState);
        float[] overviewScaleAndOffset = toState.getOverviewScaleAndOffset(this.mLauncher);
        boolean z = false;
        setter.setFloat(this.mRecentsView, LauncherAnimUtils.SCALE_PROPERTY, overviewScaleAndOffset[0], config.getInterpolator(6, Interpolators.LINEAR));
        setter.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_OFFSET, overviewScaleAndOffset[1], config.getInterpolator(7, Interpolators.LINEAR));
        if (!LGHomeFeature.isOverviewNewUIReactiveAnimationEnable() || toState == LauncherState.QUICK_SWITCH || toState == LauncherState.NORMAL) {
            setter.setFloat(this.mRecentsView, LauncherAnimUtils.SCALE_PROPERTY, overviewScaleAndOffset[0], config.getInterpolator(6, Interpolators.LINEAR));
            Interpolator interpolator = config.getInterpolator(7, Interpolators.LINEAR);
            Interpolator interpolator2 = config.getInterpolator(8, Interpolators.LINEAR);
            float f = overviewScaleAndOffset[1];
            if (this.mRecentsView.getLayoutDirection() == 1) {
                f = -f;
            }
            setter.setFloat(this.mRecentsView, LauncherAnimUtils.VIEW_TRANSLATE_X, f, interpolator);
            setter.setFloat(this.mRecentsView, LauncherAnimUtils.VIEW_TRANSLATE_Y, overviewScaleAndOffset[1], interpolator2);
        } else {
            setter.setFloatWithDuration(this.mRecentsView, LauncherAnimUtils.SCALE_PROPERTY, overviewScaleAndOffset[0], config.getInterpolator(6, Interpolators.LINEAR), 500);
            Interpolator interpolator3 = config.getInterpolator(7, Interpolators.DEACCEL_2);
            Interpolator interpolator4 = config.getInterpolator(8, Interpolators.DEACCEL_2);
            float f2 = overviewScaleAndOffset[1];
            if (this.mRecentsView.getLayoutDirection() == 1) {
                f2 = -f2;
            }
            float f3 = f2;
            setter.setFloatWithDuration(this.mRecentsView, LauncherAnimUtils.VIEW_TRANSLATE_X, f3, interpolator3, 400);
            setter.setFloatWithDuration(this.mRecentsView, LauncherAnimUtils.VIEW_TRANSLATE_Y, f3, interpolator4, 400);
        }
        if (this.mLauncher.isInState(LauncherState.OVERVIEW) && toState == LauncherState.NORMAL && SysUINavigationMode.getMode(this.mLauncher) == SysUINavigationMode.Mode.NO_BUTTON) {
            z = true;
        }
        setter.setFloat(this.mRecentsView, getContentAlphaProperty(), toState.overviewUi ? 1.0f : 0.0f, z ? Interpolators.DELAYED_OUT : Interpolators.AGGRESSIVE_EASE_IN_OUT);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue()) {
            setRecommendedLayoutAlpha(true, toState, config, setter);
        }
        setter.setFloat(this.mRecentsView, getTaskModalnessProperty(), toState.getOverviewModalness(), config.getInterpolator(13, Interpolators.LINEAR));
    }

    private void setRecommendedLayoutAlpha(boolean animated, LauncherState state, StateAnimationConfig config, PendingAnimation builder) {
        if (this.mRecentsView.getRecommandLayout() != null) {
            boolean z = (state.getVisibleElements(this.mLauncher) & 256) != 0;
            boolean z2 = this.mLauncher.getDeviceProfile().isLandscape;
            boolean z3 = this.mLauncher.getDeviceProfile().isMultiWindowMode;
            float f = (!z2 && z && LGHomeFeature.isAppSuggestionEnabled()) ? 1.0f : 0.0f;
            if (animated && config != null && builder != null && LGHomeFeature.isAppSuggestionEnabled()) {
                builder.setFloatWithDuration(this.mRecentsView, RecentsView.RECOMMAND_APP_ALPHA, f, state == LauncherState.OVERVIEW_PEEK ? Interpolators.ACCEL_2 : Interpolators.DEACCEL, f == 1.0f ? 400 : 0);
            } else {
                RecentsView.RECOMMAND_APP_ALPHA.setValue(this.mRecentsView, f);
            }
            this.mRecentsView.setLocationOfClearAllButton();
        }
    }
}
