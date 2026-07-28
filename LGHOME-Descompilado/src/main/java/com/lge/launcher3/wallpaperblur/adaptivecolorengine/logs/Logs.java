package com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs;

import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class Logs {
    private static final String PROPERTY_PERSIST_SERVICE_MAIN_ENABLE;
    private static final int REAL_METHOD_POS = 2;
    private static boolean sIsLogOn;
    private static String sTAG;

    static {
        PROPERTY_PERSIST_SERVICE_MAIN_ENABLE = Build.VERSION.SDK_INT < 28 ? "persist.service.main.enable" : "persist.vendor.lge.service.main.enable";
        sIsLogOn = false;
    }

    private static String prefix() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        return "[" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ":" + stackTraceElement.getMethodName() + "()-" + Thread.currentThread().getName() + " Thread] ";
    }

    public static void setLogOn(String tag) {
        if (sIsLogOn) {
            return;
        }
        int i = SystemProperties.getInt(PROPERTY_PERSIST_SERVICE_MAIN_ENABLE, 0);
        sTAG = tag;
        if (i > 0) {
            Log.d(tag, "####### logServiceEnable = " + i + " : Log service is enable. You can debug log messages. ");
            sIsLogOn = true;
            return;
        }
        Log.d(tag, "####### logServiceEnable = " + i + " : Log service is disable. Please set log service to enable for debug. ");
        sIsLogOn = false;
    }

    public static boolean getLogOn() {
        return sIsLogOn;
    }

    public static void d() {
        if (sIsLogOn) {
            Log.d(sTAG, prefix());
        }
    }

    public static void i() {
        if (sIsLogOn) {
            Log.i(sTAG, prefix());
        }
    }

    public static void e() {
        if (sIsLogOn) {
            Log.e(sTAG, prefix());
        }
    }

    public static void v() {
        if (sIsLogOn) {
            Log.v(sTAG, prefix());
        }
    }

    public static void w() {
        if (sIsLogOn) {
            Log.w(sTAG, prefix());
        }
    }

    public static void d(String msg) {
        if (sIsLogOn) {
            Log.d(sTAG, prefix() + msg);
        }
    }

    public static void i(String msg) {
        if (sIsLogOn) {
            Log.i(sTAG, prefix() + msg);
        }
    }

    public static void e(String msg) {
        if (sIsLogOn) {
            Log.e(sTAG, prefix() + msg);
        }
    }

    public static void v(String msg) {
        if (sIsLogOn) {
            Log.v(sTAG, prefix() + msg);
        }
    }

    public static void w(String msg) {
        if (sIsLogOn) {
            Log.w(sTAG, prefix() + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (sIsLogOn) {
            Log.d(tag, prefix() + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (sIsLogOn) {
            Log.i(tag, prefix() + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (sIsLogOn) {
            Log.e(tag, prefix() + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (sIsLogOn) {
            Log.v(tag, prefix() + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (sIsLogOn) {
            Log.w(tag, prefix() + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.d(tag, prefix() + msg, tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.i(tag, prefix() + msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.e(tag, prefix() + msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.v(tag, prefix() + msg, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.w(tag, prefix() + msg, tr);
        }
    }

    public static void d_raw(String msg) {
        if (sIsLogOn) {
            Log.d(sTAG, msg);
        }
    }

    public static void i_raw(String msg) {
        if (sIsLogOn) {
            Log.i(sTAG, msg);
        }
    }

    public static void e_raw(String msg) {
        if (sIsLogOn) {
            Log.e(sTAG, msg);
        }
    }

    public static void v_raw(String msg) {
        if (sIsLogOn) {
            Log.v(sTAG, msg);
        }
    }

    public static void w_raw(String msg) {
        if (sIsLogOn) {
            Log.w(sTAG, msg);
        }
    }

    public static void d_raw(String tag, String msg) {
        if (sIsLogOn) {
            Log.d(tag, msg);
        }
    }

    public static void i_raw(String tag, String msg) {
        if (sIsLogOn) {
            Log.i(tag, msg);
        }
    }

    public static void e_raw(String tag, String msg) {
        if (sIsLogOn) {
            Log.e(tag, msg);
        }
    }

    public static void v_raw(String tag, String msg) {
        if (sIsLogOn) {
            Log.v(tag, msg);
        }
    }

    public static void w_raw(String tag, String msg) {
        if (sIsLogOn) {
            Log.w(tag, msg);
        }
    }

    public static void d_raw(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i_raw(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.i(tag, msg, tr);
        }
    }

    public static void e_raw(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.e(tag, msg, tr);
        }
    }

    public static void v_raw(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.v(tag, msg, tr);
        }
    }

    public static void w_raw(String tag, String msg, Throwable tr) {
        if (sIsLogOn) {
            Log.w(tag, msg, tr);
        }
    }

    public static void aassert(boolean condition, String log) {
        if (!sIsLogOn || condition) {
            return;
        }
        e(log);
        throw new RuntimeException(sTAG + " : " + log);
    }
}
