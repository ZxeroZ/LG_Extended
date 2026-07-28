package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor;

import android.graphics.Bitmap;
import com.android.launcher3.LauncherAnimUtils;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil.ColorInfo;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil.ColorUtils;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs.Logs;

/* JADX INFO: loaded from: classes.dex */
public final class ColorExtractor {
    private static final boolean DEBUG = false;
    public static final int NUM_OF_SEGMENT = 14;
    private static ColorExtractor sThis;
    private ColorInfo[] mColorInfos;
    private ColorSegment[] mColorSegments;

    private ColorExtractor() {
        initializeColorSegments();
        initializeColorInfos();
    }

    public static ColorExtractor getInstance() {
        if (sThis == null) {
            synchronized (ColorExtractor.class) {
                if (sThis == null) {
                    sThis = new ColorExtractor();
                }
            }
        }
        return sThis;
    }

    public ColorInfo[] extract(Bitmap bitmap) {
        clearSegments();
        parseImage(bitmap);
        return getOrderedColors();
    }

    private ColorInfo[] getOrderedColors() {
        ColorSegment[] colorSegmentArr = this.mColorSegments;
        int[] iArr = new int[colorSegmentArr.length];
        int[] iArr2 = new int[colorSegmentArr.length];
        iArr[0] = colorSegmentArr[0].getColor();
        iArr2[0] = this.mColorSegments[0].getPopulation();
        int i = 1;
        while (true) {
            ColorSegment[] colorSegmentArr2 = this.mColorSegments;
            if (i >= colorSegmentArr2.length) {
                break;
            }
            int population = colorSegmentArr2[i].getPopulation();
            int color = this.mColorSegments[i].getColor();
            int i2 = i - 1;
            while (i2 > -1 && iArr2[i2] < population) {
                int i3 = i2 + 1;
                iArr2[i3] = iArr2[i2];
                iArr[i3] = iArr[i2];
                i2--;
            }
            int i4 = i2 + 1;
            iArr2[i4] = population;
            iArr[i4] = color;
            i++;
        }
        int i5 = 0;
        while (true) {
            ColorInfo[] colorInfoArr = this.mColorInfos;
            if (i5 >= colorInfoArr.length) {
                return colorInfoArr;
            }
            boolean z = iArr2[i5] > 0;
            if (z) {
                colorInfoArr[i5].setRGB(iArr[i5]);
            }
            this.mColorInfos[i5].setValid(z);
            i5++;
        }
    }

    private void initializeColorSegments() {
        ColorSegment[] colorSegmentArr = new ColorSegment[14];
        this.mColorSegments = colorSegmentArr;
        colorSegmentArr[0] = new LightnessSegment("black", 0, 10);
        this.mColorSegments[1] = new LightnessSegment("white", 95, 100);
        this.mColorSegments[2] = new GrayscaleSegment("gray");
        this.mColorSegments[3] = new HueSegment("red", 318, 17);
        this.mColorSegments[4] = new HueSegment("orange", 18, 53);
        this.mColorSegments[5] = new HueSegment("yellow", 54, 71);
        this.mColorSegments[6] = new HueSegment("lime", 72, 89);
        this.mColorSegments[7] = new HueSegment("green", 90, 161);
        this.mColorSegments[8] = new HueSegment("teal", 162, SysUiStatsLog.STYLE_UI_CHANGED);
        this.mColorSegments[9] = new HueSegment("cyan", 180, 197);
        this.mColorSegments[10] = new HueSegment("blue", 198, 227);
        this.mColorSegments[11] = new HueSegment("indigo", 228, 317);
        this.mColorSegments[12] = new HueSegment("purple", 318, LauncherAnimUtils.ALL_APPS_TRANSITION_MS);
        this.mColorSegments[13] = new HueSegment("magenta", 321, 330);
    }

    private void initializeColorInfos() {
        this.mColorInfos = new ColorInfo[14];
        int i = 0;
        while (true) {
            ColorInfo[] colorInfoArr = this.mColorInfos;
            if (i >= colorInfoArr.length) {
                return;
            }
            colorInfoArr[i] = new ColorInfo();
            i++;
        }
    }

    private void parseImage(Bitmap orgBitmap) {
        Bitmap bitmapScaleBitmapDown = scaleBitmapDown(orgBitmap);
        int[] iArr = new int[3];
        int width = bitmapScaleBitmapDown.getWidth();
        int height = bitmapScaleBitmapDown.getHeight();
        int i = width * height;
        int[] iArr2 = new int[i];
        bitmapScaleBitmapDown.getPixels(iArr2, 0, width, 0, 0, width, height);
        for (int i2 = 0; i2 < i; i2++) {
            ColorUtils.RGBtoHSL(iArr2[i2], iArr);
            ColorSegment colorSegment = null;
            ColorSegment[] colorSegmentArr = this.mColorSegments;
            int length = colorSegmentArr.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    break;
                }
                ColorSegment colorSegment2 = colorSegmentArr[i3];
                if (colorSegment2.inRange(iArr)) {
                    colorSegment = colorSegment2;
                    break;
                }
                i3++;
            }
            if (colorSegment != null) {
                colorSegment.addPixel(iArr2[i2]);
            }
        }
        if (bitmapScaleBitmapDown != orgBitmap) {
            bitmapScaleBitmapDown.recycle();
        }
    }

    private void printSegmentInfo() {
        Logs.d("-----------------------------------------------------");
        Logs.d("[Color Segment]");
        for (ColorSegment colorSegment : this.mColorSegments) {
            if (colorSegment.getPopulation() != 0) {
                Logs.d("  " + colorSegment.toString());
            }
        }
        Logs.d("[Color Info]");
        for (ColorInfo colorInfo : this.mColorInfos) {
            if (colorInfo.isValid()) {
                Logs.d("  " + colorInfo.toString());
            }
        }
        Logs.d("-----------------------------------------------------");
    }

    private void clearSegments() {
        for (ColorSegment colorSegment : this.mColorSegments) {
            colorSegment.clear();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap) {
        int iMin = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (iMin <= 100) {
            return bitmap;
        }
        float f = 100.0f / iMin;
        return Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * f), Math.round(bitmap.getHeight() * f), false);
    }
}
