package com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorutil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import androidx.core.view.ViewCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class ColorUtils {
    private ColorUtils() {
    }

    public static void RGBtoHSL(int r, int g, int b, int[] hsl) {
        float f;
        float fAbs;
        float f2 = r / 255.0f;
        float f3 = g / 255.0f;
        float f4 = b / 255.0f;
        float fMax = Math.max(f2, Math.max(f3, f4));
        float fMin = Math.min(f2, Math.min(f3, f4));
        float f5 = fMax - fMin;
        float f6 = (fMax + fMin) / 2.0f;
        if (fMax == fMin) {
            f = 0.0f;
            fAbs = 0.0f;
        } else {
            f = fMax == f2 ? ((f3 - f4) / f5) % 6.0f : fMax == f3 ? ((f4 - f2) / f5) + 2.0f : ((f2 - f3) / f5) + 4.0f;
            fAbs = (f5 * 100.0f) / (1.0f - Math.abs((2.0f * f6) - 1.0f));
        }
        hsl[0] = (int) ((((f * 60.0f) + 360.0f) % 360.0f) + 0.5f);
        hsl[1] = (int) (fAbs + 0.5f);
        hsl[2] = (int) ((f6 * 100.0f) + 0.5f);
    }

    public static int HSLtoRGB(int[] hsl) {
        int iRound;
        int iRound2;
        int iRound3;
        float f = hsl[0];
        float f2 = hsl[1] / 100.0f;
        float f3 = hsl[2] / 100.0f;
        float fAbs = (1.0f - Math.abs((f3 * 2.0f) - 1.0f)) * f2;
        float f4 = f3 - (0.5f * fAbs);
        float fAbs2 = (1.0f - Math.abs(((f / 60.0f) % 2.0f) - 1.0f)) * fAbs;
        switch (((int) f) / 60) {
            case 0:
                iRound = Math.round((fAbs + f4) * 255.0f);
                iRound2 = Math.round((fAbs2 + f4) * 255.0f);
                iRound3 = Math.round(f4 * 255.0f);
                break;
            case 1:
                iRound = Math.round((fAbs2 + f4) * 255.0f);
                iRound2 = Math.round((fAbs + f4) * 255.0f);
                iRound3 = Math.round(f4 * 255.0f);
                break;
            case 2:
                iRound = Math.round(f4 * 255.0f);
                iRound2 = Math.round((fAbs + f4) * 255.0f);
                iRound3 = Math.round((fAbs2 + f4) * 255.0f);
                break;
            case 3:
                iRound = Math.round(f4 * 255.0f);
                iRound2 = Math.round((fAbs2 + f4) * 255.0f);
                iRound3 = Math.round((fAbs + f4) * 255.0f);
                break;
            case 4:
                iRound = Math.round((fAbs2 + f4) * 255.0f);
                iRound2 = Math.round(f4 * 255.0f);
                iRound3 = Math.round((fAbs + f4) * 255.0f);
                break;
            case 5:
            case 6:
                iRound = Math.round((fAbs + f4) * 255.0f);
                iRound2 = Math.round(f4 * 255.0f);
                iRound3 = Math.round((fAbs2 + f4) * 255.0f);
                break;
            default:
                iRound3 = 0;
                iRound = 0;
                iRound2 = 0;
                break;
        }
        return Color.rgb(Math.max(0, Math.min(255, iRound)), Math.max(0, Math.min(255, iRound2)), Math.max(0, Math.min(255, iRound3)));
    }

    public static int HSLtoRGB(int alpha, int[] hsl) {
        return ((alpha << 24) & ViewCompat.MEASURED_STATE_MASK) | (HSLtoRGB(hsl) & ViewCompat.MEASURED_SIZE_MASK);
    }

    public static void RGBtoHSL(int color, int[] hsl) {
        RGBtoHSL(Color.red(color), Color.green(color), Color.blue(color), hsl);
    }

    public static void saveBitmapToFile(Context context, Bitmap bitmap, String path, String filename) throws Throwable {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (path != null) {
            absolutePath = absolutePath + path;
        }
        String str = absolutePath + "/" + filename;
        File file = new File(absolutePath);
        File file2 = new File(str);
        FileOutputStream fileOutputStream = null;
        try {
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            try {
                if (!file.exists() || !file.isDirectory()) {
                    file.mkdirs();
                }
                file2.createNewFile();
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                    fileOutputStream2.close();
                } catch (Exception e2) {
                    e = e2;
                    fileOutputStream = fileOutputStream2;
                    e.printStackTrace();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
            }
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static int getLightness(int color) {
        int[] iArr = new int[3];
        RGBtoHSL(color, iArr);
        return iArr[2];
    }
}
