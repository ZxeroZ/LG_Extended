package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;
import com.lge.launcher3.util.OrientationUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectBreeze extends ScreenEffectBase {
    private static final float FINAL_PORT_ALPHA_SCROLLRATIO = 1.0f;
    private static final int FINAL_PORT_ALPHA_VALUE = 127;
    private static final float FINAL_ROTATE_Y = 60.0f;
    private static final float FINAL_SCALE = 0.5f;
    private static final float FIXED_PAGE_GAP_WIDTH_RATIO = 0.075f;
    private static final float ROTATE_Y_POWER_FACTOR = 2.0f;
    private static final float START_PORT_ALPHA_SCROLLRATIO = 0.5f;
    private static final int START_PORT_ALPHA_VALUE = 255;

    public ScreenEffectBreeze(Context context) {
        super(context);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.lge.launcher3.screeneffect.ScreenEffectBase
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean zDrawChild;
        Matrix pageTransformationMatrix = getPageTransformationMatrix(child, ScreenEffectTargetManager.getInstance(this.mContext));
        Paint progressivePagePaint = getProgressivePagePaint(child);
        canvas.save();
        canvas.concat(pageTransformationMatrix);
        IScreenEffectable iScreenEffectable = (IScreenEffectable) child;
        if (iScreenEffectable.getShortcutAndWidgetLayer() == 1) {
            zDrawChild = drawChild(canvas, iScreenEffectable.getChildrenDrawingCache(true), progressivePagePaint);
        } else {
            if (OrientationUtils.isPortrait(this.mContext)) {
                iScreenEffectable.setShortcutAndWidgetAlpha(progressivePagePaint.getAlpha() / 255.0f);
            }
            zDrawChild = superDrawChild(canvas, child, drawingTime);
        }
        canvas.restore();
        return zDrawChild;
    }

    protected Matrix getPageTransformationMatrix(View child, ScreenEffectTargetManager targetMngr) {
        float fPow;
        float f;
        float scrollForPage = targetMngr.getScrollForPage(targetMngr.indexOfChild(child));
        float measuredWidth = child.getMeasuredWidth();
        float measuredHeight = child.getMeasuredHeight();
        PointF parentPivot = targetMngr.getParentPivot(child, this.mPivot);
        parentPivot.y += measuredHeight * 0.5f;
        ScreenEffectTargetManager.TargetInfo targetInfo = targetMngr.getTargetInfo(child);
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        float f2 = targetInfo.scrollProgress;
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i == 1) {
            parentPivot.x += scrollForPage + measuredWidth;
            fPow = ((float) Math.pow(f2, 2.0d)) * (-1.0f) * FINAL_ROTATE_Y;
            f = 1.0f - (0.5f * f2);
        } else if (i == 2) {
            parentPivot.x += scrollForPage;
            float f3 = 1.0f - f2;
            fPow = ((float) Math.pow(f3, 2.0d)) * FINAL_ROTATE_Y;
            f = 1.0f - (f3 * 0.5f);
        } else {
            return IDENTITY_MATRIX;
        }
        this.mPageMatrix.reset();
        this.mCamera.save();
        this.mCamera.rotateY(fPow);
        this.mCamera.getMatrix(this.mPageMatrix);
        this.mCamera.restore();
        this.mPageMatrix.preScale(f, f);
        this.mPageMatrix.preTranslate(-parentPivot.x, -parentPivot.y);
        this.mPageMatrix.postTranslate(parentPivot.x, parentPivot.y);
        this.mPageMatrix.postTranslate(ScreenEffectUtils.getFixedChlidGap(whichPageToDraw, f2, measuredWidth, FIXED_PAGE_GAP_WIDTH_RATIO), 0.0f);
        return this.mPageMatrix;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectBreeze$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    protected Paint getProgressivePagePaint(View child) {
        this.mPagePaint.reset();
        if (!OrientationUtils.isPortrait(this.mContext)) {
            return this.mPagePaint;
        }
        ScreenEffectTargetManager.TargetInfo targetInfo = ScreenEffectTargetManager.getInstance(this.mContext).getTargetInfo(child);
        this.mPagePaint.setAlpha(ScreenEffectUtils.getProgressivePageAlpha(targetInfo.scrollDirection, targetInfo.whichPageToDraw, targetInfo.scrollProgress, 0.5f, 1.0f, 255, FINAL_PORT_ALPHA_VALUE));
        return this.mPagePaint;
    }
}
