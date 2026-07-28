package com.lge.launcher3.util;

import android.app.WallpaperManager;
import android.content.Context;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class LGUtilFunctionReflect {
    private static final String CONTEXT_CLASS_NAME = "android.content.Context";

    public static class OsManagerReflect {
        private static final String LGCONTEXT_NAME = "com.lge.systemservice.core.LGContext";
        private static final String OSMANAGER_NAME = "com.lge.systemservice.core.OsManager";
        private static Method sGetLGSystemService;
        private static Method sGoToSleepWithForce;
        private static String sOsServiceField;

        static {
            try {
                Class<?> cls = Class.forName(LGCONTEXT_NAME);
                Class<?> cls2 = Class.forName(OSMANAGER_NAME);
                if (cls == null || cls2 == null) {
                    return;
                }
                sGetLGSystemService = cls.getMethod("getLGSystemService", String.class);
                sOsServiceField = (String) cls.getField("OS_SERVICE").get(cls);
                sGoToSleepWithForce = cls2.getMethod("goToSleepWithForce", Long.TYPE, Integer.TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void goToSleepWithForce(Context context, long time, int reason) {
            Method method = sGetLGSystemService;
            if (method == null || sOsServiceField == null || sGoToSleepWithForce == null) {
                return;
            }
            try {
                sGoToSleepWithForce.invoke(method.invoke(newLGContextInstance(context), sOsServiceField), Long.valueOf(time), Integer.valueOf(reason));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static Object newLGContextInstance(Context context) {
            return LGUtilFunctionReflect.newInstanceByClassName(LGCONTEXT_NAME, context);
        }
    }

    public static Object newInstanceByClassName(String className, Context context) {
        Constructor<?> constructor;
        try {
            constructor = Class.forName(className).getConstructor(Class.forName(CONTEXT_CLASS_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            constructor = null;
        }
        if (constructor != null) {
            try {
                return constructor.newInstance(context);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static class UserInfoConstantsReflect {
        private static final String CLASS_NAME = "com.lge.constants.UserInfoConstants";
        public static int sFlagKids;

        static {
            try {
                Class<?> cls = Class.forName(CLASS_NAME);
                if (cls != null) {
                    sFlagKids = cls.getField("FLAG_KIDS").getInt(cls);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (NoSuchFieldException e3) {
                e3.printStackTrace();
            }
        }
    }

    public static class WallpaperManagerExReflect {
        private static final String CLASS_NAME = "android.app.WallpaperManagerEx";
        private static Method sTextColorForCurrentWallpaper;

        static {
            try {
                Class<?> cls = Class.forName(CLASS_NAME);
                if (cls != null) {
                    sTextColorForCurrentWallpaper = cls.getDeclaredMethod("getTextColorForCurrentWallpaper", new Class[0]);
                }
            } catch (Exception unused) {
                LGLog.w("WallpaperManagerExReflect", "not init: getTextColorForCurrentWallpaper ", new int[0]);
            }
        }

        public static boolean possibleToUseWallpapaerApi() {
            return sTextColorForCurrentWallpaper != null;
        }

        public static int getTextColorForCurrentWallpaper(WallpaperManager wallpaperManger) {
            Method method = sTextColorForCurrentWallpaper;
            if (method != null) {
                try {
                    return ((Integer) method.invoke(wallpaperManger, new Object[0])).intValue();
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LGLog.w("WallpaperManagerExReflect", "getTextColorForCurrentWallpaper " + e, new int[0]);
                }
            }
            return 0;
        }
    }
}
