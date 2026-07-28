package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil;

import android.graphics.Color;
import androidx.core.view.ViewCompat;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs.Logs;

/* JADX INFO: loaded from: classes.dex */
public class ColorInfo {
    private int mAlpha;
    private int mHue;
    private boolean mIsValid = false;
    private int mLightness;
    private int mSaturation;

    public void setHSL(int[] hsl) {
        setHue(hsl[0]);
        setSaturation(hsl[1]);
        setLightness(hsl[2]);
        this.mIsValid = true;
    }

    public int getHue() {
        checkValidation("getHue()");
        return this.mHue;
    }

    public void setHue(int hue) {
        this.mHue = hue;
    }

    public int getSaturation() {
        checkValidation("getSaturation()");
        return this.mSaturation;
    }

    public void setSaturation(int saturation) {
        this.mSaturation = saturation;
    }

    public int getLightness() {
        checkValidation("getLightness()");
        return this.mLightness;
    }

    public void setLightness(int lightness) {
        this.mLightness = lightness;
    }

    public int getAlpha() {
        checkValidation("getAlpha()");
        return this.mAlpha;
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    public void setARGB(int argb) {
        int[] iArr = {0, 0, 0};
        ColorUtils.RGBtoHSL(argb, iArr);
        setHue(iArr[0]);
        setSaturation(iArr[1]);
        setLightness(iArr[2]);
        setAlpha(Color.alpha(argb));
        this.mIsValid = true;
    }

    public void setRGB(int rgb) {
        int[] iArr = {0, 0, 0};
        ColorUtils.RGBtoHSL(rgb, iArr);
        setHue(iArr[0]);
        setSaturation(iArr[1]);
        setLightness(iArr[2]);
        setAlpha(255);
        this.mIsValid = true;
    }

    public int getRGB() {
        checkValidation("getRGB()");
        return ColorUtils.HSLtoRGB(new int[]{this.mHue, this.mSaturation, this.mLightness});
    }

    public int getARGB() {
        checkValidation("getARGB()");
        return (getRGB() & ViewCompat.MEASURED_SIZE_MASK) | ((this.mAlpha << 24) & ViewCompat.MEASURED_STATE_MASK);
    }

    public boolean isValid() {
        return this.mIsValid;
    }

    public void setValid(boolean isValid) {
        this.mIsValid = isValid;
    }

    public String toString() {
        if (!this.mIsValid) {
            return "HLS(INVALID), RGB(INVALID), Alpah(INVALID)";
        }
        int rgb = getRGB();
        return "ColorInfo : HLS(" + this.mHue + ", " + this.mSaturation + ", " + this.mLightness + "), RGB(" + Color.red(rgb) + ", " + Color.green(rgb) + ", " + Color.blue(rgb) + "), Alpah(" + this.mAlpha + ")";
    }

    public boolean isMonochrom() {
        checkValidation("isMonochrom()");
        return this.mHue == 0 && this.mSaturation == 0;
    }

    private void checkValidation(String from) {
        Logs.aassert(this.mIsValid, from + " - ColorInfo is not Valid");
    }
}
