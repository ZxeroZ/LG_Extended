package com.lge.lgewidgetlib;

import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes2.dex */
public class LgeReflectionUtil {
    private static final String TAG = "LgeReflectionUtil";

    public static Long getLongField(Class<?> clazz, Object target, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            return Long.valueOf(declaredField.getLong(target));
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static void setLongField(Class<?> clazz, Object target, String name, long value) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.setLong(target, value);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static int getIntField(Class<?> clazz, Object target, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField.getInt(target);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static void setIntField(Class<?> clazz, Object target, String name, int value) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.setInt(target, value);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateLongField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static boolean getPrivateBooleanField(Class<?> clazz, Object target, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField.getBoolean(target);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static void setPrivateBooleanField(Class<?> clazz, Object target, String name, boolean value) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.setBoolean(target, value);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "getPrivateBooleanField name = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static Object getPrivateField(Class<?> clazz, Object target, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField.get(target);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "IllegalAccessException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "IllegalArgumentException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "NoSuchFieldException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static void setPrivateField(Class<?> clazz, Object target, String name, Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.set(target, value);
        } catch (IllegalAccessException e) {
            WLog.d(TAG, "setPrivateField failed, field = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            WLog.d(TAG, "setPrivateField failed, field = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            WLog.d(TAG, "setPrivateField failed, field = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }

    public static View getLgeCustomView(String targetMethod, Class<?>[] parameterType, View view) {
        if (isTargetCutsomView(view, targetMethod, parameterType)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View lgeCustomView = getLgeCustomView(targetMethod, parameterType, viewGroup.getChildAt(i));
                if (lgeCustomView != null) {
                    return lgeCustomView;
                }
            }
        }
        return null;
    }

    private static boolean isTargetCutsomView(View view, String targetMethod, Class<?>[] parameterType) {
        try {
            view.getClass().getMethod(targetMethod, parameterType);
            return true;
        } catch (NoSuchMethodException unused) {
            return false;
        }
    }

    public static void callVoidMethodWithVoidParameter(Object target, String methodName) {
        try {
            try {
                target.getClass().getMethod(methodName, (Class[]) null).invoke(target, new Object[0]);
            } catch (InvocationTargetException e) {
                WLog.e(TAG, methodName, e);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        } catch (NoSuchMethodException unused) {
            WLog.e(TAG, methodName + " method not exist on client");
        }
    }

    public static int callIntMethodWithVoidParameter(Object target, String methodName) {
        try {
            try {
                return ((Integer) target.getClass().getMethod(methodName, (Class[]) null).invoke(target, (Object[]) null)).intValue();
            } catch (InvocationTargetException e) {
                WLog.e(TAG, methodName, e);
                return 0;
            } catch (Exception e2) {
                e2.printStackTrace();
                return 0;
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

    public static boolean callBooleanMethodWithVoidParameter(Object target, String methodName) {
        try {
            try {
                return ((Boolean) target.getClass().getMethod(methodName, (Class[]) null).invoke(target, (Object[]) null)).booleanValue();
            } catch (InvocationTargetException e) {
                WLog.e(TAG, methodName, e);
                return false;
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public static void callVoidMethodWithBooleanIntegerParameter(Object target, String methodName, boolean param1, int param2) {
        try {
            try {
                target.getClass().getMethod(methodName, Boolean.TYPE, Integer.TYPE).invoke(target, Boolean.valueOf(param1), Integer.valueOf(param2));
            } catch (InvocationTargetException e) {
                WLog.e(TAG, "requestExtViewDimming", e);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
    }

    public static void callVoidMethodWithBooleanIntegerIntegerParameter(Object target, String methodName, boolean param1, int param2, int param3) {
        try {
            try {
                try {
                    target.getClass().getMethod(methodName, Boolean.TYPE, Integer.TYPE, Integer.TYPE).invoke(target, Boolean.valueOf(param1), Integer.valueOf(param2), Integer.valueOf(param3));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (InvocationTargetException e2) {
                WLog.e(TAG, "requestExtViewDimming", e2);
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
    }
}
