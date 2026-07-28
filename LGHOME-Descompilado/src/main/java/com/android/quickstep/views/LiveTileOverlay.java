package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.view.ViewOverlay;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.anim.Interpolators;

/* JADX INFO: loaded from: classes.dex */
public class LiveTileOverlay extends Drawable {
    private static final long ICON_ANIM_DURATION = 120;
    private final Rect mBoundsRect;
    private float mCornerRadius;
    private RectF mCurrentRect;
    private boolean mDrawEnabled;
    private Drawable mIcon;
    private float mIconAnimationProgress;
    private Animator mIconAnimator;
    private boolean mIsAttached;
    private final Paint mPaint;
    private static final FloatProperty<LiveTileOverlay> PROGRESS = new FloatProperty<LiveTileOverlay>(NotificationCompat.CATEGORY_PROGRESS) { // from class: com.android.quickstep.views.LiveTileOverlay.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(LiveTileOverlay liveTileOverlay, float progress) {
            liveTileOverlay.setIconAnimationProgress(progress);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(LiveTileOverlay liveTileOverlay) {
            return Float.valueOf(liveTileOverlay.mIconAnimationProgress);
        }
    };
    public static final LiveTileOverlay INSTANCE = new LiveTileOverlay();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    private LiveTileOverlay() {
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mBoundsRect = new Rect();
        this.mDrawEnabled = true;
        this.mIconAnimationProgress = 0.0f;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void update(RectF currentRect, float cornerRadius) {
        invalidateSelf();
        this.mCurrentRect = currentRect;
        this.mCornerRadius = cornerRadius;
        currentRect.roundOut(this.mBoundsRect);
        setBounds(this.mBoundsRect);
        invalidateSelf();
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    public void startIconAnimation() {
        Animator animator = this.mIconAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, PROGRESS, 1.0f);
        this.mIconAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.setDuration(120L).setInterpolator(Interpolators.LINEAR);
        this.mIconAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.views.LiveTileOverlay.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LiveTileOverlay.this.mIconAnimator = null;
            }
        });
        this.mIconAnimator.start();
    }

    public float cancelIconAnimation() {
        Animator animator = this.mIconAnimator;
        if (animator != null) {
            animator.cancel();
        }
        return this.mIconAnimationProgress;
    }

    public void setDrawEnabled(boolean drawEnabled) {
        if (this.mDrawEnabled != drawEnabled) {
            this.mDrawEnabled = drawEnabled;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        RectF rectF = this.mCurrentRect;
        if (rectF == null || !this.mDrawEnabled) {
            return;
        }
        float f = this.mCornerRadius;
        canvas.drawRoundRect(rectF, f, f, this.mPaint);
        if (this.mIcon == null || this.mIconAnimationProgress <= 0.0f) {
            return;
        }
        canvas.save();
        float interpolation = Interpolators.clampToProgress(Interpolators.FAST_OUT_SLOW_IN, 0.0f, 1.0f).getInterpolation(this.mIconAnimationProgress);
        canvas.translate(this.mCurrentRect.centerX() - ((this.mIcon.getBounds().width() / 2) * interpolation), this.mCurrentRect.top - ((this.mIcon.getBounds().height() / 2) * interpolation));
        canvas.scale(interpolation, interpolation);
        this.mIcon.draw(canvas);
        canvas.restore();
    }

    public boolean attach(ViewOverlay overlay) {
        if (overlay == null || this.mIsAttached) {
            return false;
        }
        overlay.add(this);
        this.mIsAttached = true;
        return true;
    }

    public void detach(ViewOverlay overlay) {
        if (overlay != null) {
            overlay.remove(this);
            this.mIsAttached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIconAnimationProgress(float progress) {
        this.mIconAnimationProgress = progress;
        invalidateSelf();
    }
}
