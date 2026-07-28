package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public interface BitmapRenderer {
    public static final boolean USE_HARDWARE_BITMAP;

    void draw(Canvas out);

    static {
        USE_HARDWARE_BITMAP = Build.VERSION.SDK_INT >= 28;
    }

    static Bitmap createSoftwareBitmap(int width, int height, BitmapRenderer renderer) {
        GraphicsUtils.noteNewBitmapCreated();
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        renderer.draw(new Canvas(bitmapCreateBitmap));
        return bitmapCreateBitmap;
    }

    static Bitmap createHardwareBitmap(int width, int height, BitmapRenderer renderer) {
        if (!USE_HARDWARE_BITMAP) {
            return createSoftwareBitmap(width, height, renderer);
        }
        GraphicsUtils.noteNewBitmapCreated();
        Picture picture = new Picture();
        renderer.draw(picture.beginRecording(width, height));
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }

    static Bitmap createBitmap(final Bitmap source, final int x, final int y, final int width, final int height) {
        if (Build.VERSION.SDK_INT >= 26 && source.getConfig() == Bitmap.Config.HARDWARE) {
            return createHardwareBitmap(width, height, new BitmapRenderer() { // from class: com.android.launcher3.icons.-$$Lambda$BitmapRenderer$peolrnXWM9Hj9bdyKUF3mzOUlXA
                @Override // com.android.launcher3.icons.BitmapRenderer
                public final void draw(Canvas canvas) {
                    Bitmap bitmap = source;
                    int i = x;
                    int i2 = y;
                    int i3 = width;
                    int i4 = height;
                    canvas.drawBitmap(bitmap, new Rect(i, i2, i + i3, i2 + i4), new RectF(0.0f, 0.0f, i3, i4), (Paint) null);
                }
            });
        }
        GraphicsUtils.noteNewBitmapCreated();
        return Bitmap.createBitmap(source, x, y, width, height);
    }
}
