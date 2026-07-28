package com.lge.launcher3.util;

import android.graphics.Paint;

/* JADX INFO: loaded from: classes.dex */
public class PaintUtils {
    public static Paint getStrokePaint(int color, float strokeWidth, int alpha, Paint recyle) {
        if (recyle == null) {
            recyle = new Paint();
        }
        recyle.setAntiAlias(true);
        recyle.setStyle(Paint.Style.STROKE);
        recyle.setStrokeWidth(strokeWidth);
        recyle.setColor(color);
        recyle.setAlpha(alpha);
        return recyle;
    }
}
