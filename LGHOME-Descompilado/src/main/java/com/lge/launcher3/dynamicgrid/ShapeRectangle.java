package com.lge.launcher3.dynamicgrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ShapeRectangle extends View {
    int mHeight;
    int mStrokeWidth;
    int mWidth;

    public ShapeRectangle(Context context) {
        super(context);
        this.mWidth = 10;
        this.mHeight = 10;
        this.mStrokeWidth = 4;
        setBackgroundColor(0);
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        int color = getResources().getColor(R.color.grid_white_normal, null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(this.mStrokeWidth);
        canvas.drawRect(new Rect(0, 0, this.mWidth, this.mHeight), paint);
        super.onDraw(canvas);
    }
}
