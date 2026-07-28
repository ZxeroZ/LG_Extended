package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.util.Preconditions;

/* JADX INFO: loaded from: classes.dex */
public class ShadowGenerator {
    public static final int AMBIENT_SHADOW_ALPHA = 30;
    public static final float BLUR_FACTOR = 0.010416667f;
    private static final float HALF_DISTANCE = 0.5f;
    public static final int KEY_SHADOW_ALPHA = 61;
    private static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private static final Object LOCK = new Object();
    private static ShadowGenerator sShadowGenerator;
    private final Paint mBlurPaint;
    private final Canvas mCanvas;
    private final Paint mDrawPaint;
    private final int mIconSize;

    private ShadowGenerator(Context context) {
        int i = LauncherAppState.getIDP(context).iconBitmapSize;
        this.mIconSize = i;
        this.mCanvas = new Canvas();
        Paint paint = new Paint(3);
        this.mBlurPaint = paint;
        paint.setMaskFilter(new BlurMaskFilter(i * 0.010416667f, BlurMaskFilter.Blur.NORMAL));
        this.mDrawPaint = new Paint(3);
    }

    public synchronized Bitmap recreateIcon(Bitmap icon) {
        Bitmap bitmapCreateBitmap;
        Bitmap bitmapExtractAlpha = icon.extractAlpha(this.mBlurPaint, new int[2]);
        int i = this.mIconSize;
        bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(bitmapCreateBitmap);
        this.mDrawPaint.setAlpha(30);
        this.mCanvas.drawBitmap(bitmapExtractAlpha, r0[0], r0[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(61);
        this.mCanvas.drawBitmap(bitmapExtractAlpha, r0[0], r0[1] + (this.mIconSize * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        this.mCanvas.drawBitmap(icon, 0.0f, 0.0f, this.mDrawPaint);
        this.mCanvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    public synchronized Bitmap recreateIcon(Bitmap icon, int width, int height) {
        Bitmap bitmapCreateBitmap;
        Bitmap bitmapExtractAlpha = icon.extractAlpha(this.mBlurPaint, new int[2]);
        bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(bitmapCreateBitmap);
        this.mDrawPaint.setAlpha(30);
        this.mCanvas.drawBitmap(bitmapExtractAlpha, r0[0], r0[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(61);
        this.mCanvas.drawBitmap(bitmapExtractAlpha, r0[0], r0[1] + (height * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        this.mCanvas.drawBitmap(icon, 0.0f, 0.0f, this.mDrawPaint);
        this.mCanvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    public static Bitmap createPillWithShadow(int rectColor, int width, int height) {
        float f = height * 1.0f;
        float f2 = f / 32.0f;
        float f3 = f / 16.0f;
        int i = height / 2;
        Canvas canvas = new Canvas();
        Paint paint = new Paint(3);
        paint.setMaskFilter(new BlurMaskFilter(f2, BlurMaskFilter.Blur.NORMAL));
        int i2 = width / 2;
        float f4 = i;
        int iMax = Math.max(Math.round(i2 + f2), Math.round(f2 + f4 + f3));
        int i3 = iMax * 2;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i3, i3, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmapCreateBitmap);
        int i4 = iMax - i2;
        int i5 = iMax - i;
        int i6 = i2 + iMax;
        int i7 = iMax + i;
        paint.setAlpha(30);
        float f5 = i4;
        float f6 = i5;
        float f7 = i6;
        float f8 = i7;
        canvas.drawRoundRect(f5, f6, f7, f8, f4, f4, paint);
        paint.setAlpha(61);
        canvas.drawRoundRect(f5, f6 + f3, f7, f8 + f3, f4, f4, paint);
        Paint paint2 = new Paint(3);
        paint2.setColor(rectColor);
        canvas.drawRoundRect(f5, f6, f7, f8, f4, f4, paint2);
        return bitmapCreateBitmap;
    }

    public static ShadowGenerator getInstance(Context context) {
        Preconditions.assertNonUiThread();
        synchronized (LOCK) {
            if (sShadowGenerator == null) {
                sShadowGenerator = new ShadowGenerator(context);
            }
        }
        return sShadowGenerator;
    }

    public static void updateShadowGenerator(Context context) {
        synchronized (LOCK) {
            if (sShadowGenerator != null) {
                sShadowGenerator = new ShadowGenerator(context);
            }
        }
    }

    public static float getScaleForBounds(RectF bounds) {
        float fMin = Math.min(Math.min(bounds.left, bounds.right), bounds.top);
        float f = fMin < 0.010416667f ? 0.48958334f / (0.5f - fMin) : 1.0f;
        return bounds.bottom < 0.03125f ? Math.min(f, 0.46875f / (0.5f - bounds.bottom)) : f;
    }
}
