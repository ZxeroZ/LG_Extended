package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor;

/* JADX INFO: loaded from: classes.dex */
public class HueSegment extends ColorSegment {
    private static final int HUE_RANGE_MAX = 360;
    private static final int HUE_RANGE_MIN = 0;

    public HueSegment(String name, int from, int to) {
        super(name, from, to);
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorSegment
    public boolean inRange(int[] hsl) {
        int i = hsl[0];
        int to = (int) (getTo() + 1.0f);
        int from = getFrom();
        return from > to ? (i >= from && i <= HUE_RANGE_MAX) || (i >= 0 && i < to) : i >= from && i < to;
    }
}
