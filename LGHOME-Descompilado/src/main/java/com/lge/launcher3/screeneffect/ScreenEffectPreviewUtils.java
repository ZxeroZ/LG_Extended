package com.lge.launcher3.screeneffect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;
import com.lge.launcher3.util.PaintUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewUtils {
    private static final float OUTLINE_HIDE_START_SCROLL_RATIO = 0.8f;
    private static final int OUTLINE_MAX_ALPHA = 128;
    private static final Paint OUTLINE_PAINT = PaintUtils.getStrokePaint(2, -3355444.0f, 255, null);
    private static final float OUTLINE_SHOW_FINAL_SCROLL_RATIO = 0.2f;

    public static int getOutlineAlpha(ScreenEffectTargetManager.TargetInfo targetInfo) {
        float f;
        int i;
        ScreenEffectConst.ScrollDirection scrollDirection = targetInfo.scrollDirection;
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT;
        float f2 = targetInfo.scrollProgress;
        float f3 = 0.2f;
        int i2 = 128;
        if (f2 <= 0.2f) {
            f = 0.0f;
            i = 1;
        } else {
            if (f2 < 0.8f) {
                return 128;
            }
            f3 = 1.0f;
            f = 0.8f;
            i = 128;
            i2 = 1;
        }
        return ScreenEffectUtils.getProgressivePageAlpha(scrollDirection, whichPageToDraw, f2, f, f3, i, i2);
    }

    public static final void drawOutline(Canvas canvas, View view, int alpha, Rect rectToClip) {
        if (rectToClip != null) {
            canvas.save();
            canvas.clipRect(rectToClip);
        }
        Paint paint = OUTLINE_PAINT;
        paint.setAlpha(alpha);
        canvas.drawRect(0.0f, 0.0f, view.getWidth() - (paint.getStrokeWidth() / 2.0f), view.getHeight(), paint);
        if (rectToClip != null) {
            canvas.restore();
        }
    }

    public static final void drawBorderLine(Canvas canvas, View view, int alpha, Rect rectToClip, boolean borderLeft, boolean borderTop, boolean borderRight, boolean borderBottom) {
        if (rectToClip != null) {
            canvas.save();
            canvas.clipRect(rectToClip);
        }
        Paint paint = OUTLINE_PAINT;
        paint.setAlpha(alpha);
        int width = (int) (view.getWidth() - (paint.getStrokeWidth() * 0.5f));
        int height = view.getHeight();
        if (borderLeft) {
            canvas.drawLine(0.0f, 0.0f, 0.0f, height, paint);
        }
        if (borderTop) {
            canvas.drawLine(0.0f, 0.0f, width, 0.0f, paint);
        }
        if (borderRight) {
            float f = width;
            canvas.drawLine(f, 0.0f, f, height, paint);
        }
        if (borderBottom) {
            float f2 = height;
            canvas.drawLine(0.0f, f2, width, f2, paint);
        }
        if (rectToClip != null) {
            canvas.restore();
        }
    }
}
