package com.android.quickstep;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.views.FloatingIconView;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public abstract class SwipeUpAnimationLogic {
    protected float mAlphaOnEnd;
    protected final Context mContext;
    protected final RecentsAnimationDeviceState mDeviceState;
    protected DeviceProfile mDp;
    protected final GestureState mGestureState;
    protected final TaskViewSimulator mTaskViewSimulator;
    protected final TransformParams mTransformParams;
    protected int mTransitionDragLength;
    protected AnimatorPlaybackController mWindowTransitionController;
    protected static final Rect TEMP_RECT = new Rect();
    private static final Interpolator PULLBACK_INTERPOLATOR = Interpolators.DEACCEL;
    protected final AnimatedFloat mCurrentShift = new AnimatedFloat(new Runnable() { // from class: com.android.quickstep.-$$Lambda$IgohBKxk_K7oznzdWzDkqnK6XEU
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.updateFinalShift();
        }
    });
    protected float mDragLengthFactor = 1.0f;
    private float mDragLengthFactorStartPullback = 1.0f;
    private float mDragLengthFactorMaxPullback = 1.0f;

    public abstract void updateFinalShift();

    public SwipeUpAnimationLogic(Context context, RecentsAnimationDeviceState deviceState, GestureState gestureState, TransformParams transformParams) {
        this.mContext = context;
        this.mDeviceState = deviceState;
        this.mGestureState = gestureState;
        TaskViewSimulator taskViewSimulator = new TaskViewSimulator(context, gestureState.getActivityInterface());
        this.mTaskViewSimulator = taskViewSimulator;
        this.mTransformParams = transformParams;
        taskViewSimulator.setLayoutRotation(deviceState.getCurrentActiveRotation(gestureState.getDisplayId()), deviceState.getDisplayRotation(gestureState.getDisplayId()));
    }

    protected void initTransitionEndpoints(DeviceProfile dp) {
        this.mDp = dp;
        this.mTaskViewSimulator.setDp(dp);
        this.mTransitionDragLength = this.mGestureState.getActivityInterface().getSwipeUpDestinationAndLength(dp, this.mContext, TEMP_RECT, this.mTaskViewSimulator.getOrientationState().getOrientationHandler());
        if (this.mDeviceState.isFullyGesturalNavMode()) {
            this.mDragLengthFactor = dp.heightPx / this.mTransitionDragLength;
            float fullScreenScale = this.mTaskViewSimulator.getFullScreenScale();
            float f = 1.0f - fullScreenScale;
            this.mDragLengthFactorStartPullback = (0.75f - fullScreenScale) / f;
            this.mDragLengthFactorMaxPullback = (0.5f - fullScreenScale) / f;
        } else {
            this.mDragLengthFactor = 1.0f;
            this.mDragLengthFactorMaxPullback = 1.0f;
            this.mDragLengthFactorStartPullback = 1.0f;
        }
        PendingAnimation pendingAnimation = new PendingAnimation(this.mTransitionDragLength * 2);
        this.mTaskViewSimulator.addAppToOverviewAnim(pendingAnimation, new TimeInterpolator() { // from class: com.android.quickstep.-$$Lambda$SwipeUpAnimationLogic$qBpJNdpRFvlPX3YV589FElzo84M
            @Override // android.animation.TimeInterpolator
            public final float getInterpolation(float f2) {
                return this.f$0.lambda$initTransitionEndpoints$0$SwipeUpAnimationLogic(f2);
            }
        });
        this.mWindowTransitionController = pendingAnimation.createPlaybackController();
    }

    public /* synthetic */ float lambda$initTransitionEndpoints$0$SwipeUpAnimationLogic(float f) {
        return f * this.mDragLengthFactor;
    }

    public void updateDisplacement(float displacement) {
        float f = -displacement;
        int i = this.mTransitionDragLength;
        float f2 = this.mDragLengthFactor;
        if (f <= i * f2 || i <= 0) {
            float fMax = Math.max(f, 0.0f);
            int i2 = this.mTransitionDragLength;
            f2 = i2 == 0 ? 0.0f : fMax / i2;
            float f3 = this.mDragLengthFactorStartPullback;
            if (f2 > f3) {
                float interpolation = PULLBACK_INTERPOLATOR.getInterpolation(Utilities.getProgress(f2, f3, this.mDragLengthFactor));
                float f4 = this.mDragLengthFactorStartPullback;
                f2 = f4 + (interpolation * (this.mDragLengthFactorMaxPullback - f4));
            }
        }
        this.mCurrentShift.updateValue(f2);
    }

    protected PagedOrientationHandler getOrientationHandler() {
        return this.mTaskViewSimulator.getOrientationState().getOrientationHandler();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract class HomeAnimationFactory {
        public FloatingIconView mIconView;

        public abstract AnimatorPlaybackController createActivityAnimationToHome();

        public boolean needToForceEnd() {
            return false;
        }

        public void playAtomicAnimation(float velocity) {
        }

        public HomeAnimationFactory(FloatingIconView iconView) {
            this.mIconView = iconView;
        }

        public RectF getWindowTargetRect() {
            PagedOrientationHandler orientationHandler = SwipeUpAnimationLogic.this.getOrientationHandler();
            float fIntValue = ((Integer) orientationHandler.getPrimaryValue(Integer.valueOf(r1.availableWidthPx), Integer.valueOf(r1.availableHeightPx))).intValue() / 2.0f;
            float fIntValue2 = ((Integer) orientationHandler.getSecondaryValue(Integer.valueOf(r1.availableWidthPx), Integer.valueOf(r1.availableHeightPx))).intValue() - r1.hotseatBarSizePx;
            float f = SwipeUpAnimationLogic.this.mDp.iconSizePx / 2;
            return new RectF(fIntValue - f, fIntValue2 - f, fIntValue + f, fIntValue2 + f);
        }
    }

    protected RectFSpringAnim createWindowAnimationToHome(float startProgress, HomeAnimationFactory homeAnimationFactory) {
        RectF windowTargetRect = homeAnimationFactory.getWindowTargetRect();
        FloatingIconView floatingIconView = homeAnimationFactory.mIconView;
        boolean z = floatingIconView != null;
        this.mWindowTransitionController.setPlayFraction(startProgress / this.mDragLengthFactor);
        this.mTaskViewSimulator.apply(this.mTransformParams.setProgress(startProgress));
        RectF rectF = new RectF(this.mTaskViewSimulator.getCurrentCropRect());
        Matrix matrix = new Matrix();
        this.mTaskViewSimulator.applyWindowToHomeRotation(matrix);
        RectF rectF2 = new RectF(rectF);
        this.mTaskViewSimulator.getCurrentMatrix().mapRect(rectF2);
        Matrix matrix2 = new Matrix();
        matrix.invert(matrix2);
        matrix2.mapRect(rectF2);
        final RectFSpringAnim rectFSpringAnim = new RectFSpringAnim(rectF2, windowTargetRect, this.mContext);
        if (z) {
            rectFSpringAnim.addAnimatorListener(floatingIconView);
            Objects.requireNonNull(rectFSpringAnim);
            floatingIconView.setOnTargetChangeListener(new Runnable() { // from class: com.android.quickstep.-$$Lambda$EdqqUbh7Zsa0F6Au_MsWeX9LrR0
                @Override // java.lang.Runnable
                public final void run() {
                    rectFSpringAnim.onTargetPositionChanged();
                }
            });
            Objects.requireNonNull(rectFSpringAnim);
            floatingIconView.setFastFinishRunnable(new Runnable() { // from class: com.android.quickstep.-$$Lambda$8cBo6ytMV69g67Y7lAihR9nU-EU
                @Override // java.lang.Runnable
                public final void run() {
                    rectFSpringAnim.end();
                }
            });
        }
        SpringAnimationRunner springAnimationRunner = new SpringAnimationRunner(homeAnimationFactory, rectF, matrix);
        rectFSpringAnim.addOnUpdateListener(springAnimationRunner);
        rectFSpringAnim.addAnimatorListener(springAnimationRunner);
        return rectFSpringAnim;
    }

    protected float getWindowAlpha(float progress) {
        float f = this.mAlphaOnEnd;
        if (progress <= 0.0f) {
            return 1.0f;
        }
        if (progress >= f) {
            return 0.0f;
        }
        return Utilities.mapToRange(progress, 0.0f, f, 1.0f, 0.0f, Interpolators.ACCEL_1_5);
    }

    protected class SpringAnimationRunner extends AnimationSuccessListener implements RectFSpringAnim.OnUpdateListener, TransformParams.BuilderProxy {
        final Rect mCropRect;
        final RectF mCropRectF;
        final float mEndRadius;
        final FloatingIconView mFIV;
        final AnimatorPlaybackController mHomeAnim;
        final Matrix mHomeToWindowPositionMap;
        final Matrix mMatrix;
        final float mStartRadius;
        final float mWindowAlphaThreshold;
        final RectF mWindowCurrentRect;

        SpringAnimationRunner(HomeAnimationFactory factory, RectF cropRectF, Matrix homeToWindowPositionMap) {
            Rect rect = new Rect();
            this.mCropRect = rect;
            this.mMatrix = new Matrix();
            this.mWindowCurrentRect = new RectF();
            this.mHomeAnim = factory.createActivityAnimationToHome();
            this.mCropRectF = cropRectF;
            this.mHomeToWindowPositionMap = homeToWindowPositionMap;
            cropRectF.roundOut(rect);
            FloatingIconView floatingIconView = factory.mIconView;
            this.mFIV = floatingIconView;
            float currentCornerRadius = SwipeUpAnimationLogic.this.mTaskViewSimulator.getCurrentCornerRadius();
            this.mStartRadius = currentCornerRadius;
            this.mEndRadius = currentCornerRadius;
            this.mWindowAlphaThreshold = floatingIconView != null ? 0.9f : 1.0f;
        }

        @Override // com.android.quickstep.util.RectFSpringAnim.OnUpdateListener
        public void onUpdate(RectF currentRect, float progress) {
            this.mHomeAnim.setPlayFraction(progress);
            this.mHomeToWindowPositionMap.mapRect(this.mWindowCurrentRect, currentRect);
            this.mMatrix.setRectToRect(this.mCropRectF, this.mWindowCurrentRect, Matrix.ScaleToFit.FILL);
            float fMapRange = Utilities.mapRange(progress, this.mStartRadius, this.mEndRadius);
            SwipeUpAnimationLogic.this.mTransformParams.setTargetAlpha(SwipeUpAnimationLogic.this.getWindowAlpha(progress)).setCornerRadius(fMapRange);
            SwipeUpAnimationLogic.this.mTransformParams.applySurfaceParams(SwipeUpAnimationLogic.this.mTransformParams.createSurfaceParams(this));
            FloatingIconView floatingIconView = this.mFIV;
            if (floatingIconView != null) {
                floatingIconView.update(currentRect, 1.0f, progress, this.mWindowAlphaThreshold, this.mMatrix.mapRadius(fMapRange), FloatingIconView.Action.SwipeUpClose);
            }
        }

        @Override // com.android.quickstep.util.TransformParams.BuilderProxy
        public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
            builder.withMatrix(this.mMatrix).withWindowCrop(this.mCropRect).withCornerRadius(params.getCornerRadius());
        }

        @Override // com.android.quickstep.util.RectFSpringAnim.OnUpdateListener
        public void onCancel() {
            FloatingIconView floatingIconView = this.mFIV;
            if (floatingIconView != null) {
                floatingIconView.fastFinish();
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            this.mHomeAnim.dispatchOnStart();
        }

        @Override // com.android.launcher3.anim.AnimationSuccessListener
        public void onAnimationSuccess(Animator animator) {
            this.mHomeAnim.getAnimationPlayer().end();
        }
    }

    public interface RunningWindowAnim {
        void cancel();

        void end();

        static RunningWindowAnim wrap(final Animator animator) {
            return new RunningWindowAnim() { // from class: com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim.1
                @Override // com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim
                public void end() {
                    animator.end();
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim
                public void cancel() {
                    animator.cancel();
                }
            };
        }

        static RunningWindowAnim wrap(final RectFSpringAnim rectFSpringAnim) {
            return new RunningWindowAnim() { // from class: com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim.2
                @Override // com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim
                public void end() {
                    rectFSpringAnim.end();
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.RunningWindowAnim
                public void cancel() {
                    rectFSpringAnim.cancel();
                }
            };
        }
    }
}
