package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.util.FloatProperty;
import android.util.Log;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.config.FeatureFlags;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SpringObjectAnimator<T> extends ValueAnimator {
    private static boolean DEBUG = false;
    private static final String TAG = "SpringObjectAnimator";
    private ArrayList<Animator.AnimatorListener> mListeners;
    private ObjectAnimator mObjectAnimator;
    private SpringProperty<T> mProperty;
    private SpringAnimation mSpring;
    private float[] mValues;
    private boolean mSpringEnded = true;
    private boolean mAnimatorEnded = true;
    private boolean mEnded = true;

    public SpringObjectAnimator(T object, FloatProperty<T> property, float minimumVisibleChange, float damping, float stiffness, float... values) {
        SpringAnimation springAnimation = new SpringAnimation(object, (FloatPropertyCompat<T>) FloatPropertyCompat.createFloatPropertyCompat(property));
        this.mSpring = springAnimation;
        springAnimation.setMinimumVisibleChange(minimumVisibleChange);
        this.mSpring.setSpring(new SpringForce(0.0f).setDampingRatio(damping).setStiffness(stiffness));
        this.mSpring.setStartVelocity(0.01f);
        SpringProperty<T> springProperty = new SpringProperty<>(property, this.mSpring);
        this.mProperty = springProperty;
        this.mObjectAnimator = ObjectAnimator.ofFloat(object, springProperty, values);
        this.mValues = values;
        this.mListeners = new ArrayList<>();
        setFloatValues(values);
        this.mObjectAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.anim.SpringObjectAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                SpringObjectAnimator.this.mAnimatorEnded = false;
                SpringObjectAnimator.this.mEnded = false;
                Iterator it = SpringObjectAnimator.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((Animator.AnimatorListener) it.next()).onAnimationStart(animation);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SpringObjectAnimator.this.mAnimatorEnded = true;
                SpringObjectAnimator.this.tryEnding();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                Iterator it = SpringObjectAnimator.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((Animator.AnimatorListener) it.next()).onAnimationCancel(animation);
                }
                SpringObjectAnimator.this.mSpring.cancel();
            }
        });
        this.mSpring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.android.launcher3.anim.-$$Lambda$SpringObjectAnimator$LRJiYBfwGpjpLpn4Jm6sbHBpqE0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                this.f$0.lambda$new$0$SpringObjectAnimator(dynamicAnimation, f, f2);
            }
        });
        this.mSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.launcher3.anim.-$$Lambda$SpringObjectAnimator$vfy-RfLf2GEJot6e4bf-xlQw27g
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                this.f$0.lambda$new$1$SpringObjectAnimator(dynamicAnimation, z, f, f2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$SpringObjectAnimator(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.mSpringEnded = false;
    }

    public /* synthetic */ void lambda$new$1$SpringObjectAnimator(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mSpringEnded = true;
        tryEnding();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryEnding() {
        if (DEBUG) {
            Log.d(TAG, "tryEnding#mAnimatorEnded=" + this.mAnimatorEnded + ", mSpringEnded=" + this.mSpringEnded + ", mEnded=" + this.mEnded);
        }
        if (this.mAnimatorEnded) {
            if ((this.mSpringEnded || !FeatureFlags.QUICKSTEP_SPRINGS.get()) && !this.mEnded) {
                Iterator<Animator.AnimatorListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onAnimationEnd(this);
                }
                this.mEnded = true;
            }
        }
    }

    public SpringAnimation getSpring() {
        return this.mSpring;
    }

    public void startSpring(float end, float velocity, DynamicAnimation.OnAnimationEndListener endListener) {
        this.mSpring.removeEndListener(endListener);
        this.mSpring.cancel();
        this.mSpring.addEndListener(endListener);
        this.mProperty.switchToSpring();
        this.mSpring.setStartVelocity(velocity);
        float[] fArr = this.mValues;
        float f = end == 0.0f ? fArr[1] : fArr[0];
        final float f2 = end == 0.0f ? this.mValues[0] : this.mValues[1];
        this.mSpring.setStartValue(f);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.android.launcher3.anim.-$$Lambda$SpringObjectAnimator$hyjwPG8LEeGxr9UM9WaSCZMT-Ko
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startSpring$2$SpringObjectAnimator(f2);
            }
        }, getStartDelay());
    }

    public /* synthetic */ void lambda$startSpring$2$SpringObjectAnimator(float f) {
        this.mSpring.animateToFinalPosition(f);
    }

    @Override // android.animation.Animator
    public void addListener(Animator.AnimatorListener listener) {
        this.mListeners.add(listener);
    }

    public ArrayList<Animator.AnimatorListener> getObjectAnimatorListeners() {
        return this.mObjectAnimator.getListeners();
    }

    @Override // android.animation.Animator
    public ArrayList<Animator.AnimatorListener> getListeners() {
        return this.mListeners;
    }

    @Override // android.animation.Animator
    public void removeAllListeners() {
        this.mListeners.clear();
    }

    @Override // android.animation.Animator
    public void removeListener(Animator.AnimatorListener listener) {
        this.mListeners.remove(listener);
    }

    @Override // android.animation.Animator
    public void addPauseListener(Animator.AnimatorPauseListener listener) {
        this.mObjectAnimator.addPauseListener(listener);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void cancel() {
        this.mObjectAnimator.cancel();
        this.mSpring.cancel();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void end() {
        this.mObjectAnimator.end();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public long getDuration() {
        return this.mObjectAnimator.getDuration();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public TimeInterpolator getInterpolator() {
        return this.mObjectAnimator.getInterpolator();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public long getStartDelay() {
        return this.mObjectAnimator.getStartDelay();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public long getTotalDuration() {
        return this.mObjectAnimator.getTotalDuration();
    }

    @Override // android.animation.Animator
    public boolean isPaused() {
        return this.mObjectAnimator.isPaused();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public boolean isRunning() {
        return this.mObjectAnimator.isRunning();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public boolean isStarted() {
        return this.mObjectAnimator.isStarted();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void pause() {
        this.mObjectAnimator.pause();
    }

    @Override // android.animation.Animator
    public void removePauseListener(Animator.AnimatorPauseListener listener) {
        this.mObjectAnimator.removePauseListener(listener);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void resume() {
        this.mObjectAnimator.resume();
    }

    /* JADX DEBUG: Method merged with bridge method: setDuration(J)Landroid/animation/Animator; */
    @Override // android.animation.ValueAnimator, android.animation.Animator
    public ValueAnimator setDuration(long duration) {
        return this.mObjectAnimator.setDuration(duration);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void setInterpolator(TimeInterpolator value) {
        this.mObjectAnimator.setInterpolator(value);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void setStartDelay(long startDelay) {
        this.mObjectAnimator.setStartDelay(startDelay);
    }

    @Override // android.animation.Animator
    public void setTarget(Object target) {
        this.mObjectAnimator.setTarget(target);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        this.mObjectAnimator.start();
    }

    @Override // android.animation.ValueAnimator
    public void setCurrentFraction(float fraction) {
        this.mObjectAnimator.setCurrentFraction(fraction);
    }

    @Override // android.animation.ValueAnimator
    public void setCurrentPlayTime(long playTime) {
        this.mObjectAnimator.setCurrentPlayTime(playTime);
    }

    public static class SpringProperty<T> extends FloatProperty<T> {
        final FloatProperty<T> mProperty;
        final SpringAnimation mSpring;
        boolean useSpring;

        public SpringProperty(FloatProperty<T> property, SpringAnimation spring) {
            super(property.getName());
            this.useSpring = false;
            this.mProperty = property;
            this.mSpring = spring;
        }

        public void switchToSpring() {
            this.useSpring = true;
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Property
        public Float get(T object) {
            return (Float) this.mProperty.get(object);
        }

        @Override // android.util.FloatProperty
        public void setValue(T object, float progress) {
            if (this.useSpring) {
                this.mSpring.animateToFinalPosition(progress);
            } else {
                this.mProperty.setValue(object, progress);
            }
        }
    }
}
