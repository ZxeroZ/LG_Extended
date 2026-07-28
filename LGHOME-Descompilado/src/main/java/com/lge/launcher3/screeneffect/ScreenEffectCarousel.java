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
public class ScreenEffectCarousel extends ScreenEffectBase {
    private static final float FINAL_PORT_ALPHA_SCROLLRATIO = 0.96f;
    private static final float FINAL_ROTATE_Y = 93.0f;
    private static final float OVERSHOOT_TENSION = 2.4f;
    private static final float START_PORT_ALPHA_SCROLLRATIO = 0.86f;

    @Override // com.lge.launcher3.screeneffect.ScreenEffectBase
    public boolean isOverscrollHandledBySelf() {
        return true;
    }

    public ScreenEffectCarousel(Context context) {
        super(context, OVERSHOOT_TENSION);
        scaleCameraLocationZ(2.0f);
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
        float f;
        ScreenEffectTargetManager.TargetInfo targetInfo = targetMngr.getTargetInfo(child);
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        float f2 = targetInfo.scrollProgress;
        int i = targetInfo.scrollX;
        int iIndexOfChild = targetMngr.indexOfChild(child);
        float scrollForPage = targetMngr.getScrollForPage(iIndexOfChild);
        float measuredWidth = child.getMeasuredWidth();
        float measuredHeight = child.getMeasuredHeight();
        PointF parentPivot = targetMngr.getParentPivot(child, this.mPivot);
        float f3 = parentPivot.x;
        if (!sIsRtL) {
            measuredWidth = 0.0f;
        }
        parentPivot.x = f3 + scrollForPage + measuredWidth;
        parentPivot.y += measuredHeight * 0.5f;
        float scrollForPageLoop = i - targetMngr.getScrollForPageLoop(iIndexOfChild);
        int i2 = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i2 == 1 || i2 == 2) {
            f = f2 * (-1.0f) * FINAL_ROTATE_Y;
        } else {
            if (i2 != 3 && i2 != 4) {
                return IDENTITY_MATRIX;
            }
            f = (1.0f - f2) * FINAL_ROTATE_Y;
        }
        this.mPageMatrix.reset();
        this.mCamera.save();
        this.mCamera.rotateY(f);
        this.mCamera.getMatrix(this.mPageMatrix);
        this.mCamera.restore();
        this.mPageMatrix.preTranslate(-parentPivot.x, -parentPivot.y);
        this.mPageMatrix.postTranslate(parentPivot.x, parentPivot.y);
        this.mPageMatrix.postTranslate(scrollForPageLoop, 0.0f);
        return this.mPageMatrix;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectCarousel$1, reason: invalid class name */
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
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 4;
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
        this.mPagePaint.setAlpha(ScreenEffectUtils.getProgressivePageAlpha(ScreenEffectConst.ScrollDirection.TO_RIGHT, targetInfo.whichPageToDraw, targetInfo.scrollProgress, START_PORT_ALPHA_SCROLLRATIO, FINAL_PORT_ALPHA_SCROLLRATIO, 255, 1));
        return this.mPagePaint;
    }
}
