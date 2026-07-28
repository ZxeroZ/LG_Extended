package com.lge.launcher3.wing.carousel.transformer;

import android.view.View;
import com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager;
import com.lge.launcher3.wing.carousel.widget.CarouselView;

/* JADX INFO: loaded from: classes2.dex */
public class ParameterizableViewTransformer implements CarouselView.ViewTransformer {
    protected static final float EPS = 0.001f;
    protected float mOffsetXPercent = 0.0f;
    protected float mOffsetYPercent = 0.0f;
    protected float mMinScaleX = Float.NaN;
    protected float mMaxScaleX = Float.NaN;
    protected float mScaleXOffset = Float.NaN;
    protected float mScaleXFactor = Float.NaN;
    protected float mMinScaleY = Float.NaN;
    protected float mMaxScaleY = Float.NaN;
    protected float mScaleYOffset = Float.NaN;
    protected float mScaleYFactor = Float.NaN;
    protected float mRotateDegree = Float.NaN;
    protected boolean mScaleLargestAtCenter = false;
    protected float mRotateDistFactor = Float.NaN;

    private static boolean isNonZero(float f) {
        return f > EPS || f < -0.001f;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.ViewTransformer
    public void onAttach(CarouselLayoutManager layoutManager) {
    }

    protected ParameterizableViewTransformer() {
    }

    protected float getOffsetXPercent() {
        return this.mOffsetXPercent;
    }

    protected void setOffsetXPercent(float offsetXPercent) {
        this.mOffsetXPercent = offsetXPercent;
    }

    protected float getOffsetYPercent() {
        return this.mOffsetYPercent;
    }

    protected void setOffsetYPercent(float offsetYPercent) {
        this.mOffsetYPercent = offsetYPercent;
    }

    protected float getRotateDegree() {
        return this.mRotateDegree;
    }

    protected void setRotateDegree(float rotateDegree) {
        this.mRotateDegree = rotateDegree;
        if (Float.isNaN(rotateDegree)) {
            this.mRotateDistFactor = Float.NaN;
        } else if (!isNonZero(rotateDegree)) {
            this.mRotateDistFactor = 0.0f;
        } else {
            this.mRotateDistFactor = (float) (1.0d / Math.sin(Math.toRadians(rotateDegree)));
        }
    }

    protected float getMinScaleX() {
        return this.mMinScaleX;
    }

    protected void setMinScaleX(float minScaleX) {
        this.mMinScaleX = minScaleX;
    }

    protected float getMaxScaleX() {
        return this.mMaxScaleX;
    }

    protected void setMaxScaleX(float maxScaleX) {
        this.mMaxScaleX = maxScaleX;
    }

    protected float getScaleXFactor() {
        return this.mScaleXFactor;
    }

    protected void setScaleXFactor(float scaleXFactor) {
        this.mScaleXFactor = scaleXFactor;
    }

    protected float getMinScaleY() {
        return this.mMinScaleY;
    }

    protected void setMinScaleY(float minScaleY) {
        this.mMinScaleY = minScaleY;
    }

    protected float getMaxScaleY() {
        return this.mMaxScaleY;
    }

    protected void setMaxScaleY(float maxScaleY) {
        this.mMaxScaleY = maxScaleY;
    }

    protected float getScaleYFactor() {
        return this.mScaleYFactor;
    }

    protected void setScaleYFactor(float scaleYFactor) {
        this.mScaleYFactor = scaleYFactor;
    }

    protected float getScaleXOffset() {
        return this.mScaleXOffset;
    }

    protected void setScaleXOffset(float scaleXOffset) {
        this.mScaleXOffset = scaleXOffset;
    }

    protected float getScaleYOffset() {
        return this.mScaleYOffset;
    }

    protected void setScaleYOffset(float scaleYOffset) {
        this.mScaleYOffset = scaleYOffset;
    }

    protected boolean isScaleLargestAtCenter() {
        return this.mScaleLargestAtCenter;
    }

    protected void setScaleLargestAtCenter(boolean scaleLargestAtCenter) {
        this.mScaleLargestAtCenter = scaleLargestAtCenter;
    }

    @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.ViewTransformer
    public void transform(View view, float position) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        if (!Float.isNaN(this.mScaleXFactor)) {
            float fAbs = (this.mScaleLargestAtCenter ? 1.0f - Math.abs(position) : position) * this.mScaleXFactor;
            if (!Float.isNaN(this.mScaleXOffset)) {
                fAbs += this.mScaleXOffset;
            }
            if (!Float.isNaN(this.mMinScaleX)) {
                fAbs = Math.max(this.mMinScaleX, fAbs);
            }
            if (!Float.isNaN(this.mMaxScaleX)) {
                fAbs = Math.min(this.mMaxScaleX, fAbs);
            }
            view.setPivotX(measuredWidth / 2.0f);
            view.setPivotY(measuredHeight / 2.0f);
            view.setScaleX(fAbs);
        }
        if (!Float.isNaN(this.mScaleYFactor)) {
            float fAbs2 = (this.mScaleLargestAtCenter ? 1.0f - Math.abs(position) : position) * this.mScaleYFactor;
            if (!Float.isNaN(this.mScaleYOffset)) {
                fAbs2 += this.mScaleYOffset;
            }
            if (!Float.isNaN(this.mMinScaleY)) {
                fAbs2 = Math.max(this.mMinScaleY, fAbs2);
            }
            if (!Float.isNaN(this.mMaxScaleY)) {
                fAbs2 = Math.min(this.mMaxScaleY, fAbs2);
            }
            view.setPivotX(measuredWidth / 2.0f);
            view.setPivotY(measuredHeight / 2.0f);
            view.setScaleY(fAbs2);
        }
        if (isNonZero(this.mRotateDegree)) {
            float f = measuredWidth;
            view.setPivotX(f / 2.0f);
            view.setPivotY(measuredHeight + (f * this.mRotateDistFactor));
            view.setRotation(this.mRotateDegree * position);
        }
        if (isNonZero(this.mOffsetXPercent)) {
            view.setTranslationX(measuredWidth * position * this.mOffsetXPercent);
        }
        if (isNonZero(this.mOffsetYPercent)) {
            view.setTranslationY(position * measuredHeight * this.mOffsetYPercent);
        }
    }
}
