package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.DisplayController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorPlaybackController implements ValueAnimator.AnimatorUpdateListener {
    private static final float ANIMATION_COMPLETE_THRESHOLD = 0.95f;
    private static boolean DEBUG = false;
    private static final String TAG = "AnimatorPlaybackCtrler";
    protected final AnimatorSet mAnim;
    private final ValueAnimator mAnimationPlayer;
    private final Holder[] mChildAnimations;
    protected float mCurrentFraction;
    private final long mDuration;
    private Runnable mEndAction;
    private OnAnimationEndDispatcher mEndListener;
    protected Runnable mOnCancelRunnable;
    private DynamicAnimation.OnAnimationEndListener mSpringEndListener;
    private Set<SpringAnimation> mSprings;
    protected boolean mTargetCancelled = false;
    private boolean mSkipToEnd = false;

    /* JADX INFO: Access modifiers changed from: private */
    interface ProgressMapper {
        public static final ProgressMapper DEFAULT = new ProgressMapper() { // from class: com.android.launcher3.anim.-$$Lambda$AnimatorPlaybackController$ProgressMapper$HV5dcaCySg2ZC6m3BVIyMUNjbfs
            @Override // com.android.launcher3.anim.AnimatorPlaybackController.ProgressMapper
            public final float getProgress(float f, float f2) {
                return AnimatorPlaybackController.ProgressMapper.lambda$static$0(f, f2);
            }
        };

        static /* synthetic */ float lambda$static$0(float f, float f2) {
            if (f > f2) {
                return 1.0f;
            }
            return f / f2;
        }

        float getProgress(float progress, float globalProgress);
    }

    public static AnimatorPlaybackController wrap(AnimatorSet anim, long duration) {
        ArrayList arrayList = new ArrayList();
        addAnimationHoldersRecur(anim, duration, SpringProperty.DEFAULT, arrayList);
        return new AnimatorPlaybackController(anim, duration, arrayList);
    }

    AnimatorPlaybackController(AnimatorSet anim, long duration, ArrayList<Holder> childAnims) {
        this.mAnim = anim;
        this.mDuration = duration;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mAnimationPlayer = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
        OnAnimationEndDispatcher onAnimationEndDispatcher = new OnAnimationEndDispatcher();
        this.mEndListener = onAnimationEndDispatcher;
        valueAnimatorOfFloat.addListener(onAnimationEndDispatcher);
        valueAnimatorOfFloat.addUpdateListener(this);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.anim.AnimatorPlaybackController.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = true;
                if (AnimatorPlaybackController.this.mOnCancelRunnable != null) {
                    AnimatorPlaybackController.this.mOnCancelRunnable.run();
                    AnimatorPlaybackController.this.mOnCancelRunnable = null;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
                AnimatorPlaybackController.this.mOnCancelRunnable = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
            }
        });
        this.mSprings = new HashSet();
        this.mSpringEndListener = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.launcher3.anim.-$$Lambda$AnimatorPlaybackController$xtcD-5NTwC_Ljnx9W8GqqW5bff8
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                this.f$0.lambda$new$0$AnimatorPlaybackController(dynamicAnimation, z, f, f2);
            }
        };
        this.mChildAnimations = (Holder[]) childAnims.toArray(new Holder[childAnims.size()]);
    }

    public /* synthetic */ void lambda$new$0$AnimatorPlaybackController(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (z) {
            this.mEndListener.onAnimationCancel(this.mAnimationPlayer);
        } else {
            this.mEndListener.onAnimationEnd(this.mAnimationPlayer);
        }
    }

    public AnimatorSet getTarget() {
        return this.mAnim;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public TimeInterpolator getInterpolator() {
        return this.mAnim.getInterpolator() != null ? this.mAnim.getInterpolator() : Interpolators.LINEAR;
    }

    public void start() {
        this.mAnimationPlayer.setFloatValues(this.mCurrentFraction, 1.0f);
        this.mAnimationPlayer.setDuration(clampDuration(1.0f - this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void reverse() {
        this.mAnimationPlayer.setFloatValues(this.mCurrentFraction, 0.0f);
        this.mAnimationPlayer.setDuration(clampDuration(this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void startWithVelocity(Context context, boolean goingToEnd, float velocity, float scale, long animationDuration) {
        int i;
        float fAbs = 1.0f / Math.abs(scale);
        float f = velocity * fAbs;
        float fBoundToRange = Utilities.boundToRange(getProgressFraction() + (DisplayController.getSingleFrameMs(context) * f), 0.0f, 1.0f);
        int i2 = goingToEnd ? 1 : 2;
        Holder[] holderArr = this.mChildAnimations;
        int length = holderArr.length;
        long jMax = animationDuration;
        int i3 = 0;
        while (i3 < length) {
            Holder holder = holderArr[i3];
            if ((holder.springProperty.flags & i2) != 0) {
                final SpringAnimationBuilder springAnimationBuilderComputeParams = new SpringAnimationBuilder(context).setStartValue(this.mCurrentFraction).setEndValue(goingToEnd ? 1.0f : 0.0f).setStartVelocity(f).setMinimumVisibleChange(fAbs).setDampingRatio(holder.springProperty.mDampingRatio).setStiffness(holder.springProperty.mStiffness).computeParams();
                i = i2;
                long duration = springAnimationBuilderComputeParams.getDuration();
                jMax = Math.max(duration, jMax);
                final float f2 = duration;
                holder.mapper = new ProgressMapper() { // from class: com.android.launcher3.anim.-$$Lambda$AnimatorPlaybackController$YoUg1yMNl_VLgaWufG8NnqCaQ20
                    @Override // com.android.launcher3.anim.AnimatorPlaybackController.ProgressMapper
                    public final float getProgress(float f3, float f4) {
                        return this.f$0.lambda$startWithVelocity$1$AnimatorPlaybackController(f2, f3, f4);
                    }
                };
                ValueAnimator valueAnimator = holder.anim;
                Objects.requireNonNull(springAnimationBuilderComputeParams);
                valueAnimator.setInterpolator(new TimeInterpolator() { // from class: com.android.launcher3.anim.-$$Lambda$EzbXlc2nigVSBFZVxlgxmmE290k
                    @Override // android.animation.TimeInterpolator
                    public final float getInterpolation(float f3) {
                        return springAnimationBuilderComputeParams.getInterpolatedValue(f3);
                    }
                });
            } else {
                i = i2;
            }
            i3++;
            i2 = i;
        }
        ValueAnimator valueAnimator2 = this.mAnimationPlayer;
        float[] fArr = new float[2];
        fArr[0] = fBoundToRange;
        fArr[1] = goingToEnd ? 1.0f : 0.0f;
        valueAnimator2.setFloatValues(fArr);
        if (jMax <= animationDuration) {
            this.mAnimationPlayer.setDuration(animationDuration);
            this.mAnimationPlayer.setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity));
        } else {
            this.mAnimationPlayer.setDuration(jMax);
            this.mAnimationPlayer.setInterpolator(Interpolators.clampToProgress(Interpolators.scrollInterpolatorForVelocity(velocity), 0.0f, animationDuration / jMax));
        }
        this.mAnimationPlayer.start();
    }

    public /* synthetic */ float lambda$startWithVelocity$1$AnimatorPlaybackController(float f, float f2, float f3) {
        return this.mAnimationPlayer.getCurrentPlayTime() / f;
    }

    public void forceFinishIfCloseToEnd() {
        if (!this.mAnimationPlayer.isRunning() || this.mAnimationPlayer.getAnimatedFraction() <= ANIMATION_COMPLETE_THRESHOLD) {
            return;
        }
        this.mAnimationPlayer.end();
    }

    public void pause() {
        for (Holder holder : this.mChildAnimations) {
            holder.reset();
        }
        this.mAnimationPlayer.cancel();
    }

    public ValueAnimator getAnimationPlayer() {
        return this.mAnimationPlayer;
    }

    public void setPlayFraction(float fraction) {
        this.mCurrentFraction = fraction;
        if (this.mTargetCancelled) {
            return;
        }
        float fBoundToRange = Utilities.boundToRange(fraction, 0.0f, 1.0f);
        for (Holder holder : this.mChildAnimations) {
            holder.setProgress(fBoundToRange);
        }
    }

    public float getProgressFraction() {
        return this.mCurrentFraction;
    }

    public float getInterpolatedProgress() {
        return getInterpolator().getInterpolation(this.mCurrentFraction);
    }

    public void setEndAction(Runnable runnable) {
        this.mEndAction = runnable;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    protected long clampDuration(float fraction) {
        long j = this.mDuration;
        float f = j * fraction;
        if (f <= 0.0f) {
            return 0L;
        }
        return Math.min((long) f, j);
    }

    public void dispatchOnStartWithVelocity(float end, float velocity) {
        if (!FeatureFlags.QUICKSTEP_SPRINGS.get()) {
            dispatchOnStart();
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "dispatchOnStartWithVelocity#end=" + end + ", velocity=" + velocity);
        }
        for (Animator animator : this.mAnim.getChildAnimations()) {
            if (animator instanceof SpringObjectAnimator) {
                if (DEBUG) {
                    Log.d(TAG, "Found springAnimator=" + animator);
                }
                SpringObjectAnimator springObjectAnimator = (SpringObjectAnimator) animator;
                this.mSprings.add(springObjectAnimator.getSpring());
                springObjectAnimator.startSpring(end, velocity, this.mSpringEndListener);
            }
        }
        dispatchOnStart();
    }

    public void dispatchOnCancelWithoutCancelRunnable() {
        dispatchOnCancelWithoutCancelRunnable(null);
    }

    public void dispatchOnCancelWithoutCancelRunnable(Runnable callback) {
        Runnable runnable = this.mOnCancelRunnable;
        setOnCancelRunnable(null);
        dispatchOnCancel();
        if (callback != null) {
            callback.run();
        }
        setOnCancelRunnable(runnable);
    }

    public void dispatchOnStart() {
        dispatchOnStartRecursively(this.mAnim);
    }

    private void dispatchOnStartRecursively(Animator animator) {
        List listNonNullList;
        if (animator instanceof SpringObjectAnimator) {
            listNonNullList = nonNullList(((SpringObjectAnimator) animator).getObjectAnimatorListeners());
        } else {
            listNonNullList = nonNullList(animator.getListeners());
        }
        Iterator it = listNonNullList.iterator();
        while (it.hasNext()) {
            ((Animator.AnimatorListener) it.next()).onAnimationStart(animator);
        }
        if (animator instanceof AnimatorSet) {
            Iterator it2 = nonNullList(((AnimatorSet) animator).getChildAnimations()).iterator();
            while (it2.hasNext()) {
                dispatchOnStartRecursively((Animator) it2.next());
            }
        }
    }

    public void dispatchOnCancel() {
        dispatchOnCancelRecursively(this.mAnim);
    }

    private void dispatchOnCancelRecursively(Animator animator) {
        Iterator it = nonNullList(animator.getListeners()).iterator();
        while (it.hasNext()) {
            ((Animator.AnimatorListener) it.next()).onAnimationCancel(animator);
        }
        if (animator instanceof AnimatorSet) {
            Iterator it2 = nonNullList(((AnimatorSet) animator).getChildAnimations()).iterator();
            while (it2.hasNext()) {
                dispatchOnCancelRecursively((Animator) it2.next());
            }
        }
    }

    public void dispatchSetInterpolator(TimeInterpolator interpolator) {
        dispatchSetInterpolatorRecursively(this.mAnim, interpolator);
    }

    private void dispatchSetInterpolatorRecursively(Animator anim, TimeInterpolator interpolator) {
        anim.setInterpolator(interpolator);
        if (anim instanceof AnimatorSet) {
            Iterator it = nonNullList(((AnimatorSet) anim).getChildAnimations()).iterator();
            while (it.hasNext()) {
                dispatchSetInterpolatorRecursively((Animator) it.next(), interpolator);
            }
        }
    }

    public AnimatorPlaybackController setOnCancelRunnable(Runnable runnable) {
        this.mOnCancelRunnable = runnable;
        return this;
    }

    public Runnable getOnCancelRunnable() {
        return this.mOnCancelRunnable;
    }

    public void skipToEnd() {
        this.mSkipToEnd = true;
        for (SpringAnimation springAnimation : this.mSprings) {
            if (springAnimation.canSkipToEnd()) {
                springAnimation.skipToEnd();
            }
        }
        this.mAnimationPlayer.end();
        this.mSkipToEnd = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAnySpringRunning() {
        Iterator<SpringAnimation> it = this.mSprings.iterator();
        while (it.hasNext()) {
            if (it.next().isRunning()) {
                return true;
            }
        }
        return false;
    }

    private class OnAnimationEndDispatcher extends AnimationSuccessListener {
        boolean mAnimatorDone;
        boolean mDispatched;
        boolean mSpringsDone;

        private OnAnimationEndDispatcher() {
            this.mAnimatorDone = false;
            this.mSpringsDone = false;
            this.mDispatched = false;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            this.mCancelled = false;
            this.mDispatched = false;
        }

        @Override // com.android.launcher3.anim.AnimationSuccessListener
        public void onAnimationSuccess(Animator animator) {
            if (AnimatorPlaybackController.this.mSprings.isEmpty()) {
                this.mAnimatorDone = true;
                this.mSpringsDone = true;
            }
            if (AnimatorPlaybackController.this.isAnySpringRunning()) {
                this.mAnimatorDone = true;
            } else {
                this.mSpringsDone = true;
            }
            if (this.mDispatched) {
                return;
            }
            if (AnimatorPlaybackController.this.mSkipToEnd || (this.mAnimatorDone && this.mSpringsDone)) {
                dispatchOnEndRecursively(AnimatorPlaybackController.this.mAnim);
                if (AnimatorPlaybackController.this.mEndAction != null) {
                    AnimatorPlaybackController.this.mEndAction.run();
                }
                this.mDispatched = true;
            }
        }

        private void dispatchOnEndRecursively(Animator animator) {
            Iterator it = AnimatorPlaybackController.nonNullList(animator.getListeners()).iterator();
            while (it.hasNext()) {
                ((Animator.AnimatorListener) it.next()).onAnimationEnd(animator);
            }
            if (animator instanceof AnimatorSet) {
                Iterator it2 = AnimatorPlaybackController.nonNullList(((AnimatorSet) animator).getChildAnimations()).iterator();
                while (it2.hasNext()) {
                    dispatchOnEndRecursively((Animator) it2.next());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> List<T> nonNullList(ArrayList<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    static class Holder {
        public final ValueAnimator anim;
        public final float globalEndProgress;
        public final TimeInterpolator interpolator;
        public ProgressMapper mapper = ProgressMapper.DEFAULT;
        public final SpringProperty springProperty;

        Holder(Animator anim, float globalDuration, SpringProperty springProperty) {
            ValueAnimator valueAnimator = (ValueAnimator) anim;
            this.anim = valueAnimator;
            this.springProperty = springProperty;
            this.interpolator = valueAnimator.getInterpolator();
            this.globalEndProgress = anim.getDuration() / globalDuration;
        }

        public void setProgress(float progress) {
            this.anim.setCurrentFraction(this.mapper.getProgress(progress, this.globalEndProgress));
        }

        public void reset() {
            this.anim.setInterpolator(this.interpolator);
            this.mapper = ProgressMapper.DEFAULT;
        }
    }

    static void addAnimationHoldersRecur(Animator anim, long globalDuration, SpringProperty springProperty, ArrayList<Holder> out) {
        long duration = anim.getDuration();
        TimeInterpolator interpolator = anim.getInterpolator();
        if (anim instanceof ValueAnimator) {
            out.add(new Holder(anim, globalDuration, springProperty));
            return;
        }
        if (anim instanceof AnimatorSet) {
            for (Animator animator : ((AnimatorSet) anim).getChildAnimations()) {
                if (duration > 0) {
                    animator.setDuration(duration);
                }
                if (interpolator != null) {
                    animator.setInterpolator(interpolator);
                }
                addAnimationHoldersRecur(animator, globalDuration, springProperty, out);
            }
            return;
        }
        throw new RuntimeException("Unknown animation type " + anim);
    }
}
