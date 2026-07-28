package com.lge.launcher3.wallpaperblur.adaptivecolorengine;

import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil.ColorInfo;

/* JADX INFO: loaded from: classes.dex */
public final class AdaptiveColor {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    private static AdaptiveColor sThis;
    private ColorInfo[] mAdaptedColors;
    private int mBlurRadius;
    private boolean mIsColored;
    private ColorInfo[] mOriginalColors;
    private AdaptiveColorParam mParam = AdaptiveColorParam.getParam();
    private int mSelected2ndColorIdx;

    private AdaptiveColor() {
    }

    public static AdaptiveColor getInstance() {
        if (sThis == null) {
            synchronized (AdaptiveColor.class) {
                if (sThis == null) {
                    sThis = new AdaptiveColor();
                }
            }
        }
        return sThis;
    }

    protected void clear() {
        setBlurRadius(0);
        if (this.mAdaptedColors == null) {
            ColorInfo[] colorInfoArr = new ColorInfo[3];
            this.mAdaptedColors = colorInfoArr;
            colorInfoArr[0] = new ColorInfo();
            this.mAdaptedColors[1] = new ColorInfo();
            this.mAdaptedColors[2] = new ColorInfo();
        }
        if (this.mOriginalColors != null) {
            this.mOriginalColors = null;
        }
        this.mIsColored = false;
        int[] defaultColors = AdaptiveColorParam.getDefaultColors();
        for (int i = 0; i < defaultColors.length; i++) {
            this.mAdaptedColors[i].setARGB(defaultColors[i]);
        }
    }

    public int getBlurRadius() {
        return this.mBlurRadius;
    }

    protected void setBlurRadius(int blurRadius) {
        this.mBlurRadius = blurRadius;
    }

    public int getColor() {
        return this.mAdaptedColors[0].getARGB();
    }

    public int get2ndColor() {
        return this.mAdaptedColors[1].getARGB();
    }

    public int get3rdColor() {
        return this.mAdaptedColors[2].getARGB();
    }

    public int get2ndAlpha() {
        return this.mAdaptedColors[1].getAlpha();
    }

    public int get3rdAlpha() {
        return this.mAdaptedColors[2].getAlpha();
    }

    public ColorInfo[] getColors() {
        return new ColorInfo[]{this.mAdaptedColors[0], this.mOriginalColors[0]};
    }

    public ColorInfo[] get2ndColors() {
        return new ColorInfo[]{this.mAdaptedColors[1], this.mOriginalColors[this.mSelected2ndColorIdx]};
    }

    public ColorInfo[] get3rdColors() {
        return new ColorInfo[]{this.mAdaptedColors[2], this.mOriginalColors[0]};
    }

    public void update(ColorInfo[] colorInfos) {
        this.mOriginalColors = colorInfos;
        this.mIsColored = true;
        update();
    }

    public synchronized void update() {
        if (this.mIsColored) {
            setBlurRadius(this.mParam.blurRadius);
            AdaptiveColorTuner.getAdaptedColor(this.mOriginalColors[0], this.mAdaptedColors[0], this.mParam);
            this.mSelected2ndColorIdx = AdaptiveColorTuner.get2ndAdaptedColor(this.mOriginalColors, this.mAdaptedColors[1], this.mParam);
            AdaptiveColorTuner.get3rdAdaptedColor(this.mOriginalColors[0], this.mAdaptedColors[2], this.mParam);
        }
    }

    public boolean isColored() {
        return this.mIsColored;
    }
}
