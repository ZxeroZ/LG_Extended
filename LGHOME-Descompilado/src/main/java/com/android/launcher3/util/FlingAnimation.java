package com.android.launcher3.util;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.DropTarget;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;

/* JADX INFO: loaded from: classes.dex */
public class FlingAnimation implements ValueAnimator.AnimatorUpdateListener {
    private static final int DRAG_END_DELAY = 300;
    private static final float MAX_ACCELERATION = 0.5f;
    protected float mAX;
    protected float mAY;
    protected final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);
    protected final float mAnimationTimeFraction;
    protected final DragLayer mDragLayer;
    protected final DropTarget.DragObject mDragObject;
    protected final int mDuration;
    protected final Rect mFrom;
    protected final Rect mIconRect;
    protected final float mUX;
    protected final float mUY;

    public FlingAnimation(DropTarget.DragObject d, PointF vel, Rect iconRect, DragLayer dragLayer) {
        this.mDragObject = d;
        this.mUX = vel.x / 1000.0f;
        this.mUY = vel.y / 1000.0f;
        this.mIconRect = iconRect;
        this.mDragLayer = dragLayer;
        Rect rect = new Rect();
        this.mFrom = rect;
        dragLayer.getViewRectRelativeToSelf(d.dragView, rect);
        float scaleX = d.dragView.getScaleX() - 1.0f;
        float measuredWidth = (d.dragView.getMeasuredWidth() * scaleX) / 2.0f;
        float measuredHeight = (scaleX * d.dragView.getMeasuredHeight()) / 2.0f;
        rect.left = (int) (rect.left + measuredWidth);
        rect.right = (int) (rect.right - measuredWidth);
        rect.top = (int) (rect.top + measuredHeight);
        rect.bottom = (int) (rect.bottom - measuredHeight);
        int iInitDuration = initDuration();
        this.mDuration = iInitDuration;
        this.mAnimationTimeFraction = iInitDuration / (iInitDuration + 300);
    }

    protected int initDuration() {
        float f = -this.mFrom.bottom;
        float f2 = this.mUY;
        float f3 = (f2 * f2) + (f * 2.0f * 0.5f);
        if (f3 >= 0.0f) {
            this.mAY = 0.5f;
        } else {
            this.mAY = (f2 * f2) / ((-f) * 2.0f);
            f3 = 0.0f;
        }
        double dSqrt = (((double) (-f2)) - Math.sqrt(f3)) / ((double) this.mAY);
        this.mAX = (float) (((((double) ((-this.mFrom.exactCenterX()) + this.mIconRect.exactCenterX())) - (((double) this.mUX) * dSqrt)) * 2.0d) / (dSqrt * dSqrt));
        return (int) Math.round(dSqrt);
    }

    public final int getDuration() {
        return this.mDuration + 300;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = animation.getAnimatedFraction();
        float f = this.mAnimationTimeFraction;
        float f2 = animatedFraction > f ? 1.0f : animatedFraction / f;
        DragView dragView = (DragView) this.mDragLayer.getAnimatedView();
        float f3 = this.mDuration * f2;
        dragView.setTranslationX((this.mUX * f3) + this.mFrom.left + (((this.mAX * f3) * f3) / 2.0f));
        dragView.setTranslationY((this.mUY * f3) + this.mFrom.top + (((this.mAY * f3) * f3) / 2.0f));
        dragView.setAlpha(1.0f - this.mAlphaInterpolator.getInterpolation(f2));
    }
}
