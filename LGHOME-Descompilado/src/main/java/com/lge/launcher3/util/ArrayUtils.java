package com.lge.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public class ArrayUtils {
    public static long[] convertIntToLongArray(int[] convertFrom) {
        return convertIntToLongArray(convertFrom, null);
    }

    public static long[] convertIntToLongArray(int[] convertFrom, long[] convertTo) {
        if (convertFrom == null || convertFrom.length <= 0) {
            return null;
        }
        if (convertTo == null) {
            convertTo = new long[convertFrom.length];
        }
        if (convertTo.length != convertFrom.length) {
            return null;
        }
        for (int i = 0; i < convertFrom.length; i++) {
            convertTo[i] = convertFrom[i];
        }
        return convertTo;
    }
}
