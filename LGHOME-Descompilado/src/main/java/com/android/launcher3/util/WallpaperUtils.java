package com.android.launcher3.util;

import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
public final class WallpaperUtils {
    public static final String WALLPAPER_HEIGHT_KEY = "wallpaper.height";
    public static final float WALLPAPER_SCREENS_SPAN = 2.0f;
    public static final String WALLPAPER_WIDTH_KEY = "wallpaper.width";
    private static Point sDefaultWallpaperSize;

    public static float wallpaperTravelToScreenWidthRatio(int width, int height) {
        return ((width / height) * 0.30769226f) + 1.0076923f;
    }

    public static void suggestWallpaperDimension(Resources res, final SharedPreferences sharedPrefs, WindowManager windowManager, final WallpaperManager wallpaperManager, boolean fallBackToDefaults) {
        Point defaultWallpaperSize = getDefaultWallpaperSize(res, windowManager);
        int i = sharedPrefs.getInt(WALLPAPER_WIDTH_KEY, -1);
        int i2 = sharedPrefs.getInt(WALLPAPER_HEIGHT_KEY, -1);
        if (i == -1 || i2 == -1) {
            if (!fallBackToDefaults) {
                return;
            }
            i = defaultWallpaperSize.x;
            i2 = defaultWallpaperSize.y;
        }
        if (i == wallpaperManager.getDesiredMinimumWidth() && i2 == wallpaperManager.getDesiredMinimumHeight()) {
            return;
        }
        wallpaperManager.suggestDesiredDimensions(i, i2);
    }

    public static Point getDefaultWallpaperSize(Resources res, WindowManager windowManager) {
        int iMax;
        if (sDefaultWallpaperSize == null) {
            Point point = new Point();
            Point point2 = new Point();
            windowManager.getDefaultDisplay().getCurrentSizeRange(point, point2);
            int iMax2 = Math.max(point2.x, point2.y);
            int iMax3 = Math.max(point.x, point.y);
            if (Build.VERSION.SDK_INT >= 17) {
                Point point3 = new Point();
                windowManager.getDefaultDisplay().getRealSize(point3);
                iMax2 = Math.max(point3.x, point3.y);
                iMax3 = Math.min(point3.x, point3.y);
            }
            if (res.getConfiguration().smallestScreenWidthDp >= 720) {
                iMax = (int) (iMax2 * wallpaperTravelToScreenWidthRatio(iMax2, iMax3));
            } else {
                iMax = Math.max((int) (iMax3 * 2.0f), iMax2);
            }
            sDefaultWallpaperSize = new Point(iMax, iMax2);
        }
        return sDefaultWallpaperSize;
    }

    public static void resetDefaultWallpaperSize() {
        sDefaultWallpaperSize = null;
    }
}
