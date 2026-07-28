package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
final class AppToOverviewAnimationProvider<T extends StatefulActivity<?>> extends RemoteAnimationProvider {
    private static final long RECENTS_LAUNCH_DURATION = 250;
    private static final String TAG = "AppToOverviewAnimationProvider";
    private T mActivity;
    private final BaseActivityInterface<?, T> mActivityInterface;
    private final RecentsAnimationDeviceState mDeviceState;
    private RecentsView mRecentsView;
    private final int mTargetTaskId;

    static /* synthetic */ void lambda$createWindowAnimation$1() {
    }

    long getRecentsLaunchDuration() {
        return RECENTS_LAUNCH_DURATION;
    }

    AppToOverviewAnimationProvider(BaseActivityInterface<?, T> activityInterface, int targetTaskId, RecentsAnimationDeviceState deviceState) {
        this.mActivityInterface = activityInterface;
        this.mTargetTaskId = targetTaskId;
        this.mDeviceState = deviceState;
    }

    boolean onActivityReady(T activity, Boolean wasVisible) {
        ((RecentsView) activity.getOverviewPanel()).showCurrentTask(this.mTargetTaskId);
        if (AbstractFloatingView.getTopOpenViewWithType(activity, 2048) != null) {
            AbstractFloatingView.closeAllOpenViewsExcept(activity, false, 2048);
        } else {
            AbstractFloatingView.closeAllOpenViews(activity, false);
        }
        BaseActivityInterface.AnimationFactory animationFactoryPrepareRecentsUI = this.mActivityInterface.prepareRecentsUI(this.mDeviceState, wasVisible.booleanValue(), new Consumer() { // from class: com.android.quickstep.-$$Lambda$AppToOverviewAnimationProvider$2IWInH3ogaxCJ-DKsxYeep1N0A8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AppToOverviewAnimationProvider.lambda$onActivityReady$0((AnimatorPlaybackController) obj);
            }
        });
        animationFactoryPrepareRecentsUI.createActivityInterface(RECENTS_LAUNCH_DURATION);
        animationFactoryPrepareRecentsUI.setRecentsAttachedToAppWindow(true, false);
        this.mActivity = activity;
        this.mRecentsView = (RecentsView) activity.getOverviewPanel();
        return false;
    }

    static /* synthetic */ void lambda$onActivityReady$0(AnimatorPlaybackController animatorPlaybackController) {
        animatorPlaybackController.dispatchOnStart();
        animatorPlaybackController.getAnimationPlayer().end();
    }

    @Override // com.android.quickstep.util.RemoteAnimationProvider
    public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
        Interpolator interpolatorClampToProgress;
        PendingAnimation pendingAnimation = new PendingAnimation(RECENTS_LAUNCH_DURATION);
        T t = this.mActivity;
        if (t == null) {
            Log.e(TAG, "Animation created, before activity");
            return pendingAnimation.buildAnim();
        }
        if (t.getRootView() == null || this.mActivity.getRootView().getViewRootImpl() == null) {
            Log.e(TAG, "Animation created, before activity view");
            return pendingAnimation.buildAnim();
        }
        this.mRecentsView.setRunningTaskIconScaledDown(true);
        pendingAnimation.addListener(new AnimationSuccessListener() { // from class: com.android.quickstep.AppToOverviewAnimationProvider.1
            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                AppToOverviewAnimationProvider.this.mActivityInterface.onSwipeUpToRecentsComplete();
                AppToOverviewAnimationProvider.this.mRecentsView.animateUpRunningTaskIconScale();
                AppToOverviewAnimationProvider.this.mRecentsView.removeExcludeApp();
            }
        });
        DepthController depthController = this.mActivityInterface.getDepthController();
        if (depthController != null) {
            pendingAnimation.addFloat(depthController, DepthController.DEPTH, LauncherState.BACKGROUND_APP.getDepth(this.mActivity), LauncherState.OVERVIEW.getDepth(this.mActivity), Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        }
        RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], 1);
        RemoteAnimationTargetCompat remoteAnimationTargetCompatFindTask = remoteAnimationTargets.findTask(this.mTargetTaskId);
        if (remoteAnimationTargetCompatFindTask == null) {
            Log.e(TAG, "No closing app");
            return pendingAnimation.buildAnim();
        }
        final TaskViewSimulator taskViewSimulator = new TaskViewSimulator(this.mActivity, this.mRecentsView.getSizeStrategy());
        taskViewSimulator.setRecentsConfiguration(this.mActivity.getResources().getConfiguration());
        taskViewSimulator.setDp(this.mActivity.getDeviceProfile());
        taskViewSimulator.setPreview(remoteAnimationTargetCompatFindTask);
        taskViewSimulator.setLayoutRotation(this.mRecentsView.getPagedViewOrientedState().getTouchRotation(), this.mRecentsView.getPagedViewOrientedState().getDisplayRotation());
        final TransformParams syncTransactionApplier = new TransformParams().setTargetSet(remoteAnimationTargets).setSyncTransactionApplier(new SurfaceTransactionApplier(this.mActivity.getRootView()));
        final AnimatedFloat animatedFloat = new AnimatedFloat(new Runnable() { // from class: com.android.quickstep.-$$Lambda$AppToOverviewAnimationProvider$T_Wl6R7ELSONL-Z3WSWcmzHq2Bo
            @Override // java.lang.Runnable
            public final void run() {
                AppToOverviewAnimationProvider.lambda$createWindowAnimation$1();
            }
        });
        syncTransactionApplier.setBaseBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$AppToOverviewAnimationProvider$3z7LsWFCiNWH3iWqFuQu5Lybljc
            @Override // com.android.quickstep.util.TransformParams.BuilderProxy
            public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                builder.withAlpha(animatedFloat.value);
            }
        });
        if (remoteAnimationTargets.isAnimatingHome()) {
            syncTransactionApplier.setHomeBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$AppToOverviewAnimationProvider$yR_pyG5R4Z5eUBuwqueIslqdXi0
                @Override // com.android.quickstep.util.TransformParams.BuilderProxy
                public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                    builder.withAlpha(1.0f - transformParams.getProgress());
                }
            });
            interpolatorClampToProgress = Interpolators.TOUCH_RESPONSE_INTERPOLATOR;
            pendingAnimation.addFloat(animatedFloat, AnimatedFloat.VALUE, 0.0f, 1.0f, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        } else {
            interpolatorClampToProgress = Interpolators.clampToProgress(Interpolators.TOUCH_RESPONSE_INTERPOLATOR, 0.0f, 0.8f);
            pendingAnimation.addFloat(animatedFloat, AnimatedFloat.VALUE, 0.0f, 1.0f, Interpolators.clampToProgress(Interpolators.TOUCH_RESPONSE_INTERPOLATOR, 0.8f, 1.0f));
        }
        pendingAnimation.addFloat(syncTransactionApplier, TransformParams.PROGRESS, 0.0f, 1.0f, interpolatorClampToProgress);
        taskViewSimulator.addAppToOverviewAnim(pendingAnimation, interpolatorClampToProgress);
        pendingAnimation.addOnFrameCallback(new Runnable() { // from class: com.android.quickstep.-$$Lambda$AppToOverviewAnimationProvider$KN13RBgSNvCvo_ZBPekp6dyAckY
            @Override // java.lang.Runnable
            public final void run() {
                taskViewSimulator.apply(syncTransactionApplier);
            }
        });
        return pendingAnimation.buildAnim();
    }
}
