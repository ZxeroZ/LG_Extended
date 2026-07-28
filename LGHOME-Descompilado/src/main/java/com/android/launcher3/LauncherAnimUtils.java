package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.android.launcher3.util.UiThreadCircularReveal;
import com.android.quickstep.views.RecentsView;
import java.util.HashSet;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAnimUtils {
    public static final int ALL_APPS_TRANSITION_MS = 320;
    public static final float MIN_PROGRESS_TO_ALL_APPS = 0.4f;
    public static final int OVERVIEW_TRANSITION_MS = 250;
    public static final int SPRING_LOADED_EXIT_DELAY = 500;
    public static final int SPRING_LOADED_TRANSITION_MS = 150;
    public static final float SUCCESS_TRANSITION_PROGRESS = 0.5f;
    public static final FloatProperty<View> VIEW_ALPHA;
    public static final FloatProperty<View> VIEW_TRANSLATE_X;
    public static final FloatProperty<View> VIEW_TRANSLATE_Y;
    public static final IntProperty<Drawable> DRAWABLE_ALPHA = new IntProperty<Drawable>("drawableAlpha") { // from class: com.android.launcher3.LauncherAnimUtils.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Integer get(Drawable drawable) {
            return Integer.valueOf(drawable.getAlpha());
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;I)V */
        @Override // android.util.IntProperty
        public void setValue(Drawable drawable, int alpha) {
            drawable.setAlpha(alpha);
        }
    };
    static WeakHashMap<Animator, Object> sAnimators = new WeakHashMap<>();
    static Animator.AnimatorListener sEndAnimListener = new Animator.AnimatorListener() { // from class: com.android.launcher3.LauncherAnimUtils.2
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            LauncherAnimUtils.sAnimators.put(animation, null);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            LauncherAnimUtils.sAnimators.remove(animation);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            LauncherAnimUtils.sAnimators.remove(animation);
        }
    };
    public static final FloatProperty<View> SCALE_PROPERTY = new FloatProperty<View>("scale") { // from class: com.android.launcher3.LauncherAnimUtils.4
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(View view, float scale) {
            try {
                if (!(view instanceof RecentsView) || scale >= 0.0f) {
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    };
    public static final Property<ViewGroup.LayoutParams, Integer> LAYOUT_WIDTH = new Property<ViewGroup.LayoutParams, Integer>(Integer.TYPE, "width") { // from class: com.android.launcher3.LauncherAnimUtils.5
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Integer get(ViewGroup.LayoutParams lp) {
            return Integer.valueOf(lp.width);
        }

        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(ViewGroup.LayoutParams lp, Integer width) {
            lp.width = width.intValue();
        }
    };
    public static final Property<ViewGroup.LayoutParams, Integer> LAYOUT_HEIGHT = new Property<ViewGroup.LayoutParams, Integer>(Integer.TYPE, "height") { // from class: com.android.launcher3.LauncherAnimUtils.6
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Integer get(ViewGroup.LayoutParams lp) {
            return Integer.valueOf(lp.height);
        }

        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(ViewGroup.LayoutParams lp, Integer height) {
            lp.height = height.intValue();
        }
    };

    static {
        VIEW_TRANSLATE_X = View.TRANSLATION_X instanceof FloatProperty ? (FloatProperty) View.TRANSLATION_X : new FloatProperty<View>("translateX") { // from class: com.android.launcher3.LauncherAnimUtils.7
            /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
            @Override // android.util.FloatProperty
            public void setValue(View view, float v) {
                view.setTranslationX(v);
            }

            /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
            @Override // android.util.Property
            public Float get(View view) {
                return Float.valueOf(view.getTranslationX());
            }
        };
        VIEW_TRANSLATE_Y = View.TRANSLATION_Y instanceof FloatProperty ? (FloatProperty) View.TRANSLATION_Y : new FloatProperty<View>("translateY") { // from class: com.android.launcher3.LauncherAnimUtils.8
            /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
            @Override // android.util.FloatProperty
            public void setValue(View view, float v) {
                view.setTranslationY(v);
            }

            /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
            @Override // android.util.Property
            public Float get(View view) {
                return Float.valueOf(view.getTranslationY());
            }
        };
        VIEW_ALPHA = View.ALPHA instanceof FloatProperty ? (FloatProperty) View.ALPHA : new FloatProperty<View>("alpha") { // from class: com.android.launcher3.LauncherAnimUtils.9
            /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
            @Override // android.util.FloatProperty
            public void setValue(View view, float v) {
                view.setAlpha(v);
            }

            /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
            @Override // android.util.Property
            public Float get(View view) {
                return Float.valueOf(view.getAlpha());
            }
        };
    }

    public static void cancelOnDestroyActivity(Animator a) {
        a.addListener(sEndAnimListener);
    }

    public static void startAnimationAfterNextDraw(final Animator animator, final View view) {
        view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() { // from class: com.android.launcher3.LauncherAnimUtils.3
            private boolean mStarted = false;

            @Override // android.view.ViewTreeObserver.OnDrawListener
            public void onDraw() {
                if (this.mStarted) {
                    return;
                }
                this.mStarted = true;
                if (animator.getDuration() == 0) {
                    return;
                }
                animator.start();
                view.post(new Runnable() { // from class: com.android.launcher3.LauncherAnimUtils.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        view.getViewTreeObserver().removeOnDrawListener(this);
                    }
                });
            }
        });
    }

    public static void onDestroyActivity() {
        for (Animator animator : new HashSet(sAnimators.keySet())) {
            if (animator.isRunning()) {
                animator.cancel();
            }
            sAnimators.remove(animator);
        }
    }

    public static AnimatorSet createAnimatorSet() {
        AnimatorSet animatorSet = new AnimatorSet();
        cancelOnDestroyActivity(animatorSet);
        return animatorSet;
    }

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(values);
        cancelOnDestroyActivity(valueAnimator);
        return valueAnimator;
    }

    public static ValueAnimator ofFloat(View target, float... values) {
        return ofFloat(values);
    }

    public static ObjectAnimator ofFloat(View target, String propertyName, float... values) {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setTarget(target);
        objectAnimator.setPropertyName(propertyName);
        objectAnimator.setFloatValues(values);
        cancelOnDestroyActivity(objectAnimator);
        new FirstFrameAnimatorHelper(objectAnimator, target);
        return objectAnimator;
    }

    public static ObjectAnimator ofPropertyValuesHolder(View target, PropertyValuesHolder... values) {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setTarget(target);
        objectAnimator.setValues(values);
        cancelOnDestroyActivity(objectAnimator);
        new FirstFrameAnimatorHelper(objectAnimator, target);
        return objectAnimator;
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target, View view, PropertyValuesHolder... values) {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setTarget(target);
        objectAnimator.setValues(values);
        cancelOnDestroyActivity(objectAnimator);
        new FirstFrameAnimatorHelper(objectAnimator, view);
        return objectAnimator;
    }

    public static ValueAnimator createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius) {
        ValueAnimator valueAnimatorCreateCircularReveal = UiThreadCircularReveal.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        new FirstFrameAnimatorHelper(valueAnimatorCreateCircularReveal, view);
        return valueAnimatorCreateCircularReveal;
    }

    public static int blockedFlingDurationFactor(float velocity) {
        return (int) Utilities.boundToRange(Math.abs(velocity) / 2.0f, 2.0f, 6.0f);
    }
}
