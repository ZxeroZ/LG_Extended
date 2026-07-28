package com.lge.launcher3.wallpaperblur;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;

/* JADX INFO: loaded from: classes.dex */
public class BlurInOutAnimator {
    private static final float BITMAP_MAX_RADIUS = 60.0f;
    public static final boolean DEBUG = true;
    private static final float SCREENSHOT_MAX_RADIUS = 30.0f;
    public static final String TAG = "BlurInOutAnimator";
    private ValueAnimator mAnimator;
    private Bitmap mBaseBitmap;
    private GraphicBuffer mBaseBuffer;
    private Bitmap mBlurBitmap;
    private Interpolator mBlurInInterpolator;
    private Interpolator mBlurOutInterpolator;
    private Context mContext;
    private boolean mInitialized;
    private BlurInOutAnimatorListener mListener;
    private float mMaxRadius;
    private boolean mScreenshotBlur;
    private ImageView mTargetView;

    public interface BlurInOutAnimatorListener {
        void onAnimationEnd(Animator animation, BlurType type);

        void onAnimationStart(Animator animation, BlurType type);

        void onAnimationUpdate(ValueAnimator animation, BlurType type);
    }

    public enum BlurType {
        BLUR_IN,
        BLUR_OUT
    }

    public BlurInOutAnimator(Context context) {
        this(context, null);
    }

    public BlurInOutAnimator(Context context, ImageView targetView) {
        this.mTargetView = null;
        this.mAnimator = null;
        this.mBlurInInterpolator = new DecelerateInterpolator(0.8f);
        this.mBlurOutInterpolator = new DecelerateInterpolator(1.2f);
        this.mListener = null;
        this.mBaseBitmap = null;
        this.mBlurBitmap = null;
        this.mContext = null;
        this.mInitialized = false;
        this.mScreenshotBlur = false;
        this.mContext = context;
        setTargetView(targetView);
    }

    public void setTargetView(ImageView targetView) {
        this.mTargetView = targetView;
    }

    public void startBlurIn(int duration) {
        start(BlurType.BLUR_IN, duration);
    }

    public void startBlurOut(int duration) {
        start(BlurType.BLUR_OUT, duration);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mScreenshotBlur = false;
        if (bitmap == null) {
            LGLog.w(TAG, "Bitmap is null", new int[0]);
            this.mBaseBitmap = null;
        } else {
            this.mMaxRadius = BITMAP_MAX_RADIUS;
            this.mBaseBitmap = bitmap;
        }
    }

    public void setScreenshotBuffer(int scale) {
        this.mScreenshotBlur = true;
        this.mBaseBuffer = null;
    }

    public void start(final BlurType type, int duration) {
        StaticBlurEngine.getInstance().setBlurAnimatorRunning(true);
        int i = AnonymousClass3.$SwitchMap$com$lge$launcher3$wallpaperblur$BlurInOutAnimator$BlurType[type.ordinal()];
        if (i == 1) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.1f, 1.0f);
            this.mAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setInterpolator(this.mBlurInInterpolator);
        } else {
            if (i != 2) {
                return;
            }
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(1.0f, 0.1f);
            this.mAnimator = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.setInterpolator(this.mBlurOutInterpolator);
        }
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wallpaperblur.BlurInOutAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                LGLog.i(BlurInOutAnimator.TAG, String.format("%s onAnimationStart()", type));
                if (BlurInOutAnimator.this.mListener != null) {
                    BlurInOutAnimator.this.mListener.onAnimationStart(animation, type);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LGLog.i(BlurInOutAnimator.TAG, String.format("%s onAnimationEnd()", type));
                if (BlurInOutAnimator.this.mListener != null) {
                    BlurInOutAnimator.this.mListener.onAnimationEnd(animation, type);
                }
            }
        });
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.wallpaperblur.BlurInOutAnimator.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue == null || !(animatedValue instanceof Float)) {
                    return;
                }
                Float f = (Float) animatedValue;
                float fFloatValue = f.floatValue();
                int i2 = (int) (BlurInOutAnimator.this.mMaxRadius * fFloatValue);
                if (BlurInOutAnimator.this.mScreenshotBlur) {
                    if (BlurInOutAnimator.this.mBaseBuffer == null || BlurInOutAnimator.this.mBlurBitmap == null) {
                        LGLog.i(BlurInOutAnimator.TAG, "Cannot draw BlurInOutAnimator");
                        return;
                    } else {
                        LGLog.i(BlurInOutAnimator.TAG, String.format("%s onAnimationUpdate(%.2f) - radius %d", type, f, Integer.valueOf(i2)));
                        BlurInOutAnimator.this.mTargetView.setImageBitmap(BlurInOutAnimator.this.mBlurBitmap);
                        BlurInOutAnimator.this.mTargetView.setAlpha(1.0f);
                    }
                } else {
                    float f2 = 3.3333333f * fFloatValue;
                    LGLog.i(BlurInOutAnimator.TAG, String.format("%s onAnimationUpdate(%.2f) - radius %d alpha %f", type, f, Integer.valueOf(i2), Float.valueOf(f2)));
                    if (fFloatValue <= 0.3f) {
                        BlurInOutAnimator.this.mTargetView.setAlpha(f2);
                    } else if (BlurInOutAnimator.this.mBaseBitmap == null || BlurInOutAnimator.this.mBlurBitmap == null) {
                        LGLog.i(BlurInOutAnimator.TAG, "Cannot draw BlurInOutAnimator");
                        return;
                    } else {
                        BlurInOutAnimator.this.mTargetView.setAlpha(1.0f);
                        BlurInOutAnimator.this.mTargetView.setImageBitmap(BlurInOutAnimator.this.mBlurBitmap);
                    }
                }
                if (BlurInOutAnimator.this.mListener != null) {
                    BlurInOutAnimator.this.mListener.onAnimationUpdate(animation, type);
                }
            }
        });
        this.mAnimator.setDuration(duration);
        this.mAnimator.start();
        LGLog.i(TAG, String.format("%s start()", type));
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wallpaperblur.BlurInOutAnimator$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wallpaperblur$BlurInOutAnimator$BlurType;

        static {
            int[] iArr = new int[BlurType.values().length];
            $SwitchMap$com$lge$launcher3$wallpaperblur$BlurInOutAnimator$BlurType = iArr;
            try {
                iArr[BlurType.BLUR_IN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$BlurInOutAnimator$BlurType[BlurType.BLUR_OUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
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
        LGLog.i(TAG, String.format("clear()", new Object[0]));
        StaticBlurEngine.getInstance().setBlurAnimatorRunning(false);
        this.mBaseBitmap = null;
        this.mBaseBuffer = null;
        this.mBlurBitmap = null;
        this.mInitialized = false;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            return;
        }
        valueAnimator.removeAllUpdateListeners();
        this.mAnimator.removeAllListeners();
        if (this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        this.mAnimator = null;
    }

    public void addListener(BlurInOutAnimatorListener listener) {
        this.mListener = listener;
    }

    public void destroy() {
        LGLog.i(TAG, String.format("destroy()", new Object[0]));
        this.mListener = null;
        clear();
        this.mTargetView = null;
    }
}
