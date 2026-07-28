package com.lge.launcher3.wallpaperblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class WidgetBlurView extends ImageView {
    public static final boolean DEBUG = false;
    private static final int FADE_IN_DURATION = 100;
    private static final int FADE_OUT_DURATION = 100;
    public static final String TAG = "WidgetBlurView";
    private Bitmap mBlurredImage;
    private FadeInOutAnimator mFadeInOutAnimation;
    private boolean mIsAnimated;
    private boolean mIsEnableChanged;
    private boolean mIsEnabled;
    private boolean mNeedToUpdateBlurredImage;

    public WidgetBlurView(Context context) {
        super(context);
        this.mBlurredImage = null;
        this.mIsEnabled = false;
        this.mIsAnimated = false;
        this.mIsEnableChanged = false;
        this.mNeedToUpdateBlurredImage = true;
        this.mFadeInOutAnimation = null;
        setScaleType(ImageView.ScaleType.FIT_XY);
        setAlpha(0.0f);
        this.mNeedToUpdateBlurredImage = true;
        FadeInOutAnimator fadeInOutAnimator = new FadeInOutAnimator(this);
        this.mFadeInOutAnimation = fadeInOutAnimator;
        fadeInOutAnimator.setFadeInInterpolator(new DecelerateInterpolator(0.8f));
        this.mFadeInOutAnimation.setFadeOutInterpolator(new DecelerateInterpolator(1.2f));
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mNeedToUpdateBlurredImage || !this.mIsEnableChanged) {
            return;
        }
        FadeInOutAnimator fadeInOutAnimator = this.mFadeInOutAnimation;
        if (fadeInOutAnimator != null) {
            if (this.mIsEnabled) {
                fadeInOutAnimator.startFadeIn(this.mIsAnimated ? 100 : 0);
            } else {
                fadeInOutAnimator.startFadeOut(this.mIsAnimated ? 100 : 0);
            }
        }
        this.mIsEnableChanged = false;
    }

    public boolean shouldUpdateBlurredImage() {
        return this.mIsEnabled && this.mNeedToUpdateBlurredImage;
    }

    public void enable(boolean enable, boolean animate) {
        if (this.mIsEnabled == enable) {
            return;
        }
        this.mIsEnabled = enable;
        this.mIsAnimated = animate;
        this.mIsEnableChanged = true;
        invalidate();
    }

    public void updateBlurredImage() {
        this.mNeedToUpdateBlurredImage = true;
        invalidate();
    }

    public void setBlurredImage(Bitmap image) {
        LGLog.i(TAG, "setBlurredImage");
        if (this.mNeedToUpdateBlurredImage) {
            clearBlurredImage();
            this.mBlurredImage = image;
            setImageBitmap(image);
            this.mNeedToUpdateBlurredImage = image == null;
        }
    }

    public void clearBlurredImage() {
        LGLog.i(TAG, "clearBlurredImage");
        Bitmap bitmap = this.mBlurredImage;
        if (bitmap == null) {
            return;
        }
        if (!bitmap.isRecycled()) {
            this.mBlurredImage.recycle();
        }
        this.mBlurredImage = null;
    }

    public void destroy() {
        clearBlurredImage();
        FadeInOutAnimator fadeInOutAnimator = this.mFadeInOutAnimation;
        if (fadeInOutAnimator != null) {
            fadeInOutAnimator.destroy();
            this.mFadeInOutAnimation = null;
        }
    }
}
