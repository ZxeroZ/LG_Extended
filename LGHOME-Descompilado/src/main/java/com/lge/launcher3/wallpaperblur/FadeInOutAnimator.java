package com.lge.launcher3.wallpaperblur;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class FadeInOutAnimator {
    public static final boolean DEBUG = false;
    public static final String TAG = "FadeInOutAnimator";
    private final int ALPHA_DELAYTIME;
    private final int SCALE_ANIMTIME;
    private final float SCALE_RATIO;
    private ValueAnimator mAnimator;
    private ValueAnimator mAnimator_scale;
    private final Interpolator mDecelerateInterpolator;
    private final Interpolator mExpoInOutInterpolator;
    private Interpolator mFadeInInterpolator;
    private Interpolator mFadeOutInterpolator;
    private FadeInOutAnimatorListener mListener;
    private float mPivotX;
    private float mPivotY;
    private View mTargetView;

    public interface FadeInOutAnimatorListener {
        void onAnimationEnd(Animator animation, FadeType type);

        void onAnimationStart(Animator animation, FadeType type);

        void onAnimationUpdate(ValueAnimator animation, FadeType type);
    }

    public enum FadeType {
        FADE_IN,
        FADE_OUT,
        SHOW_NOANIM,
        HIDE_NOANIM,
        FADEIN_SCALEUP,
        FADEOUT_SCALEDOWN
    }

    public FadeInOutAnimator() {
        this(null);
    }

    public FadeInOutAnimator(View targetView) {
        this.mTargetView = null;
        this.mAnimator = null;
        this.mAnimator_scale = null;
        this.mPivotX = 0.0f;
        this.mPivotY = 0.0f;
        this.SCALE_ANIMTIME = 250;
        this.ALPHA_DELAYTIME = 100;
        this.SCALE_RATIO = 1.2f;
        this.mFadeInInterpolator = null;
        this.mFadeOutInterpolator = null;
        this.mListener = null;
        this.mExpoInOutInterpolator = new PathInterpolator(0.7f, 0.0f, 0.0f, 1.0f);
        this.mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
        setTargetView(targetView);
    }

    public void setTargetView(View targetView) {
        this.mTargetView = targetView;
    }

    public void setFadeInInterpolator(Interpolator interpolator) {
        this.mFadeInInterpolator = interpolator;
    }

    public void setFadeOutInterpolator(Interpolator interpolator) {
        this.mFadeOutInterpolator = interpolator;
    }

    public void startFadeIn(int duration) {
        start(FadeType.FADE_IN, duration);
    }

    public void startFadeOut(int duration) {
        start(FadeType.FADE_OUT, duration);
    }

    public void setBGPivotX(float pivotx) {
        this.mPivotX = pivotx;
    }

    public void setBGPivotY(float pivoty) {
        this.mPivotY = pivoty;
    }

    public void start(final FadeType type, int duration) {
        clear();
        float alpha = this.mTargetView.getAlpha();
        int i = AnonymousClass4.$SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[type.ordinal()];
        if (i == 1) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(alpha, 1.0f);
            this.mAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setInterpolator(this.mFadeInInterpolator);
            this.mAnimator_scale = ValueAnimator.ofFloat(1.0f, 1.0f);
        } else if (i == 2) {
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(alpha, 0.0f);
            this.mAnimator = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.setInterpolator(this.mFadeOutInterpolator);
            this.mAnimator_scale = ValueAnimator.ofFloat(1.0f, 1.0f);
        } else if (i == 3) {
            ValueAnimator valueAnimatorOfFloat3 = ValueAnimator.ofFloat(alpha, 1.0f);
            this.mAnimator = valueAnimatorOfFloat3;
            valueAnimatorOfFloat3.setInterpolator(this.mDecelerateInterpolator);
            this.mAnimator_scale = ValueAnimator.ofFloat(1.0f, 1.2f);
            float f = this.mPivotX;
            if (f != 0.0f && this.mPivotY != 0.0f) {
                this.mTargetView.setPivotX(f);
                this.mTargetView.setPivotY(this.mPivotY);
            }
        } else {
            if (i != 4) {
                return;
            }
            ValueAnimator valueAnimatorOfFloat4 = ValueAnimator.ofFloat(alpha, 0.0f);
            this.mAnimator = valueAnimatorOfFloat4;
            valueAnimatorOfFloat4.setInterpolator(this.mExpoInOutInterpolator);
            this.mAnimator_scale = ValueAnimator.ofFloat(1.2f, 1.0f);
            float f2 = this.mPivotX;
            if (f2 != 0.0f && this.mPivotY != 0.0f) {
                this.mTargetView.setPivotX(f2);
                this.mTargetView.setPivotY(this.mPivotY);
            }
        }
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wallpaperblur.FadeInOutAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                LGLog.i(FadeInOutAnimator.TAG, String.format("%s onAnimationStart()", type));
                if (FadeInOutAnimator.this.mListener != null) {
                    FadeInOutAnimator.this.mListener.onAnimationStart(animation, type);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LGLog.i(FadeInOutAnimator.TAG, String.format("%s onAnimationEnd()", type));
                if (FadeInOutAnimator.this.mListener != null) {
                    FadeInOutAnimator.this.mListener.onAnimationEnd(animation, type);
                }
            }
        });
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.wallpaperblur.FadeInOutAnimator.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue == null || !(animatedValue instanceof Float)) {
                    return;
                }
                FadeInOutAnimator.this.mTargetView.setAlpha(((Float) animatedValue).floatValue());
                if (FadeInOutAnimator.this.mListener != null) {
                    FadeInOutAnimator.this.mListener.onAnimationUpdate(animation, type);
                }
            }
        });
        this.mAnimator_scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.wallpaperblur.FadeInOutAnimator.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue == null || !(animatedValue instanceof Float)) {
                    return;
                }
                Float f3 = (Float) animatedValue;
                FadeInOutAnimator.this.mTargetView.setScaleX(f3.floatValue());
                FadeInOutAnimator.this.mTargetView.setScaleY(f3.floatValue());
            }
        });
        if (type == FadeType.FADEOUT_SCALEDOWN) {
            this.mAnimator.setDuration(duration + 100);
        } else {
            this.mAnimator.setDuration(duration);
        }
        this.mAnimator.start();
        this.mAnimator_scale.setDuration(250L);
        this.mAnimator_scale.start();
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wallpaperblur.FadeInOutAnimator$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType;

        static {
            int[] iArr = new int[FadeType.values().length];
            $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType = iArr;
            try {
                iArr[FadeType.FADE_IN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeType.FADE_OUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeType.FADEIN_SCALEUP.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeType.FADEOUT_SCALEDOWN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public boolean isStarted() {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            return false;
        }
        return valueAnimator.isStarted();
    }

    public boolean isRunning() {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            return false;
        }
        return valueAnimator.isStarted();
    }

    public void clear() {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
            this.mAnimator.removeAllListeners();
            if (this.mAnimator.isRunning()) {
                this.mAnimator.cancel();
            }
            this.mAnimator = null;
        }
        ValueAnimator valueAnimator2 = this.mAnimator_scale;
        if (valueAnimator2 != null) {
            valueAnimator2.removeAllUpdateListeners();
            this.mAnimator_scale.removeAllListeners();
            if (this.mAnimator_scale.isRunning()) {
                this.mAnimator_scale.cancel();
            }
            this.mAnimator_scale = null;
        }
    }

    public void addListener(FadeInOutAnimatorListener listener) {
        this.mListener = listener;
    }

    public void destroy() {
        this.mListener = null;
        clear();
        this.mTargetView = null;
    }
}
