package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor;

/* JADX INFO: loaded from: classes.dex */
public class GrayscaleSegment extends LightnessSegment {
    private static final int GRAYSCALE_LIGHTNESS_MAX = 95;
    private static final int GRAYSCALE_LIGHTNESS_MIN = 10;
    private static final int GRAYSCALE_SATURATION_MAX = 20;
    private static final int GRAYSCALE_SATURATION_MIN = 0;

    public GrayscaleSegment(String name) {
        super(name);
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.LightnessSegment, com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    public boolean inRange(int[] hsl) {
        return hsl[2] >= 10 && hsl[2] < 95 && hsl[1] >= 0 && hsl[1] < 20;
    }
}
