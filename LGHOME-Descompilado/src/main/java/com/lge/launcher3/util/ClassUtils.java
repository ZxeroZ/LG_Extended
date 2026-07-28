package com.lge.launcher3.util;

import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class ClassUtils {
    private static final String TAG = "ClassUtils";

    public static Object getOutperClassReference(Object object) {
        try {
            Field declaredField = object.getClass().getDeclaredField("this$0");
            declaredField.setAccessible(true);
            return declaredField.get(object);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
            LGLog.i(TAG, "An outer class's reference could't be gotten.");
            return null;
        }
    }
}
