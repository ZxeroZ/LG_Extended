package com.android.quickstep.views;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.AppLaunchTracker;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.TraceHelper;
import com.android.quickstep.LauncherActivityInterface;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.RecentsExtraCard;
import com.android.systemui.shared.recents.model.Task;
import java.util.Objects;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class LauncherRecentsView extends RecentsView<BaseQuickstepLauncher> implements StateManager.StateListener<LauncherState> {
    private RecentsExtraCard mRecentsExtraCardPlugin;
    private PluginListener<RecentsExtraCard> mRecentsExtraCardPluginListener;
    private RecentsExtraViewContainer mRecentsExtraViewContainer;
    private final TransformParams mTransformParams;

    public LauncherRecentsView(Context context) {
        this(context, null);
    }

    public LauncherRecentsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherRecentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, LauncherActivityInterface.INSTANCE);
        this.mTransformParams = new TransformParams();
        this.mRecentsExtraCardPluginListener = new PluginListener<RecentsExtraCard>() { // from class: com.android.quickstep.views.LauncherRecentsView.1
            /* JADX DEBUG: Method merged with bridge method: onPluginConnected(Lcom/android/systemui/plugins/Plugin;Landroid/content/Context;)V */
            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginConnected(RecentsExtraCard recentsExtraCard, Context context2) {
                LauncherRecentsView.this.createRecentsExtraCard();
                LauncherRecentsView.this.mRecentsExtraCardPlugin = recentsExtraCard;
                LauncherRecentsView.this.mRecentsExtraCardPlugin.setupView(context2, LauncherRecentsView.this.mRecentsExtraViewContainer, LauncherRecentsView.this.mActivity);
            }

            /* JADX DEBUG: Method merged with bridge method: onPluginDisconnected(Lcom/android/systemui/plugins/Plugin;)V */
            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginDisconnected(RecentsExtraCard plugin) {
                LauncherRecentsView launcherRecentsView = LauncherRecentsView.this;
                launcherRecentsView.removeView(launcherRecentsView.mRecentsExtraViewContainer);
                LauncherRecentsView.this.mRecentsExtraCardPlugin = null;
                LauncherRecentsView.this.mRecentsExtraViewContainer = null;
            }
        };
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().addStateListener(this);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void init(OverviewActionsView actionsView) {
        super.init(actionsView);
        setContentAlpha(0.0f);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void init(OverviewActionsView actionsView, SplitSelectStateController controller) {
        super.init(actionsView, controller);
        setContentAlpha(0.0f);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void startHome() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            switchToScreenshot(null, new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$LauncherRecentsView$h-z83J7VfcXr0kcRgp-Lmcph-8k
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startHome$1$LauncherRecentsView();
                }
            });
        } else {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.NORMAL);
        }
    }

    public /* synthetic */ void lambda$startHome$1$LauncherRecentsView() {
        finishRecentsAnimation(true, new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$LauncherRecentsView$87BijEkR5yPMPLIwCuMET6nGnG8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startHome$0$LauncherRecentsView();
            }
        });
    }

    public /* synthetic */ void lambda$startHome$0$LauncherRecentsView() {
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.NORMAL);
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            LauncherState launcherState = (LauncherState) ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState();
            if (launcherState == LauncherState.OVERVIEW || launcherState == LauncherState.ALL_APPS) {
                redrawLiveTile(false);
            }
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    public AnimatorSet createAdjacentPageAnimForTaskLaunch(TaskView tv) {
        AnimatorSet animatorSetCreateAdjacentPageAnimForTaskLaunch = super.createAdjacentPageAnimForTaskLaunch(tv);
        if (SysUINavigationMode.getMode(this.mActivity).hasGestures && (((LauncherState) ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState()).getVisibleElements((Launcher) this.mActivity) & 8) != 0) {
            int i = ((BaseQuickstepLauncher) this.mActivity).getDeviceProfile().heightPx;
            ((BaseQuickstepLauncher) this.mActivity).getAllAppsController().getShiftRange();
        }
        return animatorSetCreateAdjacentPageAnimForTaskLaunch;
    }

    @Override // com.android.quickstep.views.RecentsView
    protected void onTaskLaunchAnimationUpdate(float progress, TaskView tv) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            if (tv.isRunningTask()) {
                this.mTransformParams.setProgress(1.0f - progress).setSyncTransactionApplier(this.mSyncTransactionApplier);
            } else {
                redrawLiveTile(true);
            }
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    protected void onTaskLaunchAnimationEnd(boolean success) {
        if (success) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.NORMAL, false);
        } else {
            ((BaseQuickstepLauncher) this.mActivity).getAllAppsController().setState((LauncherState) ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState());
        }
        super.onTaskLaunchAnimationEnd(success);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void onTaskLaunched(Task task) {
        AppLaunchTracker.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).onStartApp(task.getTopComponent(), UserHandle.of(task.key.userId), AppLaunchTracker.CONTAINER_OVERVIEW);
    }

    @Override // com.android.quickstep.views.RecentsView
    public boolean shouldUseMultiWindowTaskSizeStrategy() {
        final BaseQuickstepLauncher baseQuickstepLauncher = (BaseQuickstepLauncher) this.mActivity;
        Objects.requireNonNull(baseQuickstepLauncher);
        return ((Boolean) TraceHelper.whitelistIpcs("isInMultiWindowMode", new Supplier() { // from class: com.android.quickstep.views.-$$Lambda$LauncherRecentsView$efQdxioPB-OJFCs_GVHnf_yfMsc
            @Override // java.util.function.Supplier
            public final Object get() {
                return Boolean.valueOf(baseQuickstepLauncher.isInMultiWindowMode());
            }
        })).booleanValue();
    }

    @Override // com.android.quickstep.views.RecentsView, com.android.launcher3.PagedView, android.view.View
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mEnableDrawingLiveTile) {
            redrawLiveTile(true);
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    public TransformParams getLiveTileParams(boolean mightNeedToRefill) {
        if (!this.mEnableDrawingLiveTile || this.mRecentsAnimationController == null || this.mRecentsAnimationTargets == null) {
            return null;
        }
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.getThumbnail().getGlobalVisibleRect(this.mTempRect);
            int scaleX = (int) (((this.mTaskWidth * runningTaskView.getScaleX()) * getScaleX()) - this.mTempRect.width());
            int scaleY = (int) (((this.mTaskHeight * runningTaskView.getScaleY()) * getScaleY()) - this.mTempRect.height());
            if ((this.mCurrentPage != 0 || mightNeedToRefill) && scaleX > 0) {
                if (this.mTempRect.left - scaleX < 0) {
                    this.mTempRect.left -= scaleX;
                } else {
                    this.mTempRect.right += scaleX;
                }
            }
            if (mightNeedToRefill && scaleY > 0) {
                this.mTempRect.top -= scaleY;
            }
            this.mTransformParams.setProgress(1.0f).setTargetAlpha(runningTaskView.getAlpha()).setSyncTransactionApplier(this.mSyncTransactionApplier).setTargetSet(this.mRecentsAnimationTargets);
        }
        return this.mTransformParams;
    }

    @Override // com.android.quickstep.views.RecentsView
    public void reset() {
        super.reset();
        int i = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).getInfo(0).rotation;
        setLayoutRotation(i, i);
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionStart(LauncherState toState) {
        setOverviewStateEnabled(toState.overviewUi);
        setFreezeViewVisibility(true);
        AbstractFloatingView.closeOpenViews(this.mActivity, false, 2048);
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionComplete(LauncherState finalState) {
        if (finalState == LauncherState.NORMAL || finalState == LauncherState.SPRING_LOADED) {
            reset();
        }
        if (finalState == LauncherState.OVERVIEW && getChildCount() != 0) {
            RecentGuideView.showGuide((Launcher) this.mActivity);
        }
        setOverlayEnabled(finalState == LauncherState.OVERVIEW || finalState == LauncherState.OVERVIEW_MODAL_TASK);
        setFreezeViewVisibility(false);
        launchSplitSelectedState();
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setOverviewStateEnabled(boolean enabled) {
        super.setOverviewStateEnabled(enabled);
        if (enabled) {
            setDisallowScrollToClearAll(!((((LauncherState) ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState()).getVisibleElements((Launcher) this.mActivity) & 64) != 0));
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    protected boolean shouldStealTouchFromSiblingsBelow(MotionEvent ev) {
        if (ev.getAction() == 0) {
            Hotseat hotseat = ((BaseQuickstepLauncher) this.mActivity).getHotseat();
            return !(hotseat.isShown() && ((BaseQuickstepLauncher) this.mActivity).getDragLayer().isEventOverView(hotseat, ev, this));
        }
        return super.shouldStealTouchFromSiblingsBelow(ev);
    }

    @Override // com.android.quickstep.views.RecentsView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).addPluginListener(this.mRecentsExtraCardPluginListener, RecentsExtraCard.class);
    }

    @Override // com.android.quickstep.views.RecentsView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).removePluginListener(this.mRecentsExtraCardPluginListener);
    }

    @Override // com.android.quickstep.views.RecentsView, com.android.launcher3.PagedView
    protected int computeMinScroll() {
        if (canComputeScrollX() && !this.mIsRtl) {
            return computeScrollX();
        }
        return super.computeMinScroll();
    }

    @Override // com.android.quickstep.views.RecentsView, com.android.launcher3.PagedView
    protected int computeMaxScroll() {
        if (canComputeScrollX() && this.mIsRtl) {
            return computeScrollX();
        }
        return super.computeMaxScroll();
    }

    private boolean canComputeScrollX() {
        return (this.mRecentsExtraCardPlugin == null || getTaskViewCount() <= 0 || this.mDisallowScrollToClearAll) ? false : true;
    }

    private int computeScrollX() {
        int taskViewStartIndex = getTaskViewStartIndex() - 1;
        while (taskViewStartIndex >= 0 && (getChildAt(taskViewStartIndex) instanceof RecentsExtraViewContainer) && ((RecentsExtraViewContainer) getChildAt(taskViewStartIndex)).isScrollable()) {
            taskViewStartIndex--;
        }
        return getScrollForPage(taskViewStartIndex + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createRecentsExtraCard() {
        this.mRecentsExtraViewContainer = new RecentsExtraViewContainer(getContext());
        this.mRecentsExtraViewContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.mRecentsExtraViewContainer.setScrollable(true);
        addView(this.mRecentsExtraViewContainer, 0);
    }

    @Override // com.android.quickstep.views.RecentsView
    public boolean hasRecentsExtraCard() {
        return this.mRecentsExtraViewContainer != null;
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setContentAlpha(float alpha) {
        super.setContentAlpha(alpha);
        RecentsExtraViewContainer recentsExtraViewContainer = this.mRecentsExtraViewContainer;
        if (recentsExtraViewContainer != null) {
            recentsExtraViewContainer.setAlpha(alpha);
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    protected DepthController getDepthController() {
        return ((BaseQuickstepLauncher) this.mActivity).getDepthController();
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setModalStateEnabled(boolean isModalState) {
        super.setModalStateEnabled(isModalState);
        if (isModalState) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_MODAL_TASK);
        } else if (((BaseQuickstepLauncher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK)) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW);
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    protected void onDismissAnimationEnds() {
        super.onDismissAnimationEnds();
        if (((BaseQuickstepLauncher) this.mActivity).isInState(LauncherState.OVERVIEW_SPLIT_SELECT)) {
            setTaskViewsPrimarySplitTranslation(this.mTaskViewsPrimarySplitTranslation);
            setTaskViewsSecondarySplitTranslation(this.mTaskViewsSecondarySplitTranslation);
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    public void initiateSplitSelect(TaskView taskView, int stagePosition) {
        super.initiateSplitSelect(taskView, stagePosition);
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        super.initiateSplitSelect(splitSelectSource);
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }

    @Override // com.android.quickstep.views.RecentsView
    public boolean isFastOverView() {
        return ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState() == LauncherState.QUICK_SWITCH || ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState() == LauncherState.BACKGROUND_APP;
    }
}
