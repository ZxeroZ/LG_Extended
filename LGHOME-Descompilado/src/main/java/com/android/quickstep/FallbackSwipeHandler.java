package com.android.quickstep;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.fallback.FallbackRecentsView;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGLog;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class FallbackSwipeHandler extends BaseSwipeUpHandlerV2<RecentsActivity, FallbackRecentsView> {
    private static final String TAG = "FallbackSwipeHandler";
    private FallbackHomeAnimationFactory mActiveAnimationFactory;
    boolean mIsPaused;
    private float mMaxLauncherScale;
    private final boolean mRunningOverHome;
    private final Matrix mTmpMatrix;

    public FallbackSwipeHandler(Context context, RecentsAnimationDeviceState deviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long touchTimeMs, boolean continuingLastGesture, InputConsumerController inputConsumer) {
        super(context, deviceState, taskAnimationManager, gestureState, touchTimeMs, continuingLastGesture, inputConsumer);
        this.mTmpMatrix = new Matrix();
        this.mMaxLauncherScale = 1.0f;
        this.mIsPaused = false;
        boolean zIsHomeTask = ActivityManagerWrapperEx.getInstance().isHomeTask(gestureState.getRunningTask(), gestureState.getDisplayId(), gestureState.getHomeIntent().getComponent().getClassName());
        this.mRunningOverHome = zIsHomeTask;
        if (zIsHomeTask) {
            this.mTransformParams.setHomeBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$FallbackSwipeHandler$G8NZ0zQCmZ4CTBjOBT_nteIHINA
                @Override // com.android.quickstep.util.TransformParams.BuilderProxy
                public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                    this.f$0.updateHomeActivityTransformDuringSwipeUp(builder, remoteAnimationTargetCompat, transformParams);
                }
            });
        }
    }

    @Override // com.android.quickstep.SwipeUpAnimationLogic
    protected void initTransitionEndpoints(DeviceProfile dp) {
        super.initTransitionEndpoints(dp);
        if (this.mRunningOverHome) {
            this.mMaxLauncherScale = 1.0f / this.mTaskViewSimulator.getFullScreenScale();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHomeActivityTransformDuringSwipeUp(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
        setHomeScaleAndAlpha(builder, app, this.mCurrentShift.value, Utilities.boundToRange(1.0f - this.mCurrentShift.value, 0.0f, 1.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHomeScaleAndAlpha(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, float verticalShift, float alpha) {
        this.mTmpMatrix.setScale(1.0f, 1.0f, app.localBounds.exactCenterX(), app.localBounds.exactCenterY());
        builder.withMatrix(this.mTmpMatrix).withAlpha(this.mIsPaused ? 0.0f : 1.0f);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(long duration) {
        this.mActiveAnimationFactory = new FallbackHomeAnimationFactory(120L);
        if (this.mRunningOverHome) {
            ActivityOptions activityOptionsMakeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
            activityOptionsMakeCustomAnimation.setLaunchDisplayId(this.mGestureState.getDisplayId());
            LGLog.d(TAG, "[RecentsAnimation] createHomeAnimationFactory : " + this.mGestureState.getDisplayId() + ", " + this.mGestureState.getHomeIntent());
            this.mContext.startActivity(new Intent(this.mGestureState.getHomeIntent()), activityOptionsMakeCustomAnimation.toBundle());
        }
        return this.mActiveAnimationFactory;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2, com.android.quickstep.BaseSwipeUpHandler
    protected boolean handleTaskAppeared(RemoteAnimationTargetCompat[] appearedTaskTarget) {
        FallbackHomeAnimationFactory fallbackHomeAnimationFactory = this.mActiveAnimationFactory;
        if (fallbackHomeAnimationFactory != null && fallbackHomeAnimationFactory.handleHomeTaskAppeared(appearedTaskTarget)) {
            this.mActiveAnimationFactory = null;
            return false;
        }
        return super.handleTaskAppeared(appearedTaskTarget);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected void finishRecentsControllerToHome(Runnable callback) {
        this.mIsPaused = false;
        this.mRecentsAnimationController.finish(!isRunningOver3PHome(), callback, true);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected void switchToScreenshot() {
        if (this.mRunningOverHome) {
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
        } else {
            super.switchToScreenshot();
        }
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected void notifyGestureAnimationStartToRecents() {
        if (this.mRunningOverHome) {
            ((FallbackRecentsView) this.mRecentsView).onGestureAnimationStartOnHome(this.mGestureState.getRunningTask());
        } else {
            super.notifyGestureAnimationStartToRecents();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class FallbackHomeAnimationFactory extends SwipeUpAnimationLogic.HomeAnimationFactory {
        private final long mDuration;
        private final AnimatedFloat mHomeAlpha;
        private final TransformParams mHomeAlphaParams;
        private final AnimatedFloat mRecentsAlpha;
        private final AnimatedFloat mVerticalShiftForScale;

        @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
        public void playAtomicAnimation(float velocity) {
        }

        FallbackHomeAnimationFactory(long duration) {
            super(null);
            TransformParams transformParams = new TransformParams();
            this.mHomeAlphaParams = transformParams;
            AnimatedFloat animatedFloat = new AnimatedFloat();
            this.mVerticalShiftForScale = animatedFloat;
            AnimatedFloat animatedFloat2 = new AnimatedFloat();
            this.mRecentsAlpha = animatedFloat2;
            this.mDuration = duration;
            if (FallbackSwipeHandler.this.mRunningOverHome) {
                AnimatedFloat animatedFloat3 = new AnimatedFloat();
                this.mHomeAlpha = animatedFloat3;
                animatedFloat3.value = 1.0f;
                animatedFloat.value = FallbackSwipeHandler.this.mCurrentShift.value;
                FallbackSwipeHandler.this.mTransformParams.setHomeBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$FallbackSwipeHandler$FallbackHomeAnimationFactory$987qRFqgHZsE8peEBab9Uefl8vI
                    @Override // com.android.quickstep.util.TransformParams.BuilderProxy
                    public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams2) {
                        this.f$0.updateHomeActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams2);
                    }
                });
            } else {
                AnimatedFloat animatedFloat4 = new AnimatedFloat(new Runnable() { // from class: com.android.quickstep.-$$Lambda$FallbackSwipeHandler$FallbackHomeAnimationFactory$zZE0TXdUFdVUlvtBQSpXM6SQ32g
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.updateHomeAlpha();
                    }
                });
                this.mHomeAlpha = animatedFloat4;
                animatedFloat4.value = 0.0f;
                transformParams.setHomeBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$FallbackSwipeHandler$FallbackHomeAnimationFactory$987qRFqgHZsE8peEBab9Uefl8vI
                    @Override // com.android.quickstep.util.TransformParams.BuilderProxy
                    public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams2) {
                        this.f$0.updateHomeActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams2);
                    }
                });
            }
            animatedFloat2.value = 0.0f;
            FallbackSwipeHandler.this.mTransformParams.setBaseBuilderProxy(new TransformParams.BuilderProxy() { // from class: com.android.quickstep.-$$Lambda$FallbackSwipeHandler$FallbackHomeAnimationFactory$TdfhA5WK8q1Zc3SHeZ14MDoP2IQ
                @Override // com.android.quickstep.util.TransformParams.BuilderProxy
                public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams2) {
                    this.f$0.updateRecentsActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams2);
                }
            });
        }

        @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
        public RectF getWindowTargetRect() {
            if (SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(FallbackSwipeHandler.this.mContext).isSplitScreenVisible()) {
                return new RectF(0.0f, 0.0f, 0.0f, 0.0f);
            }
            return super.getWindowTargetRect();
        }

        @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
        public boolean needToForceEnd() {
            return SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(FallbackSwipeHandler.this.mContext).isSplitScreenVisible();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateRecentsActivityTransformDuringHomeAnim(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
            builder.withAlpha(this.mRecentsAlpha.value);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateHomeActivityTransformDuringHomeAnim(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
            FallbackSwipeHandler.this.setHomeScaleAndAlpha(builder, app, this.mVerticalShiftForScale.value, this.mHomeAlpha.value);
        }

        @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
        public AnimatorPlaybackController createActivityAnimationToHome() {
            PendingAnimation pendingAnimation = new PendingAnimation(this.mDuration);
            pendingAnimation.setFloat(this.mRecentsAlpha, AnimatedFloat.VALUE, 0.0f, Interpolators.ACCEL);
            return pendingAnimation.createPlaybackController();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateHomeAlpha() {
            if (this.mHomeAlphaParams.getTargetSet() != null) {
                TransformParams transformParams = this.mHomeAlphaParams;
                transformParams.applySurfaceParams(transformParams.createSurfaceParams(TransformParams.BuilderProxy.NO_OP));
            }
        }

        public boolean handleHomeTaskAppeared(RemoteAnimationTargetCompat[] appearedTaskTargets) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = appearedTaskTargets[0];
            if (remoteAnimationTargetCompat.activityType != 2) {
                return false;
            }
            this.mHomeAlphaParams.setTargetSet(new RemoteAnimationTargets(new RemoteAnimationTargetCompat[]{remoteAnimationTargetCompat}, new RemoteAnimationTargetCompat[0], new RemoteAnimationTargetCompat[0], remoteAnimationTargetCompat.mode));
            updateHomeAlpha();
            return true;
        }
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2, com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        super.onRecentsAnimationCanceled(thumbnailDatas);
        this.mIsPaused = false;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationFinished(RecentsAnimationController controller) {
        super.onRecentsAnimationFinished(controller);
        this.mIsPaused = false;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2, com.android.quickstep.BaseSwipeUpHandler
    public void onMotionPauseChanged(boolean isPaused) {
        FallbackHomeAnimationFactory fallbackHomeAnimationFactory;
        super.onMotionPauseChanged(isPaused);
        if (!this.mIsPaused) {
            this.mIsPaused = isPaused;
        }
        if (!isPaused || (fallbackHomeAnimationFactory = this.mActiveAnimationFactory) == null) {
            return;
        }
        fallbackHomeAnimationFactory.mRecentsAlpha.value = 1.0f;
        this.mActiveAnimationFactory.mHomeAlpha.value = 0.0f;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public boolean isRunningOver3PHome() {
        return this.mRunningOverHome;
    }
}
