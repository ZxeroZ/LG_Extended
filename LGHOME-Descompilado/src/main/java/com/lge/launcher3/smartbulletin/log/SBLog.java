package com.lge.launcher3.smartbulletin.log;

import android.util.Log;
import com.lge.launcher3.smartbulletin.constant.SBConstant;

/* JADX INFO: loaded from: classes.dex */
public final class SBLog {
    private static final String LOG_TAG = "[LGHome][Smartbulletin]";
    private static final int REAL_METHOD_POS = 2;

    private static final StringBuilder prefix() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        return new StringBuilder("[").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(":").append(stackTraceElement.getMethodName()).append("()]");
    }

    public static final void v(String tag, String msg) {
        if (SBConstant.DEBUG) {
            Log.v(LOG_TAG + tag, prefix().append(msg).toString());
        }
    }

    public static final void d(String tag, String msg) {
        if (SBConstant.DEBUG) {
            Log.d(LOG_TAG + tag, prefix().append(msg).toString());
        }
    }

    public static final void d(String tag, String msg, Throwable tr) {
        if (SBConstant.DEBUG) {
            Log.d(LOG_TAG + tag, prefix().append(msg).append(tr).toString());
        }
    }

    public static final void i(String tag, String msg, Throwable tr) {
        Log.i(LOG_TAG + tag, prefix().append(msg).append(tr).toString());
    }

    public static final void i(String tag, String msg) {
        Log.i(LOG_TAG + tag, prefix().append(msg).toString());
    }

    public static final void w(String tag, String msg) {
        Log.w(LOG_TAG + tag, prefix().append(msg).toString());
    }

    public static final void w(String tag, String msg, Exception ex) {
        Log.w(LOG_TAG + tag, prefix().append(msg).append(ex).toString());
    }

    public static final void e(String tag, String msg) {
        Log.e(LOG_TAG + tag, prefix().append(msg).toString());
    }

    public static final void e(String tag, String msg, Exception ex) {
        Log.e(LOG_TAG + tag, prefix().append(msg).append(ex).toString());
    }
}
