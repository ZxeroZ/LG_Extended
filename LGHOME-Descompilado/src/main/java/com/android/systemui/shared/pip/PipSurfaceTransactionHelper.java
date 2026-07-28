package com.android.systemui.shared.pip;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.window.PictureInPictureSurfaceTransaction;

/* JADX INFO: loaded from: classes.dex */
public class PipSurfaceTransactionHelper {
    private final int mCornerRadius;
    private final int mShadowRadius;
    private final Matrix mTmpTransform = new Matrix();
    private final float[] mTmpFloat9 = new float[9];
    private final RectF mTmpSourceRectF = new RectF();
    private final RectF mTmpDestinationRectF = new RectF();
    private final Rect mTmpDestinationRect = new Rect();

    public PipSurfaceTransactionHelper(int i, int i2) {
        this.mCornerRadius = i;
        this.mShadowRadius = i2;
    }

    public PictureInPictureSurfaceTransaction scale(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2) {
        float f = rect2.left;
        float f2 = rect2.top;
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRectF.set(rect2);
        this.mTmpDestinationRectF.offsetTo(0.0f, 0.0f);
        this.mTmpTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix.ScaleToFit.FILL);
        float scaledCornerRadius = getScaledCornerRadius(rect, rect2);
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setPosition(surfaceControl, f, f2).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, this.mShadowRadius);
        return newPipSurfaceTransaction(f, f2, this.mTmpFloat9, 0.0f, scaledCornerRadius, this.mShadowRadius, rect);
    }

    public PictureInPictureSurfaceTransaction scale(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, float f, float f2, float f3) {
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRectF.set(rect2);
        this.mTmpDestinationRectF.offsetTo(0.0f, 0.0f);
        this.mTmpTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix.ScaleToFit.FILL);
        this.mTmpTransform.postRotate(f, 0.0f, 0.0f);
        float scaledCornerRadius = getScaledCornerRadius(rect, rect2);
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setPosition(surfaceControl, f2, f3).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, this.mShadowRadius);
        return newPipSurfaceTransaction(f2, f3, this.mTmpFloat9, f, scaledCornerRadius, this.mShadowRadius, rect);
    }

    public PictureInPictureSurfaceTransaction scaleAndCrop(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, Rect rect4) {
        float fHeight;
        int iHeight;
        float f;
        float fHeight2;
        int iHeight2;
        this.mTmpSourceRectF.set(rect2);
        this.mTmpDestinationRect.set(rect2);
        this.mTmpDestinationRect.inset(rect4);
        if (rect.isEmpty() || rect.width() == rect2.width()) {
            if (rect2.width() <= rect2.height()) {
                fHeight = rect3.width();
                iHeight = rect2.width();
            } else {
                fHeight = rect3.height();
                iHeight = rect2.height();
            }
            f = fHeight / iHeight;
        } else {
            if (rect.width() <= rect.height()) {
                fHeight2 = rect3.width();
                iHeight2 = rect.width();
            } else {
                fHeight2 = rect3.height();
                iHeight2 = rect.height();
            }
            f = fHeight2 / iHeight2;
        }
        float f2 = rect3.left - ((rect4.left + rect2.left) * f);
        float f3 = rect3.top - ((rect4.top + rect2.top) * f);
        this.mTmpTransform.setScale(f, f);
        float scaledCornerRadius = getScaledCornerRadius(this.mTmpDestinationRect, rect3);
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setCrop(surfaceControl, this.mTmpDestinationRect).setPosition(surfaceControl, f2, f3).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, this.mShadowRadius);
        return newPipSurfaceTransaction(f2, f3, this.mTmpFloat9, 0.0f, scaledCornerRadius, this.mShadowRadius, this.mTmpDestinationRect);
    }

    public PictureInPictureSurfaceTransaction scaleAndRotate(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, float f, float f2, float f3) {
        float fHeight;
        int iHeight;
        float f4;
        float f5;
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRect.set(rect);
        this.mTmpDestinationRect.inset(rect3);
        if (rect.width() <= rect.height()) {
            fHeight = rect2.width();
            iHeight = rect.width();
        } else {
            fHeight = rect2.height();
            iHeight = rect.height();
        }
        float f6 = fHeight / iHeight;
        this.mTmpTransform.setRotate(f, 0.0f, 0.0f);
        this.mTmpTransform.postScale(f6, f6);
        float scaledCornerRadius = getScaledCornerRadius(this.mTmpDestinationRect, rect2);
        if (f < 0.0f) {
            f4 = f2 + (rect3.top * f6);
            f5 = f3 + (rect3.left * f6);
        } else {
            f4 = f2 - (rect3.top * f6);
            f5 = f3 - (rect3.left * f6);
        }
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setCrop(surfaceControl, this.mTmpDestinationRect).setPosition(surfaceControl, f4, f5).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, this.mShadowRadius);
        return newPipSurfaceTransaction(f4, f5, this.mTmpFloat9, f, scaledCornerRadius, this.mShadowRadius, this.mTmpDestinationRect);
    }

    private float getScaledCornerRadius(Rect rect, Rect rect2) {
        return this.mCornerRadius * ((float) (Math.hypot(rect.width(), rect.height()) / Math.hypot(rect2.width(), rect2.height())));
    }

    private static PictureInPictureSurfaceTransaction newPipSurfaceTransaction(float f, float f2, float[] fArr, float f3, float f4, float f5, Rect rect) {
        return new PictureInPictureSurfaceTransaction.Builder().setPosition(f, f2).setTransform(fArr, f3).setCornerRadius(f4).setShadowRadius(f5).setWindowCrop(rect).build();
    }

    public static SurfaceControl.Transaction newSurfaceControlTransaction() {
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.setFrameTimelineVsync(Choreographer.getSfInstance().getVsyncId());
        return transaction;
    }
}
