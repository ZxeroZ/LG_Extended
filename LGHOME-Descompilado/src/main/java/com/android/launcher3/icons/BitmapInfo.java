package com.android.launcher3.icons;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class BitmapInfo {
    public static final Bitmap LOW_RES_ICON;
    public static final BitmapInfo LOW_RES_INFO;
    public final int color;
    public final Bitmap icon;

    static {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        LOW_RES_ICON = bitmapCreateBitmap;
        LOW_RES_INFO = fromBitmap(bitmapCreateBitmap);
    }

    public BitmapInfo(Bitmap icon, int color) {
        this.icon = icon;
        this.color = color;
    }

    public final boolean isNullOrLowRes() {
        Bitmap bitmap = this.icon;
        return bitmap == null || bitmap == LOW_RES_ICON;
    }

    public final boolean isLowRes() {
        return LOW_RES_ICON == this.icon;
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap) {
        return of(bitmap, 0);
    }

    public static BitmapInfo of(Bitmap bitmap, int color) {
        return new BitmapInfo(bitmap, color);
    }

    public interface Extender {
        default void prepareToDrawOnUi() {
        }

        default BitmapInfo getExtendedInfo(Bitmap bitmap, int color, BaseIconFactory iconFactory) {
            return BitmapInfo.of(bitmap, color);
        }
    }
}
