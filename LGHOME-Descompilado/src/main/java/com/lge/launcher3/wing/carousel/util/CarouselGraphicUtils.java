package com.lge.launcher3.wing.carousel.util;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes2.dex */
public class CarouselGraphicUtils {
    private static final boolean DEBUG = false;
    private static final String TAG = "CarouselGraphicUtils";
    private static final int mReflectionGap = 22;
    private static final float mReflectionHeight = 0.55f;
    private static final int mReflectionOpacity = 125;
    private static final Matrix mReflectionMatrix = new Matrix();
    private static final PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private static final Canvas mReflectionCanvas = new Canvas();

    public static int getReflectionGap() {
        return 22;
    }

    public static Bitmap addShadow(final Bitmap bm, final int dstHeight, final int dstWidth, int color, int size, float dx, float dy) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0.0f, 0.0f, bm.getWidth(), bm.getHeight()), new RectF(0.0f, 0.0f, dstWidth - dx, dstHeight - dy), Matrix.ScaleToFit.CENTER);
        Matrix matrix2 = new Matrix(matrix);
        matrix2.postTranslate(dx, dy);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint(1);
        canvas.drawBitmap(bm, matrix, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(bm, matrix2, paint);
        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setAlpha(120);
        paint.setColor(color);
        paint.setMaskFilter(blurMaskFilter);
        paint.setFilterBitmap(true);
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmapCreateBitmap2);
        canvas2.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, paint);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas2.drawBitmap(bm, matrix, paint);
        bitmapCreateBitmap.recycle();
        return bitmapCreateBitmap2;
    }

    public static int getAppIconBGColor(Drawable drawable, boolean isTheme) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = drawable instanceof FastBitmapDrawable ? ((FastBitmapDrawable) drawable).getBitmap() : null;
        }
        if (bitmap == null) {
            return 0;
        }
        int iFindDominantColorByHue = Utilities.findDominantColorByHue(bitmap, 20, -1);
        return isTheme ? iFindDominantColorByHue & Integer.MAX_VALUE : iFindDominantColorByHue;
    }

    public static Bitmap createReflectionBitmap(Bitmap original) {
        Matrix matrix = mReflectionMatrix;
        matrix.setScale(1.0f, -1.0f);
        int width = original.getWidth();
        int height = original.getHeight();
        int i = (int) (height * mReflectionHeight);
        int iArgb = Color.argb(mReflectionOpacity, 255, 255, 255);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(original, 0, height - i, width, i, matrix, false);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, bitmapCreateBitmap.getHeight(), iArgb, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(linearGradient);
        paint.setXfermode(mXfermode);
        Canvas canvas = mReflectionCanvas;
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.drawRect(0.0f, 0.0f, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight(), paint);
        return bitmapCreateBitmap;
    }
}
