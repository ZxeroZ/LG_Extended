package com.android.quickstep.fallback;

import android.util.FloatProperty;
import android.util.Pair;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.RecentsView;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class FallbackRecentsStateController implements StateManager.StateHandler<RecentsState> {
    private final RecentsActivity mActivity;
    private final StateAnimationConfig mNoConfig = new StateAnimationConfig();
    private final FallbackRecentsView mRecentsView;

    public FallbackRecentsStateController(RecentsActivity activity) {
        this.mActivity = activity;
        this.mRecentsView = (FallbackRecentsView) activity.getOverviewPanel();
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(RecentsState state) {
        PropertySetter.NO_ANIM_PROPERTY_SETTER.setFloat(this.mRecentsView, RecentsView.CONTENT_ALPHA, 1.0f, Interpolators.LINEAR);
        this.mRecentsView.updateEmptyMessage();
        this.mRecentsView.resetTaskVisuals();
        setProperties(state, this.mNoConfig, PropertySetter.NO_ANIM_PROPERTY_SETTER);
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(RecentsState toState, StateAnimationConfig config, PendingAnimation setter) {
        if (config.hasAnimationFlag(6) && !config.hasAnimationFlag(8)) {
            final FallbackRecentsView fallbackRecentsView = this.mRecentsView;
            Objects.requireNonNull(fallbackRecentsView);
            setter.addOnFrameCallback(new Runnable() { // from class: com.android.quickstep.fallback.-$$Lambda$mHYSxfYS3YeYAt4CiMpWR3DIySE
                @Override // java.lang.Runnable
                public final void run() {
                    fallbackRecentsView.loadVisibleTaskData();
                }
            });
            this.mRecentsView.updateEmptyMessage();
            setProperties(toState, config, setter);
        }
    }

    private void setProperties(RecentsState state, StateAnimationConfig config, PropertySetter setter) {
        setter.setFloat(this.mRecentsView.getClearAllButton(), ClearAllButton.VISIBILITY_ALPHA, (!state.hasButtons() || this.mRecentsView.getChildCount() <= 0 || this.mRecentsView.isFastOverView()) ? 0.0f : 1.0f, Interpolators.LINEAR);
        float[] overviewScaleAndOffset = state.getOverviewScaleAndOffset(this.mActivity);
        setter.setFloat(this.mRecentsView, LauncherAnimUtils.SCALE_PROPERTY, overviewScaleAndOffset[0], config.getInterpolator(6, Interpolators.LINEAR));
        setter.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_OFFSET, overviewScaleAndOffset[1], config.getInterpolator(7, Interpolators.LINEAR));
        setter.setFloat(this.mRecentsView, RecentsView.TASK_MODALNESS, state.getOverviewModalness(), config.getInterpolator(13, Interpolators.LINEAR));
        setter.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, state.isFullScreen() ? 1.0f : 0.0f, Interpolators.LINEAR);
        RecentsState recentsState = (RecentsState) this.mActivity.getStateManager().getState();
        if (isSplitSelectionState(state) && !isSplitSelectionState(recentsState)) {
            setter.add(this.mRecentsView.createSplitSelectInitAnimation(state.getTransitionDuration(this.mActivity)).buildAnim());
            if (setter instanceof PendingAnimation) {
                final FallbackRecentsView fallbackRecentsView = this.mRecentsView;
                Objects.requireNonNull(fallbackRecentsView);
                ((PendingAnimation) setter).addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.quickstep.fallback.-$$Lambda$sE4dekbT5oitvkeBHaQz8ftXABA
                    @Override // java.lang.Runnable
                    public final void run() {
                        fallbackRecentsView.onFinishInitSplitAnimation();
                    }
                }));
            }
        }
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = this.mRecentsView.getPagedOrientationHandler().getSplitSelectTaskOffset(RecentsView.TASK_PRIMARY_SPLIT_TRANSLATION, RecentsView.TASK_SECONDARY_SPLIT_TRANSLATION, this.mActivity.getDeviceProfile());
        setter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.second, 0.0f, Interpolators.LINEAR);
        if (isSplitSelectionState(state)) {
            this.mRecentsView.applySplitPrimaryScrollOffset();
            setter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, this.mRecentsView.getSplitSelectTranslation(), Interpolators.LINEAR);
        } else {
            this.mRecentsView.resetSplitPrimaryScrollOffset();
            setter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, 0.0f, Interpolators.LINEAR);
        }
    }

    private boolean isSplitSelectionState(RecentsState toState) {
        return toState == RecentsState.OVERVIEW_SPLIT_SELECT;
    }
}
