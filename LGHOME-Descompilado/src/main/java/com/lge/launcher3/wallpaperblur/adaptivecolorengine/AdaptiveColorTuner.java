package com.lge.launcher3.wallpaperblur.adaptivecolorengine;

import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil.ColorInfo;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs.Logs;

/* JADX INFO: loaded from: classes.dex */
public class AdaptiveColorTuner {
    public static final int FIRST = 0;
    public static final int HUE = 0;
    private static final int HUE_RANGE_MAX = 360;
    public static final int LIGHTNESS = 2;
    private static final int LIGHTNESS_RANGE_MAX = 100;
    private static final int LIGHTNESS_RANGE_MIN = 0;
    private static final int MIN_HUE_DISTANCE = 30;
    private static final int OPACITY_RANGE_MAX = 255;
    public static final int SATURATION = 1;
    private static final int SATURATION_RANGE_MAX = 100;
    private static final int SATURATION_RANGE_MIN = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    private static final int TO_MAX = -255;

    private static int get2ByteOpacity(int opacity) {
        return (int) ((opacity * 2.55f) + 0.5f);
    }

    public static void getAdaptedColor(ColorInfo src, ColorInfo dest, AdaptiveColorParam p) {
        Logs.aassert(src.isValid(), "The source color is not valid!");
        int[] iArr = {src.getHue(), src.getSaturation(), src.getLightness()};
        getAdaptedHSL(p, 0, iArr);
        dest.setHSL(iArr);
        dest.setAlpha(getAdaptedAlpha(p.opacity[0]));
    }

    public static int get2ndAdaptedColor(ColorInfo[] srcs, ColorInfo dest, AdaptiveColorParam p) {
        int[] iArr = {0, 0, 0};
        int i = get2ndSource(srcs, iArr);
        getAdaptedHSL(p, 1, iArr);
        dest.setHSL(iArr);
        dest.setAlpha(getAdaptedAlpha(p.opacity[1]));
        return i;
    }

    public static void get3rdAdaptedColor(ColorInfo src, ColorInfo dest, AdaptiveColorParam p) {
        Logs.aassert(src.isValid(), "The source color is not valid!");
        int[] iArr = {src.getHue(), src.getSaturation(), src.getLightness()};
        getAdaptedHSL(p, 2, iArr);
        dest.setHSL(iArr);
        dest.setAlpha(getAdaptedAlpha(p.opacity[2]));
    }

    private static void getAdaptedHSL(AdaptiveColorParam p, int idx, int[] hsl) {
        hsl[1] = getAdaptedValue(hsl[1], p.satStep, p.satCoef[idx], 0, 100);
        hsl[2] = getAdaptedValue(hsl[2], p.litStep, p.litCoef[idx], 0, 100);
    }

    private static int getAdaptedValue(int current, int[] steps, int[] coef, int min, int max) {
        int i = 0;
        while (i < steps.length && current >= steps[i]) {
            i++;
        }
        return truncate(coef[i] == -255 ? max : current + coef[i], min, max);
    }

    private static int truncate(int min, int max, int v) {
        return Math.max(Math.min(v, max), min);
    }

    private static int getAdaptedAlpha(int current) {
        if (current == -255) {
            return 255;
        }
        return get2ByteOpacity(current);
    }

    private static int get2ndSource(ColorInfo[] srcs, int[] hsl) {
        Logs.aassert(srcs[0].isValid(), "The first color is invalid!");
        int i = 1;
        while (i < srcs.length && srcs[i].isValid() && !isNotSimularColor(srcs[i].getHue(), srcs[0].getHue())) {
            i++;
        }
        if (i < srcs.length && srcs[i].isValid()) {
            hsl[0] = srcs[i].getHue();
            hsl[1] = srcs[i].getSaturation();
            hsl[2] = srcs[i].getLightness();
            return i;
        }
        hsl[0] = srcs[0].getHue();
        hsl[0] = hsl[0] + 30;
        hsl[0] = hsl[0] % HUE_RANGE_MAX;
        hsl[1] = srcs[0].getSaturation();
        hsl[2] = srcs[0].getLightness();
        return 0;
    }

    private static boolean isNotSimularColor(int hue1, int hue2) {
        int iMax = Math.max(hue1, hue2);
        int iMin = Math.min(hue1, hue2);
        return iMax - iMin > 30 || (iMin + HUE_RANGE_MAX) - iMax <= 30;
    }
}
