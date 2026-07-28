package com.android.quickstep;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.R;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class FallbackActivityInterface extends BaseActivityInterface<RecentsState, RecentsActivity> {
    public static final FallbackActivityInterface INSTANCE = new FallbackActivityInterface();

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean allowMinimizeSplitScreen() {
        return false;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean isInLiveTileMode() {
        return false;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onAssistantVisibilityChanged(float visibility) {
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onExitOverview(RecentsAnimationDeviceState deviceState, Runnable exitRunnable) {
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean switchToRecentsIfVisible(Runnable onCompleteCallback) {
        return false;
    }

    private FallbackActivityInterface() {
        super(true, RecentsState.DEFAULT, RecentsState.BACKGROUND_APP);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context, Rect outRect, PagedOrientationHandler orientationHandler) {
        calculateTaskSize(context, dp, outRect, orientationHandler);
        if (dp.isVerticalBarLayout() && SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getMode() != SysUINavigationMode.Mode.NO_BUTTON) {
            Rect insets = dp.getInsets();
            return dp.hotseatBarSizePx + (dp.isSeascape() ? insets.left : insets.right);
        }
        return dp.heightPx - outRect.bottom;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onSwipeUpToHomeComplete(RecentsAnimationDeviceState deviceState) {
        onSwipeUpToRecentsComplete();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public BaseActivityInterface.AnimationFactory prepareRecentsUI(RecentsAnimationDeviceState deviceState, boolean activityVisible, Consumer<AnimatorPlaybackController> callback) {
        notifyRecentsOfOrientation(deviceState);
        BaseActivityInterface.DefaultAnimationFactory defaultAnimationFactory = new BaseActivityInterface.DefaultAnimationFactory(callback);
        defaultAnimationFactory.initUI();
        return defaultAnimationFactory;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public ActivityInitListener createActivityInitListener(final Predicate<Boolean> onInitListener) {
        return new ActivityInitListener(new BiPredicate() { // from class: com.android.quickstep.-$$Lambda$FallbackActivityInterface$CXahKmtaFMw2ukBnImLpTsdZGQc
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                return onInitListener.test((Boolean) obj2);
            }
        }, RecentsActivity.ACTIVITY_TRACKER);
    }

    /* JADX DEBUG: Method merged with bridge method: getCreatedActivity()Lcom/android/launcher3/statemanager/StatefulActivity; */
    @Override // com.android.quickstep.BaseActivityInterface
    public RecentsActivity getCreatedActivity() {
        return (RecentsActivity) RecentsActivity.ACTIVITY_TRACKER.getCreatedActivity();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public RecentsView getVisibleRecentsView() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity == null || !createdActivity.hasWindowFocus()) {
            return null;
        }
        return (RecentsView) createdActivity.getOverviewPanel();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target) {
        return target.screenSpaceBounds;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean deferStartingActivity(RecentsAnimationDeviceState deviceState, MotionEvent ev) {
        return !deviceState.isFullyGesturalNavMode() || super.deferStartingActivity(deviceState, ev);
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public int getContainerType() {
        RecentsActivity createdActivity = getCreatedActivity();
        return createdActivity != null && createdActivity.isStarted() && createdActivity.hasWindowFocus() ? 15 : 13;
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public void onLaunchTaskFailed() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity == null) {
            return;
        }
        ((RecentsView) createdActivity.getOverviewPanel()).startHome();
    }

    @Override // com.android.quickstep.BaseActivityInterface
    protected float getExtraSpace(Context context, DeviceProfile dp, PagedOrientationHandler orientationHandler) {
        return dp.mDisplayId == 4 ? context.getResources().getDimensionPixelSize(R.dimen.recent_clear_all_gap) : context.getResources().getDimensionPixelSize(R.dimen.recommand_app_layout_height);
    }

    private void notifyRecentsOfOrientation(RecentsAnimationDeviceState deviceState) {
        ((RecentsView) getCreatedActivity().getOverviewPanel()).setLayoutRotation(deviceState.getCurrentActiveRotation(this.mDisplayId), deviceState.getDisplayRotation(this.mDisplayId));
    }

    @Override // com.android.quickstep.BaseActivityInterface
    public boolean getOverviewStateOrSplitSelectedEnable() {
        RecentsActivity createdActivity = getCreatedActivity();
        if (createdActivity == null || ((RecentsView) createdActivity.getOverviewPanel()) == null) {
            return false;
        }
        return createdActivity.hasBeenResumed();
    }
}
