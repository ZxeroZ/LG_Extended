package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class FloatingTaskThumbnailView extends View {
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private final Matrix mMatrix;
    private final Paint mPaint;

    public FloatingTaskThumbnailView(Context context) {
        this(context, null);
    }

    public FloatingTaskThumbnailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingTaskThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint = new Paint(1);
        this.mMatrix = new Matrix();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mBitmap == null) {
            return;
        }
        float measuredWidth = (getMeasuredWidth() * 1.0f) / this.mBitmap.getWidth();
        this.mMatrix.reset();
        this.mMatrix.postScale(measuredWidth, measuredWidth);
        this.mBitmapShader.setLocalMatrix(this.mMatrix);
        ((FloatingTaskView) getParent()).drawRoundedRect(canvas, this.mPaint);
    }

    public void setThumbnail(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (bitmap != null) {
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mBitmapShader = bitmapShader;
            this.mPaint.setShader(bitmapShader);
        }
    }
}
