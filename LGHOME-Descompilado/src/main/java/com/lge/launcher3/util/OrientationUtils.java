package com.lge.launcher3.util;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class OrientationUtils {
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }
}
