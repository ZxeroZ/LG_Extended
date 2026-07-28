package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;
import com.lge.launcher3.screeneffect.interpolator.ScreenEffectInterpolatorSpring;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.MathFunctionUtils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class OverscrollScreenEffectSpring extends OverscrollScreenEffectBase {
    private static final int PRECISION_FLOAT_DIGITS = 3;
    private static final int SPLIT_WIDTH = 100;
    private int mSplitNum;
    private Rect[] mSplitRect;
    private float[] mSplitScaleX;
    private float[] mSplitTranslateX;

    private void showClipChildRectsForDebug(Canvas canvas, int split) {
    }

    public OverscrollScreenEffectSpring(Context context) {
        super(context, new ScreenEffectInterpolatorSpring());
        this.mSplitNum = 0;
        this.mSplitRect = null;
        this.mSplitScaleX = null;
        this.mSplitTranslateX = null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r11v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.lge.launcher3.screeneffect.OverscrollScreenEffectBase
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        drawInit(child);
        boolean zDrawChild = true;
        for (int i = 0; i < this.mSplitNum; i++) {
            Matrix pageTransformationMatrix = getPageTransformationMatrix(child, i);
            canvas.save();
            canvas.concat(pageTransformationMatrix);
            IScreenEffectable iScreenEffectable = (IScreenEffectable) child;
            if (iScreenEffectable.getShortcutAndWidgetLayer() == 1) {
                Bitmap childrenDrawingCache = iScreenEffectable.getChildrenDrawingCache(true);
                Rect[] rectArr = this.mSplitRect;
                zDrawChild = drawChild(canvas, childrenDrawingCache, rectArr[i], rectArr[i], null);
            } else {
                canvas.clipRect(this.mSplitRect[i]);
                zDrawChild = superDrawChild(canvas, child, drawingTime);
            }
            showClipChildRectsForDebug(canvas, i);
            canvas.restore();
        }
        return zDrawChild;
    }

    private void drawInit(View child) {
        int i;
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        ScreenEffectTargetManager.TargetInfo targetInfo = screenEffectTargetManager.getTargetInfo(child);
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        int scrollForPage = ((int) screenEffectTargetManager.getParentPivot(child, this.mPivot).x) + screenEffectTargetManager.getScrollForPage(screenEffectTargetManager.indexOfChild(child));
        int top = child.getTop();
        int measuredHeight = child.getMeasuredHeight() + top;
        int measuredWidth = child.getMeasuredWidth();
        initializeTrasitionComponents(measuredWidth);
        this.mSplitNum = measuredWidth / 100;
        if (this.mSplitRect.length == 0) {
            LGLog.i("OverscrollScreenEffectSpring", "mSplitRect.length is zero.");
            return;
        }
        int i2 = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        int i3 = 0;
        if (i2 == 1) {
            while (true) {
                i = this.mSplitNum;
                if (i3 >= i) {
                    break;
                }
                int i4 = i3 + 1;
                this.mSplitRect[i3].set((i3 * 100) + scrollForPage, top, (i4 * 100) + scrollForPage, measuredHeight);
                i3 = i4;
            }
            this.mSplitRect[i - 1].right = scrollForPage + measuredWidth;
        } else {
            if (i2 != 2) {
                return;
            }
            int i5 = measuredWidth % 100;
            int i6 = 0;
            while (i6 < this.mSplitNum) {
                int i7 = i6 + 1;
                this.mSplitRect[i6].set((i6 * 100) + scrollForPage + i5, top, (i7 * 100) + scrollForPage + i5, measuredHeight);
                i6 = i7;
            }
            this.mSplitRect[0].left = scrollForPage;
        }
        computeTrasitionComponents(targetInfo);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.OverscrollScreenEffectSpring$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private void initializeTrasitionComponents(int childWidth) {
        int iCeil = (int) Math.ceil(childWidth / 100.0f);
        Rect[] rectArr = this.mSplitRect;
        boolean z = rectArr != null && rectArr.length < iCeil;
        if (rectArr == null || z) {
            this.mSplitRect = new Rect[iCeil];
            for (int i = 0; i < iCeil; i++) {
                this.mSplitRect[i] = new Rect();
            }
        }
        for (Rect rect : this.mSplitRect) {
            rect.setEmpty();
        }
        if (this.mSplitScaleX == null || z) {
            this.mSplitScaleX = new float[iCeil];
        }
        Arrays.fill(this.mSplitScaleX, 0.0f);
        if (this.mSplitTranslateX == null || z) {
            this.mSplitTranslateX = new float[iCeil];
        }
        Arrays.fill(this.mSplitTranslateX, 0.0f);
    }

    private void computeTrasitionComponents(ScreenEffectTargetManager.TargetInfo targetInfo) {
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        float pageScale = getPageScale(targetInfo);
        float f = 0.0f;
        for (int i = 0; i < this.mSplitNum; i++) {
            int i2 = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
            if (i2 == 1) {
                this.mSplitScaleX[i] = MathFunctionUtils.round((((this.mSplitNum - i) - 1) * pageScale) + 1.0f, 3);
                this.mSplitTranslateX[i] = MathFunctionUtils.round(f, 3);
                f += this.mSplitScaleX[i] * 100.0f;
            } else {
                if (i2 != 2) {
                    return;
                }
                int i3 = (this.mSplitNum - i) - 1;
                this.mSplitScaleX[i3] = MathFunctionUtils.round((((r3 - i) - 1) * pageScale) + 1.0f, 3);
                this.mSplitTranslateX[i3] = MathFunctionUtils.round(f, 3);
                f -= this.mSplitScaleX[i3] * 100.0f;
            }
        }
    }

    private float getPageScale(ScreenEffectTargetManager.TargetInfo targetInfo) {
        float f;
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        ScreenEffectConst.FixedOverscrollState fixedOverscrollState = targetInfo.fixedOverscrollState;
        float f2 = targetInfo.scrollProgress;
        if (whichPageToDraw == ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT) {
            if (fixedOverscrollState == ScreenEffectConst.FixedOverscrollState.INNER) {
                f2 = 1.0f - f2;
                f = f2 + 1.0f;
            } else {
                f = fixedOverscrollState == ScreenEffectConst.FixedOverscrollState.OUTER ? 1.0f - f2 : 1.0f;
            }
        } else {
            if (whichPageToDraw == ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT) {
                if (fixedOverscrollState != ScreenEffectConst.FixedOverscrollState.INNER) {
                    if (fixedOverscrollState == ScreenEffectConst.FixedOverscrollState.OUTER) {
                        f2 = 1.0f - f2;
                    }
                }
                f = f2 + 1.0f;
            }
        }
        return MathFunctionUtils.getDiffInSumOfArithSeq(this.mSplitNum, 1.0f, f);
    }

    public Matrix getPageTransformationMatrix(View child, int split) {
        float f;
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        ScreenEffectTargetManager.TargetInfo targetInfo = screenEffectTargetManager.getTargetInfo(child);
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = targetInfo.whichPageToDraw;
        int i = targetInfo.scrollX;
        int scrollForPage = screenEffectTargetManager.getScrollForPage(screenEffectTargetManager.indexOfChild(child));
        float measuredWidth = child.getMeasuredWidth();
        PointF parentPivot = screenEffectTargetManager.getParentPivot(child, this.mPivot);
        float f2 = parentPivot.x;
        float measuredHeight = parentPivot.y + (child.getMeasuredHeight() * 0.5f);
        float f3 = (i - scrollForPage) + this.mSplitTranslateX[split];
        int i2 = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i2 == 1) {
            f = this.mSplitRect[split].left;
        } else if (i2 == 2) {
            f = this.mSplitRect[split].right;
            f2 += scrollForPage;
            f3 += measuredWidth;
        } else {
            return IDENTITY_MATRIX;
        }
        this.mPageMatrix.reset();
        this.mPageMatrix.postTranslate(-f, -measuredHeight);
        this.mPageMatrix.postScale(this.mSplitScaleX[split], 1.0f);
        this.mPageMatrix.postTranslate(f2, measuredHeight);
        this.mPageMatrix.postTranslate(f3, 0.0f);
        return this.mPageMatrix;
    }
}
