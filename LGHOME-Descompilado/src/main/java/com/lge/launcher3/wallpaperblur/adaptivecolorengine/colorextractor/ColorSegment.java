package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor;

import android.graphics.Color;

/* JADX INFO: loaded from: classes.dex */
public class ColorSegment {
    private static final int ALPHA_RANGE_MAX = 255;
    protected static final int HUE = 0;
    protected static final int LIGHTNESS = 2;
    protected static final int SATURATION = 1;
    private float mBlueSum;
    private int mFrom;
    private float mGreenSum;
    private String mName;
    private int mPopulation;
    private float mRedSum;
    private int mTo;

    protected boolean inRange(int[] hsl) {
        return false;
    }

    protected ColorSegment(String name) {
        this(name, 0, 0);
    }

    protected ColorSegment(String name, int from, int to) {
        this.mName = name;
        setFrom(from);
        setTo(to);
        clear();
    }

    protected void clear() {
        this.mBlueSum = 0.0f;
        this.mGreenSum = 0.0f;
        this.mRedSum = 0.0f;
        this.mPopulation = 0;
    }

    protected int addPixel(int color) {
        this.mRedSum += Color.red(color) / 255.0f;
        this.mGreenSum += Color.green(color) / 255.0f;
        this.mBlueSum += Color.blue(color) / 255.0f;
        int i = this.mPopulation + 1;
        this.mPopulation = i;
        return i;
    }

    protected String getName() {
        return this.mName;
    }

    public int getColor() {
        int i = this.mPopulation;
        if (i == 0) {
            return 0;
        }
        return Color.argb(255, (int) ((this.mRedSum * 255.0f) / i), (int) ((this.mGreenSum * 255.0f) / i), (int) ((this.mBlueSum * 255.0f) / i));
    }

    protected int getRed() {
        int i = this.mPopulation;
        if (i == 0) {
            return 0;
        }
        return (int) ((this.mRedSum * 255.0f) / i);
    }

    protected int getGreen() {
        int i = this.mPopulation;
        if (i == 0) {
            return 0;
        }
        return (int) ((this.mGreenSum * 255.0f) / i);
    }

    protected int getBlue() {
        int i = this.mPopulation;
        if (i == 0) {
            return 0;
        }
        return (int) ((this.mBlueSum * 255.0f) / i);
    }

    public int getPopulation() {
        return this.mPopulation;
    }

    public int hashCode() {
        String str = this.mName;
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ColorSegment colorSegment = (ColorSegment) obj;
        String str = this.mName;
        if (str == null) {
            if (colorSegment.mName != null) {
                return false;
            }
        } else if (!str.equals(colorSegment.mName)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "ColorSegment " + this.mName + " (" + getFrom() + " ~ " + getTo() + ") has " + getPopulation() + " pixels. color is (" + getRed() + ", " + getGreen() + ", " + getBlue() + ")";
    }

    protected int getFrom() {
        return this.mFrom;
    }

    protected void setFrom(int from) {
        this.mFrom = from;
    }

    protected int getTo() {
        return this.mTo;
    }

    protected void setTo(int to) {
        this.mTo = to;
    }
}
