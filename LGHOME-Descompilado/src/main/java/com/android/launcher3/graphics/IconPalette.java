package com.android.launcher3.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class IconPalette {
    private static final boolean DEBUG = false;
    private static final float MIN_PRELOAD_COLOR_LIGHTNESS = 0.6f;
    private static final float MIN_PRELOAD_COLOR_SATURATION = 0.2f;
    private static final String TAG = "IconPalette";
    private static IconPalette sBadgePalette;
    private static IconPalette sFolderBadgePalette;
    public final int backgroundColor;
    public final ColorMatrixColorFilter backgroundColorMatrixFilter;
    public final int dominantColor;
    public final ColorMatrixColorFilter saturatedBackgroundColorMatrixFilter;
    public final int secondaryColor;
    public final int textColor;

    private IconPalette(int color, boolean desaturateBackground) {
        this.dominantColor = color;
        int mutedColor = desaturateBackground ? getMutedColor(color, 0.87f) : color;
        this.backgroundColor = mutedColor;
        ColorMatrix colorMatrix = new ColorMatrix();
        Themes.setColorScaleOnMatrix(mutedColor, colorMatrix);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        this.backgroundColorMatrixFilter = colorMatrixColorFilter;
        if (!desaturateBackground) {
            this.saturatedBackgroundColorMatrixFilter = colorMatrixColorFilter;
        } else {
            Themes.setColorScaleOnMatrix(getMutedColor(color, 0.54f), colorMatrix);
            this.saturatedBackgroundColorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        }
        this.textColor = getTextColorForBackground(mutedColor);
        this.secondaryColor = getLowContrastColor(mutedColor);
    }

    public int getPreloadProgressColor(Context context) {
        float[] fArr = new float[3];
        Color.colorToHSV(this.dominantColor, fArr);
        if (fArr[1] < 0.2f) {
            return Themes.getColorAccent(context);
        }
        fArr[2] = Math.max(MIN_PRELOAD_COLOR_LIGHTNESS, fArr[2]);
        return Color.HSVToColor(fArr);
    }

    public static IconPalette fromDominantColor(int dominantColor, boolean desaturateBackground) {
        return new IconPalette(dominantColor, desaturateBackground);
    }

    public static IconPalette getBadgePalette(Resources resources) {
        int color = resources.getColor(R.color.badge_color);
        if (color == 0) {
            return null;
        }
        if (sBadgePalette == null) {
            sBadgePalette = fromDominantColor(color, false);
        }
        return sBadgePalette;
    }

    public static IconPalette getFolderBadgePalette(Resources resources) {
        if (sFolderBadgePalette == null) {
            sFolderBadgePalette = fromDominantColor(resources.getColor(R.color.folder_badge_color), false);
        }
        return sFolderBadgePalette;
    }

    public static int resolveContrastColor(Context context, int color, int background) {
        return ensureTextContrast(resolveColor(context, color), background);
    }

    private static int resolveColor(Context context, int color) {
        return color == 0 ? context.getColor(R.color.notification_icon_default_color) : color;
    }

    private static String contrastChange(int colorOld, int colorNew, int bg) {
        return String.format("from %.2f:1 to %.2f:1", Double.valueOf(ColorUtils.calculateContrast(colorOld, bg)), Double.valueOf(ColorUtils.calculateContrast(colorNew, bg)));
    }

    private static int ensureTextContrast(int color, int bg) {
        return findContrastColor(color, bg, true, 4.5d);
    }

    private static int findContrastColor(int color, int other, boolean findFg, double minRatio) {
        int iLABToColor = findFg ? color : other;
        int iLABToColor2 = findFg ? other : color;
        if (ColorUtils.calculateContrast(iLABToColor, iLABToColor2) >= minRatio) {
            return color;
        }
        double[] dArr = new double[3];
        ColorUtils.colorToLAB(findFg ? iLABToColor : iLABToColor2, dArr);
        double d = 0.0d;
        double d2 = dArr[0];
        double d3 = dArr[1];
        double d4 = dArr[2];
        for (int i = 0; i < 15 && d2 - d > 1.0E-5d; i++) {
            double d5 = (d + d2) / 2.0d;
            if (findFg) {
                iLABToColor = ColorUtils.LABToColor(d5, d3, d4);
            } else {
                iLABToColor2 = ColorUtils.LABToColor(d5, d3, d4);
            }
            if (ColorUtils.calculateContrast(iLABToColor, iLABToColor2) > minRatio) {
                d = d5;
            } else {
                d2 = d5;
            }
        }
        return ColorUtils.LABToColor(d, d3, d4);
    }

    private static int getMutedColor(int color, float whiteScrimAlpha) {
        return ColorUtils.compositeColors(ColorUtils.setAlphaComponent(-1, (int) (whiteScrimAlpha * 255.0f)), color);
    }

    private static int getTextColorForBackground(int backgroundColor) {
        return getLighterOrDarkerVersionOfColor(backgroundColor, 4.5f);
    }

    private static int getLowContrastColor(int color) {
        return getLighterOrDarkerVersionOfColor(color, 1.5f);
    }

    private static int getLighterOrDarkerVersionOfColor(int color, float contrastRatio) {
        int alphaComponent = -1;
        int iCalculateMinimumAlpha = ColorUtils.calculateMinimumAlpha(-1, color, contrastRatio);
        int iCalculateMinimumAlpha2 = ColorUtils.calculateMinimumAlpha(ViewCompat.MEASURED_STATE_MASK, color, contrastRatio);
        if (iCalculateMinimumAlpha >= 0) {
            alphaComponent = ColorUtils.setAlphaComponent(-1, iCalculateMinimumAlpha);
        } else if (iCalculateMinimumAlpha2 >= 0) {
            alphaComponent = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, iCalculateMinimumAlpha2);
        }
        return ColorUtils.compositeColors(alphaComponent, color);
    }
}
