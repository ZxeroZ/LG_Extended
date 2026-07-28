package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/* JADX INFO: loaded from: classes.dex */
public class TintedDrawableSpan extends DynamicDrawableSpan {
    private final Drawable mDrawable;
    private int mOldTint;

    public TintedDrawableSpan(Context context, int resourceId) {
        super(0);
        Drawable drawableMutate = context.getDrawable(resourceId).mutate();
        this.mDrawable = drawableMutate;
        this.mOldTint = 0;
        drawableMutate.setTint(0);
    }

    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm == null) {
            fm = paint.getFontMetricsInt();
        }
        Paint.FontMetricsInt fontMetricsInt = fm;
        int i = fontMetricsInt.bottom - fontMetricsInt.top;
        this.mDrawable.setBounds(0, 0, i, i);
        return super.getSize(paint, text, start, end, fontMetricsInt);
    }

    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color = paint.getColor();
        if (this.mOldTint != color) {
            this.mOldTint = color;
            this.mDrawable.setTint(color);
        }
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
    }

    @Override // android.text.style.DynamicDrawableSpan
    public Drawable getDrawable() {
        return this.mDrawable;
    }
}
