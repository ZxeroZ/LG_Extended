package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;

/* JADX INFO: loaded from: classes.dex */
public class AnimatedFloat {
    private Float mEndValue;
    private final Runnable mUpdateCallback;
    private ObjectAnimator mValueAnimator;
    public float value;
    public static final FloatProperty<AnimatedFloat> VALUE = new FloatProperty<AnimatedFloat>("value") { // from class: com.android.quickstep.AnimatedFloat.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(AnimatedFloat obj, float v) {
            obj.updateValue(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(AnimatedFloat obj) {
            return Float.valueOf(obj.value);
        }
    };
    private static final Runnable NO_OP = new Runnable() { // from class: com.android.quickstep.-$$Lambda$AnimatedFloat$PU374XMcAp47MKOhTiMQ9MU5xUo
        @Override // java.lang.Runnable
        public final void run() {
            AnimatedFloat.lambda$static$0();
        }
    };

    static /* synthetic */ void lambda$static$0() {
    }

    public AnimatedFloat() {
        this(NO_OP);
    }

    public AnimatedFloat(Runnable updateCallback) {
        this.mUpdateCallback = updateCallback;
    }

    public ObjectAnimator animateToValue(float end) {
        return animateToValue(this.value, end);
    }

    public ObjectAnimator animateToValue(float start, final float end) {
        cancelAnimation();
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, VALUE, start, end);
        this.mValueAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.AnimatedFloat.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                if (AnimatedFloat.this.mValueAnimator == animator) {
                    AnimatedFloat.this.mEndValue = Float.valueOf(end);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (AnimatedFloat.this.mValueAnimator == animator) {
                    AnimatedFloat.this.mValueAnimator = null;
                    AnimatedFloat.this.mEndValue = null;
                }
            }
        });
        return this.mValueAnimator;
    }

    public void updateValue(float v) {
        if (Float.compare(v, this.value) != 0) {
            this.value = v;
            this.mUpdateCallback.run();
        }
    }

    public void startAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null) {
            objectAnimator.start();
        }
    }

    public void cancelAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    public void finishAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator == null || !objectAnimator.isRunning()) {
            return;
        }
        this.mValueAnimator.end();
    }

    public ObjectAnimator getCurrentAnimation() {
        return this.mValueAnimator;
    }

    public boolean isAnimating() {
        return this.mValueAnimator != null;
    }

    public boolean isAnimatingToValue(float endValue) {
        Float f;
        return isAnimating() && (f = this.mEndValue) != null && f.floatValue() == endValue;
    }
}
