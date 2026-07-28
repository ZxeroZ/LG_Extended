package com.lge.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public class MathFunctionUtils {
    public static final float computeSumOfArithmeticSequence(int n, float a, float d) {
        return (n / 2.0f) * ((a * 2.0f) + ((n - 1) * d));
    }

    public static final float getDiffInSumOfArithSeq(int n, float a1, float sum) {
        return ((sum - a1) * 2.0f) / (n - 1.0f);
    }

    public static final float computeSumOfGeometricSequence(int n, float a, float r) {
        return a * ((1.0f - ((float) Math.pow(r, n + 1))) / (1.0f - r));
    }

    public static final float getCircularEquation(float centerX, float centerY, float x, float radius) {
        return (float) (Math.sqrt(Math.pow(radius, 2.0d) - Math.pow(x - centerX, 2.0d)) + ((double) centerY));
    }

    public static final boolean equals(float a, float b) {
        return Math.abs(a - b) < 1.0E-6f;
    }

    public static final boolean equals(float a, float b, int precisionPoint) {
        return Math.abs(a - b) < 1.0f / ((float) Math.pow(10.0d, (double) precisionPoint));
    }

    public static final boolean equals(double a, double b) {
        return Math.abs(a - b) < 9.999999974752427E-7d;
    }

    public static final boolean equals(int a, int b, int deviation) {
        return Math.abs(a - b) <= deviation;
    }

    public static final float truncate(float value, int digits) {
        return ((int) (value * r5)) / ((float) Math.pow(10.0d, digits));
    }

    public static final float round(float value, int digits) {
        if (digits < 0) {
            return value;
        }
        return Math.round(value * r5) / ((float) Math.pow(10.0d, digits - 1));
    }

    public static final int round(int value, int digits) {
        if (digits < 0) {
            return value;
        }
        return (int) (Math.round(value / r5) * ((float) Math.pow(10.0d, digits)));
    }

    public static final float normalize(float current, float start, float end) {
        if (Float.compare(start, end) == 0) {
            return 0.0f;
        }
        return (current - start) / (end - start);
    }

    public static final int random(int start, int end) {
        if (start > end || start < 0 || end < 0) {
            return 0;
        }
        return ((int) (Math.random() * ((double) ((end - start) + 1)))) + start;
    }

    public static final double getDiagonalDistanceOfRectangle(double width, double height) {
        return Math.sqrt((width * width) + (height * height));
    }

    public static final float floorDigit(float value, int digit) {
        if (digit <= 0 || digit >= 9) {
            LGLog.i("LGMath", "wrong digit : " + digit);
            return value;
        }
        return (float) (Math.floor(value * r5) / ((double) ((float) Math.pow(10.0d, digit))));
    }
}
