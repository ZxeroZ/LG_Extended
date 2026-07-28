package com.lge.lgewidgetlib;

import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class WLog {
    private static final String LOG_TAG = "[LgeWidgetLib]";
    private static final int REAL_METHOD_POS = 2;
    public static boolean ENABLE_LOG = LgeWidgetFeature.sFEATURE_ENABLE_LOG;
    public static StackLogger stack = new StackLogger();

    private static final StringBuilder prefix() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        return new StringBuilder("[").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(":").append(stackTraceElement.getMethodName()).append("()]");
    }

    public static final void exceptWithLog(String tag, String msg) {
        if (ENABLE_LOG) {
            e(tag, msg);
        }
    }

    public static final void v(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.v(LOG_TAG + tag, prefix().append(msg).toString());
        }
    }

    public static final void d(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.d(LOG_TAG + tag, prefix().append(msg).toString());
        }
    }

    public static final void d(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
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

    public static final String getCallerMethodName() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace[2] != null) {
            return stackTrace[2].getMethodName();
        }
        return null;
    }

    public static final class StackLogger {
        private static final String CALLSTACK_PREFIX = "\nStackLogger:   ";
        private static final String FOOT_PRINT = "StackLogger: ";
        private static final String HEAD_MSG_PREFIX = "StackLogger: Print Callstack: ";

        public void v(String tag, String msg) {
            Log.v(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void d(String tag, String msg) {
            Log.d(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void i(String tag, String msg) {
            Log.i(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void w(String tag, String msg) {
            Log.w(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void e(String tag, String msg) {
            Log.e(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void v(String tag, String msg, int lines) {
            Log.v(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void d(String tag, String msg, int lines) {
            Log.d(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void i(String tag, String msg, int lines) {
            Log.i(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void w(String tag, String msg, int lines) {
            Log.w(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void e(String tag, String msg, int lines) {
            Log.e(WLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        private String getStackTrace() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            for (int i = 2; i < stackTrace.length; i++) {
                sb.append(CALLSTACK_PREFIX + stackTrace[i].toString());
            }
            return sb.toString();
        }

        private String getStackTrace(int lines) {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            int i = 2;
            for (int i2 = 0; i < stackTrace.length && i2 < lines; i2++) {
                sb.append(CALLSTACK_PREFIX + stackTrace[i].toString());
                i++;
            }
            if (i < stackTrace.length) {
                sb.append("\nStackLogger:   ...");
            }
            return sb.toString();
        }
    }
}
