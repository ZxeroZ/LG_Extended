package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor;

import android.graphics.Color;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil.ColorUtils;

/* JADX INFO: loaded from: classes.dex */
public class LightnessSegment extends ColorSegment {
    private static final int LIGHTNESS_RANGE_MAX = 100;

    public LightnessSegment(String name) {
        super(name);
    }

    public LightnessSegment(String name, int from, int to) {
        super(name, from, to);
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    public boolean inRange(int[] hsl) {
        return getTo() == 100 ? hsl[2] >= getFrom() : hsl[2] >= getFrom() && hsl[2] < getTo();
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    public String toString() {
        return "ColorSegment " + getName() + " has " + getPopulation() + " pixels. colored to (" + getRed() + ", " + getGreen() + ", " + getBlue() + ")";
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    public int getColor() {
        int lightness = getLightness();
        return Color.rgb(lightness, lightness, lightness);
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    protected int getRed() {
        return getLightness();
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    protected int getGreen() {
        return getLightness();
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    protected int getBlue() {
        return getLightness();
    }

    private int getLightness() {
        return (ColorUtils.getLightness(super.getColor()) * 255) / 100;
    }
}
