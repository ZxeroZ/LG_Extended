package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.android.launcher3.FastBitmapDrawable;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PreloadIconDrawable extends Drawable {
    private static final float ANIMATION_PROGRESS_COMPLETED = 1.0f;
    private static final float ANIMATION_PROGRESS_STARTED = 0.0f;
    private static final float ANIMATION_PROGRESS_STOPPED = -1.0f;
    private static final int DEFAULT_COLOR = -16738680;
    private static final float ICON_SCALE_FACTOR = 0.5f;
    private static final float MIN_LIGHTNESS = 0.6f;
    private static final float MIN_SATUNATION = 0.2f;
    private static final Rect sTempRect = new Rect();
    private ObjectAnimator mAnimator;
    private Drawable mBgDrawable;
    public final Drawable mIcon;
    private boolean mIndicatorRectDirty;
    private final Paint mPaint;
    private int mRingOutset;
    private final RectF mIndicatorRect = new RectF();
    private int mIndicatorColor = 0;
    private int mProgress = 0;
    private float mAnimationProgress = ANIMATION_PROGRESS_STOPPED;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public PreloadIconDrawable(Drawable icon, Resources.Theme theme) {
        this.mIcon = icon;
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        setBounds(icon.getBounds());
        applyPreloaderTheme(theme);
        onLevelChange(0);
    }

    public void applyPreloaderTheme(Resources.Theme t) {
        TypedArray typedArrayObtainStyledAttributes = t.obtainStyledAttributes(R.styleable.PreloadIconDrawable);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        this.mBgDrawable = drawable;
        drawable.setFilterBitmap(true);
        this.mPaint.setStrokeWidth(typedArrayObtainStyledAttributes.getDimension(1, 0.0f));
        this.mRingOutset = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, 0);
        typedArrayObtainStyledAttributes.recycle();
        onBoundsChange(getBounds());
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.mIcon.setBounds(bounds);
        if (this.mBgDrawable != null) {
            Rect rect = sTempRect;
            rect.set(bounds);
            int i = this.mRingOutset;
            rect.inset(-i, -i);
            this.mBgDrawable.setBounds(rect);
        }
        this.mIndicatorRectDirty = true;
    }

    public int getOutset() {
        return this.mRingOutset;
    }

    private void initIndicatorRect() {
        Drawable drawable = this.mBgDrawable;
        Rect bounds = drawable.getBounds();
        drawable.getPadding(sTempRect);
        float fWidth = bounds.width() / drawable.getIntrinsicWidth();
        float fHeight = bounds.height() / drawable.getIntrinsicHeight();
        this.mIndicatorRect.set(bounds.left + (r2.left * fWidth), bounds.top + (r2.top * fHeight), bounds.right - (r2.right * fWidth), bounds.bottom - (r2.bottom * fHeight));
        float strokeWidth = this.mPaint.getStrokeWidth() / 2.0f;
        this.mIndicatorRect.inset(strokeWidth, strokeWidth);
        this.mIndicatorRectDirty = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect rect = new Rect(getBounds());
        Rect rect2 = sTempRect;
        if (!canvas.getClipBounds(rect2) || Rect.intersects(rect2, rect)) {
            if (this.mIndicatorRectDirty) {
                initIndicatorRect();
            }
            float f = this.mAnimationProgress;
            float f2 = 0.5f;
            if (f >= 0.0f && f < 1.0f) {
                this.mPaint.setAlpha((int) ((1.0f - f) * 255.0f));
                this.mBgDrawable.setAlpha(this.mPaint.getAlpha());
                this.mBgDrawable.draw(canvas);
                canvas.drawOval(this.mIndicatorRect, this.mPaint);
                f2 = 0.5f + (this.mAnimationProgress * 0.5f);
            } else if (f == ANIMATION_PROGRESS_STOPPED) {
                this.mPaint.setAlpha(255);
                this.mBgDrawable.setAlpha(255);
                this.mBgDrawable.draw(canvas);
                int i = this.mProgress;
                if (i >= 100) {
                    canvas.drawOval(this.mIndicatorRect, this.mPaint);
                } else if (i > 0) {
                    canvas.drawArc(this.mIndicatorRect, -90.0f, i * 3.6f, false, this.mPaint);
                }
            } else {
                f2 = 1.0f;
            }
            canvas.save();
            canvas.scale(f2, f2, rect.exactCenterX(), rect.exactCenterY());
            this.mIcon.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mIcon.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mIcon.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        this.mProgress = level;
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        this.mAnimationProgress = ANIMATION_PROGRESS_STOPPED;
        if (level > 0) {
            this.mPaint.setColor(getIndicatorColor());
        }
        Drawable drawable = this.mIcon;
        if (drawable instanceof FastBitmapDrawable) {
            ((FastBitmapDrawable) drawable).setState(level <= 0 ? FastBitmapDrawable.State.DISABLED : FastBitmapDrawable.State.NORMAL);
        }
        invalidateSelf();
        return true;
    }

    public void maybePerformFinishedAnimation() {
        if (this.mAnimationProgress > ANIMATION_PROGRESS_STOPPED) {
            return;
        }
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        setAnimationProgress(0.0f);
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "animationProgress", 0.0f, 1.0f);
        this.mAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.start();
    }

    public void setAnimationProgress(float progress) {
        if (progress != this.mAnimationProgress) {
            this.mAnimationProgress = progress;
            invalidateSelf();
        }
    }

    public float getAnimationProgress() {
        return this.mAnimationProgress;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIcon.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIcon.getIntrinsicWidth();
    }

    private int getIndicatorColor() {
        int i = this.mIndicatorColor;
        if (i != 0) {
            return i;
        }
        Drawable drawable = this.mIcon;
        if (!(drawable instanceof FastBitmapDrawable)) {
            this.mIndicatorColor = DEFAULT_COLOR;
            return DEFAULT_COLOR;
        }
        int iFindDominantColorByHue = Utilities.findDominantColorByHue(((FastBitmapDrawable) drawable).getBitmap(), 20);
        this.mIndicatorColor = iFindDominantColorByHue;
        float[] fArr = new float[3];
        Color.colorToHSV(iFindDominantColorByHue, fArr);
        if (fArr[1] < 0.2f) {
            this.mIndicatorColor = DEFAULT_COLOR;
            return DEFAULT_COLOR;
        }
        fArr[2] = Math.max(MIN_LIGHTNESS, fArr[2]);
        int iHSVToColor = Color.HSVToColor(fArr);
        this.mIndicatorColor = iHSVToColor;
        return iHSVToColor;
    }
}
