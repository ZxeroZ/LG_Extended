package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import androidx.core.view.ViewCompat;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ColorExtractor {
    private final int NUM_SAMPLES = 20;
    private final float[] mTmpHsv = new float[3];
    private final float[] mTmpHueScoreHistogram = new float[360];
    private final int[] mTmpPixels = new int[20];
    private final SparseArray<Float> mTmpRgbScores = new SparseArray<>();

    public int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public int findDominantColorByHue(Bitmap bitmap, int samples) {
        int i;
        int i2;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int iSqrt = (int) Math.sqrt((height * width) / samples);
        if (iSqrt < 1) {
            iSqrt = 1;
        }
        float[] fArr = this.mTmpHsv;
        Arrays.fill(fArr, 0.0f);
        float[] fArr2 = this.mTmpHueScoreHistogram;
        Arrays.fill(fArr2, 0.0f);
        int i3 = -1;
        int[] iArr = this.mTmpPixels;
        int i4 = 0;
        Arrays.fill(iArr, 0);
        int i5 = 0;
        int i6 = 0;
        float f = -1.0f;
        while (true) {
            i = ViewCompat.MEASURED_STATE_MASK;
            if (i5 >= height) {
                break;
            }
            int i7 = i4;
            while (i7 < width) {
                int pixel = bitmap.getPixel(i7, i5);
                if (((pixel >> 24) & 255) < 128) {
                    i2 = height;
                } else {
                    int i8 = pixel | ViewCompat.MEASURED_STATE_MASK;
                    Color.colorToHSV(i8, fArr);
                    i2 = height;
                    int i9 = (int) fArr[0];
                    if (i9 >= 0 && i9 < fArr2.length) {
                        if (i6 < samples) {
                            iArr[i6] = i8;
                            i6++;
                        }
                        fArr2[i9] = fArr2[i9] + (fArr[1] * fArr[2]);
                        if (fArr2[i9] > f) {
                            i3 = i9;
                            f = fArr2[i9];
                        }
                    }
                }
                i7 += iSqrt;
                height = i2;
            }
            i5 += iSqrt;
            i4 = 0;
        }
        SparseArray<Float> sparseArray = this.mTmpRgbScores;
        sparseArray.clear();
        float f2 = -1.0f;
        for (int i10 = 0; i10 < i6; i10++) {
            int i11 = iArr[i10];
            Color.colorToHSV(i11, fArr);
            if (((int) fArr[0]) == i3) {
                float f3 = fArr[1];
                float f4 = fArr[2];
                int i12 = ((int) (100.0f * f3)) + ((int) (10000.0f * f4));
                float fFloatValue = f3 * f4;
                Float f5 = sparseArray.get(i12);
                if (f5 != null) {
                    fFloatValue += f5.floatValue();
                }
                sparseArray.put(i12, Float.valueOf(fFloatValue));
                if (fFloatValue > f2) {
                    i = i11;
                    f2 = fFloatValue;
                }
            }
        }
        return i;
    }
}
