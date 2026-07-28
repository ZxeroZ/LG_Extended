package com.lge.launcher3.wallpaperpicker.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class LGWallpaperSwitchUtil {
    public static void changeWallpaper(Context context, Bitmap bitmap) {
        WallpaperManager wallpaperManager;
        if (context == null || bitmap == null || (wallpaperManager = WallpaperManager.getInstance(context)) == null) {
            return;
        }
        try {
            wallpaperManager.setBitmap(bitmap, null, true, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeWallpaper(Context context, int resId) {
        WallpaperManager wallpaperManager;
        if (context == null || (wallpaperManager = WallpaperManager.getInstance(context)) == null) {
            return;
        }
        try {
            wallpaperManager.setResource(resId, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getDensityDpi(Context context) {
        if (context == null) {
            return new DisplayMetrics().densityDpi;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }
}
