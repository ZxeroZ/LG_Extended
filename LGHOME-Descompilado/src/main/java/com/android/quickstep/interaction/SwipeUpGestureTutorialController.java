package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowInsets;
import android.view.WindowManager;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.OverviewComponentObserver;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.interaction.TutorialController;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;

/* JADX INFO: loaded from: classes.dex */
abstract class SwipeUpGestureTutorialController extends TutorialController {
    private float mFakeTaskViewRadius;
    private Rect mFakeTaskViewRect;
    private SwipeUpAnimationLogic.RunningWindowAnim mRunningWindowAnim;
    private final ViewSwipeUpAnimation mViewSwipeUpAnimation;

    SwipeUpGestureTutorialController(TutorialFragment tutorialFragment, TutorialController.TutorialType tutorialType) {
        super(tutorialFragment, tutorialType);
        this.mFakeTaskViewRect = new Rect();
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this.mContext);
        OverviewComponentObserver overviewComponentObserver = new OverviewComponentObserver(this.mContext, recentsAnimationDeviceState);
        ViewSwipeUpAnimation viewSwipeUpAnimation = new ViewSwipeUpAnimation(this.mContext, recentsAnimationDeviceState, new GestureState(overviewComponentObserver, -1));
        this.mViewSwipeUpAnimation = viewSwipeUpAnimation;
        overviewComponentObserver.onDestroy();
        recentsAnimationDeviceState.destroy();
        DeviceProfile deviceProfileCopy = InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).getDeviceProfile(this.mContext).copy(this.mContext);
        Insets insets = ((WindowManager) this.mContext.getSystemService(WindowManager.class)).getCurrentWindowMetrics().getWindowInsets().getInsets(WindowInsets.Type.systemBars());
        deviceProfileCopy.updateInsets(new Rect(insets.left, insets.top, insets.right, insets.bottom));
        viewSwipeUpAnimation.initDp(deviceProfileCopy);
        this.mFakeTaskViewRadius = QuickStepContract.getWindowCornerRadius(this.mContext);
        this.mFakeTaskView.setClipToOutline(true);
        this.mFakeTaskView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.quickstep.interaction.SwipeUpGestureTutorialController.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(SwipeUpGestureTutorialController.this.mFakeTaskViewRect, SwipeUpGestureTutorialController.this.mFakeTaskViewRadius);
            }
        });
    }

    private void cancelRunningAnimation() {
        SwipeUpAnimationLogic.RunningWindowAnim runningWindowAnim = this.mRunningWindowAnim;
        if (runningWindowAnim != null) {
            runningWindowAnim.cancel();
        }
        this.mRunningWindowAnim = null;
    }

    void fadeOutFakeTaskView(boolean toOverviewFirst, Runnable onEndRunnable) {
        hideFeedback();
        hideHandCoachingAnimation();
        cancelRunningAnimation();
        PendingAnimation pendingAnimation = new PendingAnimation(300L);
        final AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.android.quickstep.interaction.SwipeUpGestureTutorialController.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                SwipeUpGestureTutorialController.this.mFakeTaskView.setVisibility(4);
                SwipeUpGestureTutorialController.this.mFakeTaskView.setAlpha(1.0f);
                SwipeUpGestureTutorialController.this.mRunningWindowAnim = null;
            }
        };
        if (toOverviewFirst) {
            pendingAnimation.setFloat(this.mViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 1.0f, Interpolators.ACCEL);
            pendingAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.interaction.SwipeUpGestureTutorialController.3
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    PendingAnimation pendingAnimation2 = new PendingAnimation(300L);
                    pendingAnimation2.setViewAlpha(SwipeUpGestureTutorialController.this.mFakeTaskView, 0.0f, Interpolators.ACCEL);
                    pendingAnimation2.addListener(animatorListenerAdapter);
                    AnimatorSet animatorSetBuildAnim = pendingAnimation2.buildAnim();
                    animatorSetBuildAnim.setStartDelay(100L);
                    animatorSetBuildAnim.start();
                    SwipeUpGestureTutorialController.this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(animatorSetBuildAnim);
                }
            });
        } else {
            pendingAnimation.setViewAlpha(this.mFakeTaskView, 0.0f, Interpolators.ACCEL);
            pendingAnimation.addListener(animatorListenerAdapter);
        }
        if (onEndRunnable != null) {
            pendingAnimation.addListener(AnimationSuccessListener.forRunnable(onEndRunnable));
        }
        AnimatorSet animatorSetBuildAnim = pendingAnimation.buildAnim();
        animatorSetBuildAnim.start();
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(animatorSetBuildAnim);
    }

    void animateFakeTaskViewHome(PointF finalVelocity, final Runnable onEndRunnable) {
        hideFeedback();
        hideHandCoachingAnimation();
        cancelRunningAnimation();
        RectFSpringAnim rectFSpringAnimHandleSwipeUpToHome = this.mViewSwipeUpAnimation.handleSwipeUpToHome(finalVelocity);
        rectFSpringAnimHandleSwipeUpToHome.addAnimatorListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$SwipeUpGestureTutorialController$RvdsPqqfspBm_W4XK-C0-YHxgW8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$animateFakeTaskViewHome$0$SwipeUpGestureTutorialController(onEndRunnable);
            }
        }));
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(rectFSpringAnimHandleSwipeUpToHome);
    }

    public /* synthetic */ void lambda$animateFakeTaskViewHome$0$SwipeUpGestureTutorialController(Runnable runnable) {
        fadeOutFakeTaskView(false, runnable);
    }

    @Override // com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureAttemptCallback
    public void setNavBarGestureProgress(Float displacement) {
        if (displacement == null || this.mTutorialType == TutorialController.TutorialType.HOME_NAVIGATION_COMPLETE || this.mTutorialType == TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE) {
            this.mFakeTaskView.setVisibility(4);
            return;
        }
        this.mFakeTaskView.setVisibility(0);
        if (this.mRunningWindowAnim == null) {
            this.mViewSwipeUpAnimation.updateDisplacement(displacement.floatValue());
        }
    }

    class ViewSwipeUpAnimation extends SwipeUpAnimationLogic {
        ViewSwipeUpAnimation(Context context, RecentsAnimationDeviceState deviceState, GestureState gestureState) {
            super(context, deviceState, gestureState, new FakeTransformParams());
        }

        void initDp(DeviceProfile dp) {
            initTransitionEndpoints(dp);
            this.mTaskViewSimulator.setPreviewBounds(new Rect(0, 0, dp.widthPx, dp.heightPx), dp.getInsets());
        }

        @Override // com.android.quickstep.SwipeUpAnimationLogic
        public void updateFinalShift() {
            this.mWindowTransitionController.setPlayFraction(this.mCurrentShift.value / this.mDragLengthFactor);
            this.mTaskViewSimulator.apply(this.mTransformParams);
        }

        AnimatedFloat getCurrentShift() {
            return this.mCurrentShift;
        }

        RectFSpringAnim handleSwipeUpToHome(PointF velocity) {
            PointF pointF = new PointF(velocity.x, velocity.y);
            float f = this.mCurrentShift.value;
            float fBoundToRange = Utilities.boundToRange(f - ((pointF.y * DisplayController.getSingleFrameMs(this.mContext)) / this.mTransitionDragLength), 0.0f, this.mDragLengthFactor);
            final long jMin = Math.min(350L, ((long) Math.round(Math.abs(((1.0f - f) * this.mTransitionDragLength) / pointF.y))) * 2);
            RectFSpringAnim rectFSpringAnimCreateWindowAnimationToHome = createWindowAnimationToHome(fBoundToRange, new SwipeUpAnimationLogic.HomeAnimationFactory(null) { // from class: com.android.quickstep.interaction.SwipeUpGestureTutorialController.ViewSwipeUpAnimation.1
                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public AnimatorPlaybackController createActivityAnimationToHome() {
                    return AnimatorPlaybackController.wrap(new AnimatorSet(), jMin);
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public RectF getWindowTargetRect() {
                    return new RectF((ViewSwipeUpAnimation.this.mDp.widthPx - ViewSwipeUpAnimation.this.mDp.allAppsIconSizePx) / 2, ViewSwipeUpAnimation.this.mDp.heightPx - (ViewSwipeUpAnimation.this.mDp.allAppsCellHeightPx * 3), r1 + r0, r2 + r0);
                }
            });
            rectFSpringAnimCreateWindowAnimationToHome.start(this.mContext, pointF);
            return rectFSpringAnimCreateWindowAnimationToHome;
        }
    }

    private class FakeTransformParams extends TransformParams {
        private FakeTransformParams() {
        }

        @Override // com.android.quickstep.util.TransformParams
        public SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] createSurfaceParams(TransformParams.BuilderProxy proxy) {
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder((SurfaceControl) null);
            proxy.onBuildTargetParams(builder, null, this);
            return new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[]{builder.build()};
        }

        @Override // com.android.quickstep.util.TransformParams
        public void applySurfaceParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] params) {
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams = params[0];
            SwipeUpGestureTutorialController.this.mFakeTaskView.setAnimationMatrix(surfaceParams.matrix);
            SwipeUpGestureTutorialController.this.mFakeTaskViewRect.set(surfaceParams.windowCrop);
            SwipeUpGestureTutorialController.this.mFakeTaskViewRadius = surfaceParams.cornerRadius;
            SwipeUpGestureTutorialController.this.mFakeTaskView.invalidateOutline();
        }
    }
}
