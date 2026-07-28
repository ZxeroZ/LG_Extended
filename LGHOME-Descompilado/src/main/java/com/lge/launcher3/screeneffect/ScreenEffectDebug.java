package com.lge.launcher3.screeneffect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import com.lge.launcher3.util.PaintUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectDebug {
    public static final Paint PAINT = new Paint();
    public static final boolean SHOW_CHILD_BOUNDS = false;
    public static final boolean SHOW_CLIP_CHILD_RECTS = false;

    public static final void drawChildBounds(Canvas canvas, View child, int color, int stroke) {
        int left = child.getLeft();
        canvas.drawRect(left, child.getTop(), child.getWidth() + left, child.getHeight() + r1, PaintUtils.getStrokePaint(color, stroke, 255, PAINT));
    }
}
