package com.lge.launcher3.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class ColorUtilsExtension {
    private static final float DISABLED_ALPHA = 0.35f;

    public static float getEnableAlpha(boolean enable) {
        if (enable) {
            return 1.0f;
        }
        return DISABLED_ALPHA;
    }

    public static int randomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static ColorStateList getColorStateList(Context context, int colorResId) {
        return ColorStateList.valueOf(context.getResources().getColor(colorResId));
    }
}
