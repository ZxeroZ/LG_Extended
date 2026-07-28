package com.android.quickstep;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.ShelfPeekAnim;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseActivityInterface<STATE_TYPE extends BaseState<STATE_TYPE>, ACTIVITY_TYPE extends StatefulActivity<STATE_TYPE>> {
    private static final String TAG = "BaseActivityInterface";
    private final STATE_TYPE mBackgroundState;
    public int mDisplayId = 0;
    private boolean mNeedLaunchSplitSelectState = false;
    private final STATE_TYPE mOverviewState;
    public final boolean rotationSupportedByActivity;

    public interface AnimationFactory {
        void createActivityInterface(long transitionLength);

        default void onTransitionCancelled() {
        }

        default void setRecentsAttachedToAppWindow(boolean attached, boolean animate) {
        }

        default void setShelfState(ShelfPeekAnim.ShelfAnimState animState, Interpolator interpolator, long duration) {
        }
    }

    public abstract boolean allowMinimizeSplitScreen();

    public void closeOverlay() {
    }

    public abstract ActivityInitListener createActivityInitListener(Predicate<Boolean> onInitListener);

    public abstract int getContainerType();

    public abstract ACTIVITY_TYPE getCreatedActivity();

    public DepthController getDepthController() {
        return null;
    }

    protected abstract float getExtraSpace(Context context, DeviceProfile dp, PagedOrientationHandler orientedState);

    public abstract Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target);

    public abstract int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context, Rect outRect, PagedOrientationHandler orientationHandler);

    public abstract <T extends RecentsView> T getVisibleRecentsView();

    public abstract boolean isInLiveTileMode();

    public abstract void onAssistantVisibilityChanged(float visibility);

    public abstract void onExitOverview(RecentsAnimationDeviceState deviceState, Runnable exitRunnable);

    public abstract void onLaunchTaskFailed();

    public abstract void onSwipeUpToHomeComplete(RecentsAnimationDeviceState deviceState);

    public abstract AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState deviceState, boolean activityVisible, Consumer<AnimatorPlaybackController> callback);

    public void setOnDeferredActivityLaunchCallback(Runnable r) {
    }

    public boolean shouldCancelCurrentGesture() {
        return false;
    }

    public abstract boolean switchToRecentsIfVisible(Runnable onCompleteCallback);

    public void updateOverviewPredictionState() {
    }

    protected BaseActivityInterface(boolean rotationSupportedByActivity, STATE_TYPE overviewState, STATE_TYPE backgroundState) {
        this.rotationSupportedByActivity = rotationSupportedByActivity;
        this.mOverviewState = overviewState;
        this.mBackgroundState = backgroundState;
    }

    public void onTransitionCancelled(boolean activityVisible) {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.getStateManager().goToState(createdActivity.getStateManager().getRestState(), activityVisible);
    }

    public void onSwipeUpToRecentsComplete() {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.getStateManager().reapplyState();
    }

    public final boolean isResumed() {
        StatefulActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.hasBeenResumed();
    }

    public final boolean isStarted() {
        StatefulActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.isStarted();
    }

    public boolean deferStartingActivity(RecentsAnimationDeviceState deviceState, MotionEvent ev) {
        return deviceState.isInDeferredGestureRegion(ev);
    }

    public void onLaunchTaskSuccess() {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        createdActivity.getStateManager().moveToRestState();
    }

    public void switchRunningTaskViewToScreenshot(ThumbnailData thumbnailData, Runnable runnable) {
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        RecentsView recentsView = (RecentsView) createdActivity.getOverviewPanel();
        if (recentsView != null) {
            recentsView.switchToScreenshot(thumbnailData, runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public final void calculateTaskSize(Context context, DeviceProfile dp, Rect outRect, PagedOrientationHandler orientedState) {
        calculateTaskSize(context, dp, getExtraSpace(context, dp, orientedState), outRect, orientedState);
    }

    private void calculateTaskSize(Context context, DeviceProfile dp, float extraVerticalSpace, Rect outRect, PagedOrientationHandler orientationHandler) {
        int i;
        Resources resources = context.getResources();
        boolean z = showOverviewActions(context) || SysUINavigationMode.hideShelfInTwoButtonLandscape(context, orientationHandler);
        if (dp.isMultiWindowMode) {
            i = R.dimen.multi_window_task_card_horz_space;
        } else {
            i = dp.isVerticalBarLayout() ? R.dimen.landscape_task_card_horz_space : z ? R.dimen.portrait_task_card_horz_space_big_overview : R.dimen.portrait_task_card_horz_space;
        }
        calculateTaskSizeInternal(context, dp, extraVerticalSpace, resources.getDimension(i), z ? 0.0f : resources.getDimension(R.dimen.task_card_vert_space), resources.getDimension(R.dimen.task_thumbnail_top_margin), outRect);
    }

    private void calculateTaskSizeInternal(Context context, DeviceProfile dp, float extraVerticalSpace, float paddingHorz, float paddingVert, float topIconMargin, Rect outRect) {
        float f;
        float f2;
        Rect insets = dp.getInsets();
        if (dp.isMultiWindowMode) {
            WindowBounds secondaryWindowBounds = SplitScreenBounds.INSTANCE.getSecondaryWindowBounds(context);
            f = secondaryWindowBounds.availableSize.x;
            f2 = secondaryWindowBounds.availableSize.y;
            if (WindowUtils.isHideNav(context) && !dp.isLandscape) {
                f2 += dp.getInsets().bottom;
            }
        } else {
            f = dp.availableWidthPx;
            f2 = dp.availableHeightPx;
        }
        Resources resources = context.getResources();
        float dimension = resources.getDimension(R.dimen.recent_clear_all_height);
        float f3 = ((((dp.heightPx - insets.top) - insets.bottom) - topIconMargin) - extraVerticalSpace) - dimension;
        float f4 = (dp.widthPx - insets.left) - insets.right;
        float fMin = Math.min((f4 - paddingHorz) / f, f3 / f2);
        if (!dp.isMultiWindowMode) {
            fMin = Math.min(fMin, dp.isLandscape ? context.getResources().getFloat(R.dimen.land_taskview_scale) : context.getResources().getFloat(R.dimen.port_taskview_scale));
        }
        float f5 = f * fMin;
        float f6 = fMin * f2;
        float f7 = insets.left + ((f4 - f5) / 2.0f);
        float dimension2 = insets.top + topIconMargin + ((f3 - f6) / 2.0f);
        boolean z = this instanceof FallbackActivityInterface;
        if (dp.mDisplayId != 4 && z && (dp.isLandscape || dp.isMultiWindowMode)) {
            dimension2 += (insets.top + insets.bottom) / 2;
            if (dp.mIsMultiDisplay) {
                dimension2 += WindowUtils.getStatusBarHeight(context);
            }
        }
        if (!z && dp.isLandscape) {
            dimension2 += resources.getDimension(R.dimen.task_thumbnail_top_margin_land);
        }
        outRect.set(Math.round(f7), Math.round(dimension2), Math.round(f7) + Math.round(f5), Math.round(dimension2) + Math.round(f6));
    }

    public final void calculateModalTaskSize(Context context, DeviceProfile dp, Rect outRect) {
        int i;
        Resources resources = context.getResources();
        if (dp.isMultiWindowMode) {
            i = R.dimen.multi_window_task_card_horz_space;
        } else {
            i = dp.isVerticalBarLayout() ? R.dimen.landscape_task_card_horz_space : R.dimen.portrait_modal_task_card_horz_space;
        }
        calculateTaskSizeInternal(context, dp, getOverviewActionsHeight(context), resources.getDimension(i), 0.0f, 0.0f, outRect);
    }

    public final float getOverviewActionsHeight(Context context) {
        int dimensionPixelSize;
        Resources resources = context.getResources();
        if (SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.THREE_BUTTONS) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.overview_actions_bottom_margin_three_button);
        } else {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.overview_actions_bottom_margin_gesture);
        }
        return dimensionPixelSize + resources.getDimensionPixelSize(R.dimen.overview_actions_height);
    }

    class DefaultAnimationFactory implements AnimationFactory {
        protected final ACTIVITY_TYPE mActivity;
        private final Consumer<AnimatorPlaybackController> mCallback;
        private boolean mIsAttachedToWindow;
        private final STATE_TYPE mStartState;

        DefaultAnimationFactory(Consumer<AnimatorPlaybackController> consumer) {
            this.mCallback = consumer;
            ACTIVITY_TYPE activity_type = (ACTIVITY_TYPE) BaseActivityInterface.this.getCreatedActivity();
            this.mActivity = activity_type;
            this.mStartState = (STATE_TYPE) activity_type.getStateManager().getState();
        }

        protected ACTIVITY_TYPE initUI() {
            STATE_TYPE state_type = this.mStartState;
            if (state_type.shouldDisableRestore()) {
                state_type = (STATE_TYPE) this.mActivity.getStateManager().getRestState();
            }
            this.mActivity.getStateManager().setRestState(state_type);
            this.mActivity.getStateManager().goToState(BaseActivityInterface.this.mBackgroundState, false);
            return this.mActivity;
        }

        @Override // com.android.quickstep.BaseActivityInterface.AnimationFactory
        public void createActivityInterface(long transitionLength) {
            PendingAnimation pendingAnimation = new PendingAnimation(transitionLength * 2);
            createBackgroundToOverviewAnim(this.mActivity, pendingAnimation);
            final AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimation.createPlaybackController();
            this.mActivity.getStateManager().setCurrentUserControlledAnimation(animatorPlaybackControllerCreatePlaybackController);
            animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseActivityInterface$DefaultAnimationFactory$zwWRfzhgI9bAdMy2wtlWajOUNH4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createActivityInterface$0$BaseActivityInterface$DefaultAnimationFactory(animatorPlaybackControllerCreatePlaybackController);
                }
            });
            this.mCallback.accept(animatorPlaybackControllerCreatePlaybackController);
            if (SysUINavigationMode.getMode(this.mActivity) == SysUINavigationMode.Mode.NO_BUTTON) {
                setRecentsAttachedToAppWindow(this.mIsAttachedToWindow, false);
            }
        }

        public /* synthetic */ void lambda$createActivityInterface$0$BaseActivityInterface$DefaultAnimationFactory(AnimatorPlaybackController animatorPlaybackController) {
            this.mActivity.getStateManager().goToState(((double) animatorPlaybackController.getInterpolatedProgress()) > 0.5d ? BaseActivityInterface.this.mOverviewState : BaseActivityInterface.this.mBackgroundState, false);
        }

        @Override // com.android.quickstep.BaseActivityInterface.AnimationFactory
        public void onTransitionCancelled() {
            this.mActivity.getStateManager().goToState((BaseState) this.mStartState, false);
        }

        @Override // com.android.quickstep.BaseActivityInterface.AnimationFactory
        public void setRecentsAttachedToAppWindow(boolean attached, boolean animate) {
            boolean z;
            if (this.mIsAttachedToWindow == attached && animate) {
                return;
            }
            ACTIVITY_TYPE activity_type = this.mActivity;
            if ((activity_type instanceof Launcher) && (activity_type.getStateManager().getState() instanceof LauncherState)) {
                z = ((LauncherState) this.mActivity.getStateManager().getState()).overviewUi;
                LGLog.i(BaseActivityInterface.TAG, "[RecentsAnimation] fadeAnim : attached = (" + attached + ",  " + this.mIsAttachedToWindow + "), animate = " + animate + ", " + this.mActivity.getStateManager().getState());
            } else {
                z = true;
            }
            this.mIsAttachedToWindow = attached;
            RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
            StateManager<STATE_TYPE> stateManager = this.mActivity.getStateManager();
            float[] fArr = new float[1];
            fArr[0] = (attached && z) ? 1.0f : 0.0f;
            Animator animatorCreateStateElementAnimation = stateManager.createStateElementAnimation(0, fArr);
            float fFloatValue = attached ? 1.0f : 0.0f;
            float f = attached ? 0.0f : 1.0f;
            this.mActivity.getStateManager().cancelStateElementAnimation(1);
            if (!recentsView.isShown() && animate) {
                RecentsView.ADJACENT_PAGE_OFFSET.set(recentsView, Float.valueOf(fFloatValue));
            } else {
                fFloatValue = ((Float) RecentsView.ADJACENT_PAGE_OFFSET.get(recentsView)).floatValue();
            }
            if (!animate) {
                RecentsView.ADJACENT_PAGE_OFFSET.set(recentsView, Float.valueOf(f));
            } else {
                this.mActivity.getStateManager().createStateElementAnimation(1, fFloatValue, f).start();
            }
            animatorCreateStateElementAnimation.setInterpolator(attached ? Interpolators.INSTANT : Interpolators.ACCEL_2);
            animatorCreateStateElementAnimation.setDuration(animate ? 300L : 0L).start();
        }

        protected void createBackgroundToOverviewAnim(ACTIVITY_TYPE activity, PendingAnimation pa) {
            RecentsView recentsView = (RecentsView) activity.getOverviewPanel();
            pa.addFloat(recentsView, LauncherAnimUtils.SCALE_PROPERTY, recentsView.getMaxScaleForFullScreen(), 1.0f, Interpolators.LINEAR);
            pa.addFloat(recentsView, RecentsView.FULLSCREEN_PROGRESS, 1.0f, 0.0f, Interpolators.LINEAR);
        }
    }

    protected static boolean showOverviewActions(Context context) {
        return FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(context);
    }

    public void setDisplayId(int displayId) {
        this.mDisplayId = displayId;
    }

    public boolean getNeedLaunchSplitSelectState() {
        return this.mNeedLaunchSplitSelectState;
    }

    public void setNeedLaunchSplitSelectState(boolean needLaunchSplitSelectState) {
        this.mNeedLaunchSplitSelectState = needLaunchSplitSelectState;
    }

    public boolean getOverviewStateOrSplitSelectedEnable() {
        RecentsView recentsView;
        StatefulActivity createdActivity = getCreatedActivity();
        if (createdActivity == null || (recentsView = (RecentsView) createdActivity.getOverviewPanel()) == null) {
            return false;
        }
        return recentsView.getOverviewStateOrSplitSelectedEnable();
    }
}
