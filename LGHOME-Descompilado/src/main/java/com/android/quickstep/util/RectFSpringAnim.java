package com.android.quickstep.util;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.FlingSpringAnim;
import com.android.launcher3.util.DynamicResource;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.plugins.ResourceProvider;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class RectFSpringAnim extends RemoteAnimationTargets.ReleaseCheck {
    private static final String TAG = "RectFSpringAnim";
    private boolean mAnimsStarted;
    private float mCurrentCenterX;
    private float mCurrentScaleProgress;
    private float mCurrentY;
    private float mFinishProgress;
    private float mMinVisChange;
    private SpringAnimation mRectScaleAnim;
    private boolean mRectScaleAnimEnded;
    private FlingSpringAnim mRectXAnim;
    private boolean mRectXAnimEnded;
    private FlingSpringAnim mRectYAnim;
    private boolean mRectYAnimEnded;
    private final RectF mStartRect;
    private final RectF mTargetRect;
    private boolean mTrackingBottomY;
    private float mYOvershoot;
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_CENTER_X = new FloatPropertyCompat<RectFSpringAnim>("rectCenterXSpring") { // from class: com.android.quickstep.util.RectFSpringAnim.1
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(RectFSpringAnim anim) {
            return anim.mCurrentCenterX;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(RectFSpringAnim anim, float currentCenterX) {
            anim.mCurrentCenterX = currentCenterX;
            anim.onUpdate();
        }
    };
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_Y = new FloatPropertyCompat<RectFSpringAnim>("rectYSpring") { // from class: com.android.quickstep.util.RectFSpringAnim.2
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(RectFSpringAnim anim) {
            return anim.mCurrentY;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(RectFSpringAnim anim, float y) {
            anim.mCurrentY = y;
            anim.onUpdate();
        }
    };
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_SCALE_PROGRESS = new FloatPropertyCompat<RectFSpringAnim>("rectScaleProgress") { // from class: com.android.quickstep.util.RectFSpringAnim.3
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(RectFSpringAnim object) {
            return object.mCurrentScaleProgress;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(RectFSpringAnim object, float value) {
            object.mCurrentScaleProgress = value;
            object.onUpdate();
            object.forceEnd();
        }
    };
    private final RectF mCurrentRect = new RectF();
    private final List<OnUpdateListener> mOnUpdateListeners = new ArrayList();
    private final List<Animator.AnimatorListener> mAnimatorListeners = new ArrayList();

    public interface OnUpdateListener {
        default void onCancel() {
        }

        void onUpdate(RectF currentRect, float progress);
    }

    public RectFSpringAnim(RectF startRect, RectF targetRect, Context context) {
        this.mStartRect = startRect;
        this.mTargetRect = targetRect;
        this.mCurrentCenterX = startRect.centerX();
        boolean z = startRect.bottom < targetRect.bottom;
        this.mTrackingBottomY = z;
        this.mCurrentY = z ? startRect.bottom : startRect.top;
        ResourceProvider resourceProviderProvider = DynamicResource.provider(context);
        this.mMinVisChange = resourceProviderProvider.getDimension(R.dimen.swipe_up_fling_min_visible_change);
        this.mYOvershoot = resourceProviderProvider.getDimension(R.dimen.swipe_up_y_overshoot);
        setCanRelease(true);
    }

    public void onTargetPositionChanged() {
        FlingSpringAnim flingSpringAnim = this.mRectXAnim;
        if (flingSpringAnim != null && flingSpringAnim.getTargetPosition() != this.mTargetRect.centerX()) {
            this.mRectXAnim.updatePosition(this.mCurrentCenterX, this.mTargetRect.centerX());
        }
        FlingSpringAnim flingSpringAnim2 = this.mRectYAnim;
        if (flingSpringAnim2 != null) {
            if (this.mTrackingBottomY && flingSpringAnim2.getTargetPosition() != this.mTargetRect.bottom) {
                this.mRectYAnim.updatePosition(this.mCurrentY, this.mTargetRect.bottom);
            } else {
                if (this.mTrackingBottomY || this.mRectYAnim.getTargetPosition() == this.mTargetRect.top) {
                    return;
                }
                this.mRectYAnim.updatePosition(this.mCurrentY, this.mTargetRect.top);
            }
        }
    }

    public void addOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListeners.add(onUpdateListener);
    }

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimatorListeners.add(animatorListener);
    }

    public void start(Context context, PointF velocityPxPerMs) {
        DynamicAnimation.OnAnimationEndListener onAnimationEndListener = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.quickstep.util.-$$Lambda$RectFSpringAnim$ZloMU1iAIpWQX5m_VnrbAvueEX8
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                this.f$0.lambda$start$0$RectFSpringAnim(dynamicAnimation, z, f, f2);
            }
        };
        DynamicAnimation.OnAnimationEndListener onAnimationEndListener2 = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.quickstep.util.-$$Lambda$RectFSpringAnim$iZDgaingn4bLwxYrdJl_MZtr8M8
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                this.f$0.lambda$start$1$RectFSpringAnim(dynamicAnimation, z, f, f2);
            }
        };
        float f = this.mCurrentCenterX;
        float fCenterX = this.mTargetRect.centerX();
        this.mRectXAnim = new FlingSpringAnim(this, context, RECT_CENTER_X, f, fCenterX, velocityPxPerMs.x * 1000.0f, this.mMinVisChange, Math.min(f, fCenterX), Math.max(f, fCenterX), 1.0f, onAnimationEndListener);
        float f2 = velocityPxPerMs.y * 1000.0f;
        float fAbs = ((Math.abs(f2) * 0.9f) / 20000.0f) + 0.1f;
        float f3 = this.mCurrentY;
        float f4 = this.mTrackingBottomY ? this.mTargetRect.bottom : this.mTargetRect.top;
        this.mRectYAnim = new FlingSpringAnim(this, context, RECT_Y, f3, f4, f2, this.mMinVisChange, Math.min(f3, f4 - this.mYOvershoot), Math.max(f3, f4), fAbs, onAnimationEndListener2);
        float fAbs2 = Math.abs(1.0f / this.mStartRect.height());
        ResourceProvider resourceProviderProvider = DynamicResource.provider(context);
        this.mRectScaleAnim = new SpringAnimation(this, RECT_SCALE_PROGRESS).setSpring(new SpringForce(1.0f).setDampingRatio(resourceProviderProvider.getFloat(R.dimen.swipe_up_rect_scale_damping_ratio)).setStiffness(resourceProviderProvider.getFloat(R.dimen.swipe_up_rect_scale_stiffness))).setStartVelocity(velocityPxPerMs.y * fAbs2).setMaxValue(1.0f).setMinimumVisibleChange(fAbs2).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.quickstep.util.-$$Lambda$RectFSpringAnim$1Xceo656XIXH5idpd_76LxxnVOg
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f5, float f6) {
                this.f$0.lambda$start$2$RectFSpringAnim(dynamicAnimation, z, f5, f6);
            }
        });
        setCanRelease(false);
        this.mAnimsStarted = true;
        this.mRectXAnim.start();
        this.mRectYAnim.start();
        this.mRectScaleAnim.start();
        Iterator<Animator.AnimatorListener> it = this.mAnimatorListeners.iterator();
        while (it.hasNext()) {
            it.next().onAnimationStart(null);
        }
    }

    public /* synthetic */ void lambda$start$0$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectXAnimEnded = true;
        maybeOnEnd();
    }

    public /* synthetic */ void lambda$start$1$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectYAnimEnded = true;
        maybeOnEnd();
    }

    public /* synthetic */ void lambda$start$2$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectScaleAnimEnded = true;
        maybeOnEnd();
    }

    public void end() {
        if (this.mAnimsStarted) {
            this.mRectXAnim.end();
            this.mRectYAnim.end();
            if (this.mRectScaleAnim.canSkipToEnd()) {
                this.mRectScaleAnim.skipToEnd();
            }
        }
        this.mRectXAnimEnded = true;
        this.mRectYAnimEnded = true;
        this.mRectScaleAnimEnded = true;
        maybeOnEnd();
    }

    private boolean isEnded() {
        return this.mRectXAnimEnded && this.mRectYAnimEnded && this.mRectScaleAnimEnded;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdate() {
        if (isEnded() || this.mOnUpdateListeners.isEmpty()) {
            return;
        }
        float fMapRange = Utilities.mapRange(this.mCurrentScaleProgress, this.mStartRect.width(), this.mTargetRect.width());
        float fMapRange2 = Utilities.mapRange(this.mCurrentScaleProgress, this.mStartRect.height(), this.mTargetRect.height());
        if (this.mTrackingBottomY) {
            RectF rectF = this.mCurrentRect;
            float f = this.mCurrentCenterX;
            float f2 = fMapRange / 2.0f;
            float f3 = this.mCurrentY;
            rectF.set(f - f2, f3 - fMapRange2, f + f2, f3);
        } else {
            RectF rectF2 = this.mCurrentRect;
            float f4 = this.mCurrentCenterX;
            float f5 = fMapRange / 2.0f;
            float f6 = this.mCurrentY;
            rectF2.set(f4 - f5, f6, f4 + f5, fMapRange2 + f6);
        }
        Iterator<OnUpdateListener> it = this.mOnUpdateListeners.iterator();
        while (it.hasNext()) {
            it.next().onUpdate(this.mCurrentRect, this.mCurrentScaleProgress);
        }
    }

    private void maybeOnEnd() {
        if (this.mAnimsStarted && isEnded()) {
            this.mAnimsStarted = false;
            setCanRelease(true);
            Iterator<Animator.AnimatorListener> it = this.mAnimatorListeners.iterator();
            while (it.hasNext()) {
                it.next().onAnimationEnd(null);
            }
        }
    }

    public void cancel() {
        if (this.mAnimsStarted) {
            Iterator<OnUpdateListener> it = this.mOnUpdateListeners.iterator();
            while (it.hasNext()) {
                it.next().onCancel();
            }
        }
        end();
    }

    public void setFinishProgress(float value) {
        this.mFinishProgress = value;
    }

    public void forceEnd() {
        if (!this.mAnimsStarted || Float.compare(this.mFinishProgress, 0.0f) == 0 || this.mAnimatorListeners.isEmpty() || Float.compare(this.mCurrentScaleProgress, this.mFinishProgress) < 0) {
            return;
        }
        LGLog.i(TAG, "forceEnd : " + this.mCurrentScaleProgress);
        this.mFinishProgress = 0.0f;
        Iterator<Animator.AnimatorListener> it = this.mAnimatorListeners.iterator();
        while (it.hasNext()) {
            it.next().onAnimationEnd(null);
        }
        end();
        this.mAnimsStarted = false;
    }

    public void forceEndWithAllCallback() {
        LGLog.i(TAG, "forceEndWithAllCallback : " + this.mCurrentScaleProgress);
        this.mFinishProgress = 0.0f;
        for (Animator.AnimatorListener animatorListener : this.mAnimatorListeners) {
            animatorListener.onAnimationStart(null);
            animatorListener.onAnimationEnd(null);
        }
        end();
        this.mAnimsStarted = false;
    }
}
