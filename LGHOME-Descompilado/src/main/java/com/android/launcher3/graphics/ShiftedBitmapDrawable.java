package com.android.launcher3.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class ShiftedBitmapDrawable extends Drawable {
    private final Bitmap mBitmap;
    private final Drawable.ConstantState mConstantState;
    private final Paint mPaint = new Paint(2);
    private float mShiftX;
    private float mShiftY;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    public ShiftedBitmapDrawable(Bitmap bitmap, float shiftX, float shiftY) {
        this.mBitmap = bitmap;
        this.mShiftX = shiftX;
        this.mShiftY = shiftY;
        this.mConstantState = new MyConstantState(bitmap, this.mShiftX, this.mShiftY);
    }

    public float getShiftX() {
        return this.mShiftX;
    }

    public float getShiftY() {
        return this.mShiftY;
    }

    public void setShiftX(float shiftX) {
        this.mShiftX = shiftX;
    }

    public void setShiftY(float shiftY) {
        this.mShiftY = shiftY;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, this.mShiftX, this.mShiftY, this.mPaint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mConstantState;
    }

    private static class MyConstantState extends Drawable.ConstantState {
        private final Bitmap mBitmap;
        private float mShiftX;
        private float mShiftY;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        MyConstantState(Bitmap bitmap, float shiftX, float shiftY) {
            this.mBitmap = bitmap;
            this.mShiftX = shiftX;
            this.mShiftY = shiftY;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ShiftedBitmapDrawable(this.mBitmap, this.mShiftX, this.mShiftY);
        }
    }
}
