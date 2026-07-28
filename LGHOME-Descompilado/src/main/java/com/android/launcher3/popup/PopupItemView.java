package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.LogAccelerateInterpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.PillRevealOutlineProvider;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class PopupItemView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    protected static final Point sTempPoint = new Point();
    private final Paint mBackgroundClipPaint;
    protected View mIconView;
    protected final boolean mIsRtl;
    private final Matrix mMatrix;
    private float mOpenAnimationProgress;
    protected final Rect mPillRect;
    private Bitmap mRoundedCornerBitmap;

    public PopupItemView(Context context) {
        this(context, null, 0);
    }

    public PopupItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Paint paint = new Paint(5);
        this.mBackgroundClipPaint = paint;
        this.mMatrix = new Matrix();
        this.mPillRect = new Rect();
        int backgroundRadius = (int) getBackgroundRadius();
        this.mRoundedCornerBitmap = Bitmap.createBitmap(backgroundRadius, backgroundRadius, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas();
        canvas.setBitmap(this.mRoundedCornerBitmap);
        float f = backgroundRadius * 2;
        canvas.drawArc(0.0f, 0.0f, f, f, 180.0f, 90.0f, true, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = findViewById(R.id.popup_item_icon);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mPillRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int iSaveLayer = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null);
        super.dispatchDraw(canvas);
        float width = this.mRoundedCornerBitmap.getWidth();
        float height = this.mRoundedCornerBitmap.getHeight();
        this.mMatrix.reset();
        canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        float f = width / 2.0f;
        float f2 = height / 2.0f;
        this.mMatrix.setRotate(90.0f, f, f2);
        this.mMatrix.postTranslate(canvas.getWidth() - width, 0.0f);
        canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        this.mMatrix.setRotate(180.0f, f, f2);
        this.mMatrix.postTranslate(canvas.getWidth() - width, canvas.getHeight() - height);
        canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        this.mMatrix.setRotate(270.0f, f, f2);
        this.mMatrix.postTranslate(0.0f, canvas.getHeight() - height);
        canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        canvas.restoreToCount(iSaveLayer);
    }

    public Animator createOpenAnimation(boolean isContainerAboveIcon, boolean pivotLeft) {
        Point iconCenter = getIconCenter();
        ValueAnimator valueAnimatorCreateRevealAnimator = new ZoomRevealOutlineProvider(iconCenter.x, iconCenter.y, this.mPillRect, this, this.mIconView, isContainerAboveIcon, pivotLeft, getResources().getDimensionPixelSize(this.mIsRtl ^ pivotLeft ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end)).createRevealAnimator(this, false);
        this.mOpenAnimationProgress = 0.0f;
        valueAnimatorCreateRevealAnimator.addUpdateListener(this);
        return valueAnimatorCreateRevealAnimator;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.mOpenAnimationProgress = valueAnimator.getAnimatedFraction();
    }

    public boolean isOpenOrOpening() {
        return this.mOpenAnimationProgress > 0.0f;
    }

    public Animator createCloseAnimation(boolean isContainerAboveIcon, boolean pivotLeft, long duration) {
        Point iconCenter = getIconCenter();
        ValueAnimator valueAnimatorCreateRevealAnimator = new ZoomRevealOutlineProvider(iconCenter.x, iconCenter.y, this.mPillRect, this, this.mIconView, isContainerAboveIcon, pivotLeft, getResources().getDimensionPixelSize(this.mIsRtl ^ pivotLeft ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end)).createRevealAnimator(this, true);
        valueAnimatorCreateRevealAnimator.setDuration((long) (duration * this.mOpenAnimationProgress));
        valueAnimatorCreateRevealAnimator.setInterpolator(new CloseInterpolator(this.mOpenAnimationProgress));
        valueAnimatorCreateRevealAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupItemView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PopupItemView.this.mOpenAnimationProgress = 0.0f;
            }
        });
        return valueAnimatorCreateRevealAnimator;
    }

    public Point getIconCenter() {
        Point point = sTempPoint;
        point.y = getMeasuredHeight() / 2;
        point.x = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_height) / 2;
        if (Utilities.isRtl(getResources())) {
            point.x = getMeasuredWidth() - point.x;
        }
        return point;
    }

    protected float getBackgroundRadius() {
        return getResources().getDimensionPixelSize(R.dimen.bg_round_rect_radius);
    }

    private static class ZoomRevealOutlineProvider extends PillRevealOutlineProvider {
        private final float mArrowCenter;
        private final float mFullHeight;
        private final boolean mPivotLeft;
        private final View mTranslateView;
        private final float mTranslateX;
        private final float mTranslateYMultiplier;
        private final View mZoomView;

        public ZoomRevealOutlineProvider(int x, int y, Rect pillRect, PopupItemView translateView, View zoomView, boolean isContainerAboveIcon, boolean pivotLeft, float arrowCenter) {
            super(x, y, pillRect, translateView.getBackgroundRadius());
            this.mTranslateView = translateView;
            this.mZoomView = zoomView;
            this.mFullHeight = pillRect.height();
            this.mTranslateYMultiplier = isContainerAboveIcon ? 0.5f : -0.5f;
            this.mPivotLeft = pivotLeft;
            this.mTranslateX = pivotLeft ? arrowCenter : pillRect.right - arrowCenter;
            this.mArrowCenter = arrowCenter;
        }

        @Override // com.android.launcher3.util.PillRevealOutlineProvider
        public void setProgress(float progress) {
            super.setProgress(progress);
            View view = this.mZoomView;
            if (view != null) {
                view.setScaleX(progress);
                this.mZoomView.setScaleY(progress);
            }
            this.mTranslateView.setTranslationY(this.mTranslateYMultiplier * (this.mFullHeight - this.mOutline.height()));
            float fMin = Math.min(this.mOutline.width(), this.mArrowCenter);
            this.mTranslateView.setTranslationX(this.mTranslateX - (this.mPivotLeft ? this.mOutline.left + fMin : this.mOutline.right - fMin));
        }
    }

    private static class CloseInterpolator extends LogAccelerateInterpolator {
        private float mRemainingProgress;
        private float mStartProgress;

        public CloseInterpolator(float openAnimationProgress) {
            super(100, 0);
            this.mStartProgress = 1.0f - openAnimationProgress;
            this.mRemainingProgress = openAnimationProgress;
        }

        @Override // com.android.launcher3.LogAccelerateInterpolator, android.animation.TimeInterpolator
        public float getInterpolation(float v) {
            return this.mStartProgress + (super.getInterpolation(v) * this.mRemainingProgress);
        }
    }
}
