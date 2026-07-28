package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.Log;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class GraphicsUtils {
    private static final String TAG = "GraphicsUtils";
    public static Runnable sOnNewBitmapRunnable = new Runnable() { // from class: com.android.launcher3.icons.-$$Lambda$GraphicsUtils$nWC-qLw6D-l8ASiZoBWOYgQdw-0
        @Override // java.lang.Runnable
        public final void run() {
            GraphicsUtils.lambda$static$0();
        }
    };

    static /* synthetic */ void lambda$static$0() {
    }

    public static int setColorAlphaBound(int color, int alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        return (color & ViewCompat.MEASURED_SIZE_MASK) | (alpha << 24);
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException unused) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        }
    }

    public static int getArea(Region r) {
        RegionIterator regionIterator = new RegionIterator(r);
        Rect rect = new Rect();
        int iWidth = 0;
        while (regionIterator.next(rect)) {
            iWidth += rect.width() * rect.height();
        }
        return iWidth;
    }

    public static void noteNewBitmapCreated() {
        sOnNewBitmapRunnable.run();
    }
}
