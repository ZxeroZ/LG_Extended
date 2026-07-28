package com.android.launcher3.uioverrides;

import android.util.FloatProperty;
import android.util.Pair;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.LauncherRecentsView;
import com.android.quickstep.views.RecentsView;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class RecentsViewStateController extends BaseRecentsViewStateController<LauncherRecentsView> {
    public RecentsViewStateController(BaseQuickstepLauncher launcher) {
        super(launcher);
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.launcher3.uioverrides.BaseRecentsViewStateController, com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState state) {
        super.setState(state);
        if (state.overviewUi) {
            ((LauncherRecentsView) this.mRecentsView).updateEmptyMessage();
            ((LauncherRecentsView) this.mRecentsView).resetTaskVisuals();
        }
        setAlphas(PropertySetter.NO_ANIM_PROPERTY_SETTER, state);
        ((LauncherRecentsView) this.mRecentsView).setFullscreenProgress(state.getOverviewFullscreenProgress());
        handleSplitSelectionState(state, null);
    }

    @Override // com.android.launcher3.uioverrides.BaseRecentsViewStateController
    void setStateWithAnimationInternal(LauncherState toState, StateAnimationConfig config, PendingAnimation builder) {
        super.setStateWithAnimationInternal(toState, config, builder);
        if (toState.overviewUi) {
            final LauncherRecentsView launcherRecentsView = (LauncherRecentsView) this.mRecentsView;
            Objects.requireNonNull(launcherRecentsView);
            builder.addOnFrameCallback(new Runnable() { // from class: com.android.launcher3.uioverrides.-$$Lambda$1S8S7UxwMuXlfU3F6mc7h8VNVEI
                @Override // java.lang.Runnable
                public final void run() {
                    launcherRecentsView.loadVisibleTaskData();
                }
            });
            ((LauncherRecentsView) this.mRecentsView).updateEmptyMessage();
        } else {
            final LauncherRecentsView launcherRecentsView2 = (LauncherRecentsView) this.mRecentsView;
            Objects.requireNonNull(launcherRecentsView2);
            builder.addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.-$$Lambda$XbUHfNpcNOxl2hevwJOaC-WpmYA
                @Override // java.lang.Runnable
                public final void run() {
                    launcherRecentsView2.resetTaskVisuals();
                }
            }));
        }
        handleSplitSelectionState(toState, builder);
        setAlphas(builder, toState);
        builder.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, toState.getOverviewFullscreenProgress(), Interpolators.LINEAR);
    }

    private void handleSplitSelectionState(LauncherState toState, PendingAnimation builder) {
        boolean z = builder != null;
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = ((RecentsView) this.mLauncher.getOverviewPanel()).getPagedOrientationHandler().getSplitSelectTaskOffset(RecentsView.TASK_PRIMARY_SPLIT_TRANSLATION, RecentsView.TASK_SECONDARY_SPLIT_TRANSLATION, this.mLauncher.getDeviceProfile());
        if (toState == LauncherState.OVERVIEW_SPLIT_SELECT) {
            PendingAnimation pendingAnimationCreateSplitSelectInitAnimation = ((LauncherRecentsView) this.mRecentsView).createSplitSelectInitAnimation(toState.getTransitionDuration(this.mLauncher));
            pendingAnimationCreateSplitSelectInitAnimation.setFloat((LauncherRecentsView) this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, toState.getSplitSelectTranslation(this.mLauncher), Interpolators.LINEAR);
            pendingAnimationCreateSplitSelectInitAnimation.setFloat((LauncherRecentsView) this.mRecentsView, (FloatProperty) splitSelectTaskOffset.second, 0.0f, Interpolators.LINEAR);
            if (!z) {
                AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimationCreateSplitSelectInitAnimation.createPlaybackController();
                final LauncherRecentsView launcherRecentsView = (LauncherRecentsView) this.mRecentsView;
                Objects.requireNonNull(launcherRecentsView);
                animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.launcher3.uioverrides.-$$Lambda$aTySCCIWdmFCwjI9gpjCssEGEQ0
                    @Override // java.lang.Runnable
                    public final void run() {
                        launcherRecentsView.onFinishInitSplitAnimation();
                    }
                });
                animatorPlaybackControllerCreatePlaybackController.start();
            } else {
                builder.add(pendingAnimationCreateSplitSelectInitAnimation.buildAnim());
                final LauncherRecentsView launcherRecentsView2 = (LauncherRecentsView) this.mRecentsView;
                Objects.requireNonNull(launcherRecentsView2);
                builder.addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.-$$Lambda$aTySCCIWdmFCwjI9gpjCssEGEQ0
                    @Override // java.lang.Runnable
                    public final void run() {
                        launcherRecentsView2.onFinishInitSplitAnimation();
                    }
                }));
            }
            ((LauncherRecentsView) this.mRecentsView).applySplitPrimaryScrollOffset();
            return;
        }
        ((LauncherRecentsView) this.mRecentsView).resetSplitPrimaryScrollOffset();
    }

    private boolean isSplitSelectionState(LauncherState toState) {
        return toState == LauncherState.OVERVIEW_SPLIT_SELECT;
    }

    private void setAlphas(PropertySetter propertySetter, LauncherState state) {
        float f = ((state.getVisibleElements(this.mLauncher) & 64) == 0 || this.mRecentsView.isFastOverView()) ? 0.0f : 1.0f;
        if (((LauncherRecentsView) this.mRecentsView).getChildCount() == 0) {
            propertySetter.setFloat(((LauncherRecentsView) this.mRecentsView).getClearAllButton(), ClearAllButton.VISIBILITY_ALPHA, f, Interpolators.INSTANT);
        } else {
            propertySetter.setFloat(((LauncherRecentsView) this.mRecentsView).getClearAllButton(), ClearAllButton.VISIBILITY_ALPHA, f, Interpolators.LINEAR);
        }
    }

    @Override // com.android.launcher3.uioverrides.BaseRecentsViewStateController
    FloatProperty<RecentsView> getTaskModalnessProperty() {
        return RecentsView.TASK_MODALNESS;
    }

    @Override // com.android.launcher3.uioverrides.BaseRecentsViewStateController
    FloatProperty<RecentsView> getContentAlphaProperty() {
        return RecentsView.CONTENT_ALPHA;
    }
}
