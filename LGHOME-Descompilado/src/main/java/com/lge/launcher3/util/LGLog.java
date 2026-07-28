package com.lge.launcher3.util;

import android.os.Build;
import android.util.Log;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class LGLog {
    private static final boolean ENABLE_LOG;
    private static final String LOG_TAG = "[LGHome6]";
    private static final int REAL_METHOD_POS = 2;
    public static StackLogger stack;

    static {
        ENABLE_LOG = "userdebug".equals(Build.TYPE) || "eng".equals(Build.TYPE);
        stack = new StackLogger();
    }

    private static StringBuilder prefix(int... level) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[level.length > 0 ? level[0] : 2];
        sb.append(String.format(Locale.getDefault(), "[%s:%s:%s()]", stackTraceElement.getFileName(), Integer.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName()));
        return sb;
    }

    public static final void v(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.v(LOG_TAG + tag, msg);
        }
    }

    public static final void d(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.d(LOG_TAG + tag, msg);
        }
    }

    public static final void d(String tag, String msg, Throwable tr) {
        if (ENABLE_LOG) {
            Log.d(LOG_TAG + tag, msg);
        }
    }

    public static final void i(String tag, String msg, Throwable tr) {
        Log.i(LOG_TAG + tag, msg + "\n" + Log.getStackTraceString(tr));
    }

    public static final void i(String tag, String msg) {
        Log.i(LOG_TAG + tag, msg);
    }

    public static final void w(String tag, String msg, int... depth) {
        Log.w(LOG_TAG + tag, prefix(depth).append(msg).toString());
    }

    public static final void w(String tag, String msg, Exception ex, int... depth) {
        Log.w(LOG_TAG + tag, prefix(depth).append(msg).append(ex).toString());
    }

    public static final void e(String tag, String msg) {
        stack.e(LOG_TAG + tag, msg);
    }

    public static final void e(String tag, String msg, boolean addPrefix) {
        stack.e(LOG_TAG + tag, msg);
    }

    public static final void e(String tag, String msg, Exception ex) {
        stack.e(LOG_TAG + tag, msg);
    }

    public static final void e(String tag, String msg, boolean addPrefix, Exception ex) {
        stack.e(LOG_TAG + tag, msg);
    }

    public static final class StackLogger {
        private static final String CALLSTACK_PREFIX = "\nStackLogger:   ";
        private static final String FOOT_PRINT = "StackLogger: ";
        private static final String HEAD_MSG_PREFIX = "StackLogger: Print Callstack: ";

        public void v(String tag, String msg) {
            Log.v(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void d(String tag, String msg) {
            Log.d(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void i(String tag, String msg) {
            Log.i(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void w(String tag, String msg) {
            Log.w(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void e(String tag, String msg) {
            Log.e(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace());
        }

        public void v(String tag, String msg, int lines) {
            Log.v(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void d(String tag, String msg, int lines) {
            Log.d(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void i(String tag, String msg, int lines) {
            Log.i(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void w(String tag, String msg, int lines) {
            Log.w(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
        }

        public void e(String tag, String msg, int lines) {
            Log.e(LGLog.LOG_TAG + tag, HEAD_MSG_PREFIX + msg + getStackTrace(lines));
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
