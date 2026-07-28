package com.lge.launcher3.badge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class BadgeUtils {
    private static BitmapDrawable sShortcutBadgeDrawable;

    public enum LocationType {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        TOP
    }

    public static boolean isChanged(BitmapDrawable drawable, boolean visible) {
        return (visible && drawable == null) || (!visible && drawable != null);
    }

    public static BitmapDrawable createShortcutBadgeDrawable(Context context) {
        BitmapDrawable bitmapDrawable = sShortcutBadgeDrawable;
        if (bitmapDrawable != null) {
            return bitmapDrawable;
        }
        Resources resources = context.getResources();
        BitmapDrawable bitmapDrawable2 = (BitmapDrawable) resources.getDrawable(R.drawable.ic_homescreen_shortcut_badge, null);
        if (bitmapDrawable2 == null) {
            return null;
        }
        int dimension = (int) resources.getDimension(R.dimen.appnotifier_badge_size);
        int dimension2 = (int) resources.getDimension(R.dimen.appnotifier_badge_margin_left);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        bitmapDrawable2.setFilterBitmap(true);
        bitmapDrawable2.setDither(true);
        bitmapDrawable2.setBounds(dimension2, 0, dimension + dimension2, dimension);
        bitmapDrawable2.draw(canvas);
        BitmapDrawable bitmapDrawable3 = new BitmapDrawable(resources, bitmapCreateBitmap);
        sShortcutBadgeDrawable = bitmapDrawable3;
        return bitmapDrawable3;
    }

    public static void initShortcutBadge() {
        sShortcutBadgeDrawable = null;
    }
}
