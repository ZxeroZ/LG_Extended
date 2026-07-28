package com.android.launcher3.graphics;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class ColorExtractor {
    public static int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x006c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int findDominantColorByHue(android.graphics.Bitmap r20, int r21) {
        /*
            r0 = r21
            int r1 = r20.getHeight()
            int r2 = r20.getWidth()
            int r3 = r1 * r2
            int r3 = r3 / r0
            double r3 = (double) r3
            double r3 = java.lang.Math.sqrt(r3)
            int r3 = (int) r3
            r4 = 1
            if (r3 >= r4) goto L17
            r3 = r4
        L17:
            r5 = 3
            float[] r5 = new float[r5]
            r6 = 360(0x168, float:5.04E-43)
            float[] r7 = new float[r6]
            r8 = -1
            int[] r9 = new int[r0]
            r11 = 0
            r12 = r11
            r13 = r12
            r14 = -1082130432(0xffffffffbf800000, float:-1.0)
        L26:
            r16 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            if (r12 >= r1) goto L7a
            r10 = r11
        L2b:
            if (r10 >= r2) goto L73
            r15 = r20
            int r17 = r15.getPixel(r10, r12)
            int r4 = r17 >> 24
            r4 = r4 & 255(0xff, float:3.57E-43)
            r6 = 128(0x80, float:1.8E-43)
            if (r4 >= r6) goto L3c
            goto L6c
        L3c:
            r4 = r17 | r16
            android.graphics.Color.colorToHSV(r4, r5)
            r6 = r5[r11]
            int r6 = (int) r6
            if (r6 < 0) goto L6c
            r11 = 360(0x168, float:5.04E-43)
            if (r6 < r11) goto L4b
            goto L6e
        L4b:
            if (r13 >= r0) goto L53
            int r18 = r13 + 1
            r9[r13] = r4
            r13 = r18
        L53:
            r4 = 1
            r18 = r5[r4]
            r4 = 2
            r19 = r5[r4]
            float r18 = r18 * r19
            r4 = r7[r6]
            float r4 = r4 + r18
            r7[r6] = r4
            r4 = r7[r6]
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 <= 0) goto L6e
            r4 = r7[r6]
            r14 = r4
            r8 = r6
            goto L6e
        L6c:
            r11 = 360(0x168, float:5.04E-43)
        L6e:
            int r10 = r10 + r3
            r6 = r11
            r4 = 1
            r11 = 0
            goto L2b
        L73:
            r15 = r20
            r11 = r6
            int r12 = r12 + r3
            r4 = 1
            r11 = 0
            goto L26
        L7a:
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r1 = 0
            r10 = -1082130432(0xffffffffbf800000, float:-1.0)
        L82:
            if (r1 >= r13) goto Lc2
            r2 = r9[r1]
            android.graphics.Color.colorToHSV(r2, r5)
            r3 = 0
            r4 = r5[r3]
            int r4 = (int) r4
            if (r4 != r8) goto Lbd
            r4 = 1
            r6 = r5[r4]
            r7 = 2
            r11 = r5[r7]
            r12 = 1120403456(0x42c80000, float:100.0)
            float r12 = r12 * r6
            int r12 = (int) r12
            r14 = 1176256512(0x461c4000, float:10000.0)
            float r14 = r14 * r11
            int r14 = (int) r14
            int r12 = r12 + r14
            float r6 = r6 * r11
            java.lang.Object r11 = r0.get(r12)
            java.lang.Float r11 = (java.lang.Float) r11
            if (r11 != 0) goto La9
            goto Lae
        La9:
            float r11 = r11.floatValue()
            float r6 = r6 + r11
        Lae:
            java.lang.Float r11 = java.lang.Float.valueOf(r6)
            r0.put(r12, r11)
            int r11 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r11 <= 0) goto Lbf
            r16 = r2
            r10 = r6
            goto Lbf
        Lbd:
            r4 = 1
            r7 = 2
        Lbf:
            int r1 = r1 + 1
            goto L82
        Lc2:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.ColorExtractor.findDominantColorByHue(android.graphics.Bitmap, int):int");
    }
}
