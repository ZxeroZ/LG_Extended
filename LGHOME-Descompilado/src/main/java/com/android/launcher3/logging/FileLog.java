package com.android.launcher3.logging;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public final class FileLog {
    private static final String FILE_NAME_PREFIX = "log-";
    private static final long MAX_LOG_FILE_SIZE = 4194304;
    protected static final boolean ENABLED = Utilities.IS_DEBUG_DEVICE;
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(3, 3);
    private static Handler sHandler = null;
    private static File sLogsDirectory = null;

    public static void setDir(File logsDir) {
        if (ENABLED) {
            synchronized (DATE_FORMAT) {
                if (sHandler != null && !logsDir.equals(sLogsDirectory)) {
                    ((HandlerThread) sHandler.getLooper().getThread()).quit();
                    sHandler = null;
                }
            }
        }
        sLogsDirectory = logsDir;
    }

    public static void d(String tag, String msg, Exception e) {
        Log.d(tag, msg, e);
        print(tag, msg, e);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        print(tag, msg);
    }

    public static void e(String tag, String msg, Exception e) {
        Log.e(tag, msg, e);
        print(tag, msg, e);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        print(tag, msg);
    }

    public static void print(String tag, String msg) {
        print(tag, msg, null);
    }

    public static void print(String tag, String msg, Exception e) {
        if (ENABLED) {
            String str = String.format("%s %s %s", DATE_FORMAT.format(new Date()), tag, msg);
            if (e != null) {
                str = str + "\n" + Log.getStackTraceString(e);
            }
            Message.obtain(getHandler(), 1, str).sendToTarget();
        }
    }

    private static Handler getHandler() {
        synchronized (DATE_FORMAT) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("file-logger");
                handlerThread.start();
                sHandler = new Handler(handlerThread.getLooper(), new LogWriterCallback());
            }
        }
        return sHandler;
    }

    public static void flushAll(PrintWriter out) throws InterruptedException {
        if (ENABLED) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Message.obtain(getHandler(), 3, Pair.create(out, countDownLatch)).sendToTarget();
            countDownLatch.await(2L, TimeUnit.SECONDS);
        }
    }

    private static class LogWriterCallback implements Handler.Callback {
        private static final long CLOSE_DELAY = 5000;
        private static final int MSG_CLOSE = 2;
        private static final int MSG_FLUSH = 3;
        private static final int MSG_WRITE = 1;
        private String mCurrentFileName;
        private PrintWriter mCurrentWriter;

        private LogWriterCallback() {
            this.mCurrentFileName = null;
            this.mCurrentWriter = null;
        }

        private void closeWriter() {
            Utilities.closeSilently(this.mCurrentWriter);
            this.mCurrentWriter = null;
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) throws Throwable {
            if (FileLog.sLogsDirectory != null && FileLog.ENABLED) {
                int i = msg.what;
                if (i != 1) {
                    if (i == 2) {
                        closeWriter();
                        return true;
                    }
                    if (i != 3) {
                        return true;
                    }
                    closeWriter();
                    Pair pair = (Pair) msg.obj;
                    if (pair.first != null) {
                        FileLog.dumpFile((PrintWriter) pair.first, "log-0");
                        FileLog.dumpFile((PrintWriter) pair.first, "log-1");
                    }
                    ((CountDownLatch) pair.second).countDown();
                    return true;
                }
                Calendar calendar = Calendar.getInstance();
                String str = FileLog.FILE_NAME_PREFIX + (calendar.get(6) & 1);
                if (!str.equals(this.mCurrentFileName)) {
                    closeWriter();
                }
                try {
                    if (this.mCurrentWriter == null) {
                        this.mCurrentFileName = str;
                        File file = new File(FileLog.sLogsDirectory, str);
                        boolean z = false;
                        if (file.exists()) {
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.setTimeInMillis(file.lastModified());
                            calendar2.add(10, 36);
                            if (calendar.before(calendar2) && file.length() < 4194304) {
                                z = true;
                            }
                        }
                        this.mCurrentWriter = new PrintWriter(new FileWriter(file, z));
                    }
                    this.mCurrentWriter.println((String) msg.obj);
                    this.mCurrentWriter.flush();
                    FileLog.sHandler.removeMessages(2);
                    FileLog.sHandler.sendEmptyMessageDelayed(2, CLOSE_DELAY);
                } catch (Exception e) {
                    Log.e("FileLog", "Error writing logs to file", e);
                    closeWriter();
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dumpFile(PrintWriter out, String fileName) throws Throwable {
        BufferedReader bufferedReader;
        File file = new File(sLogsDirectory, fileName);
        if (!file.exists()) {
            return;
        }
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (Exception unused) {
        } catch (Throwable th) {
            th = th;
        }
        try {
            out.println();
            out.println("--- logfile: " + fileName + " ---");
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    out.println(line);
                } else {
                    Utilities.closeSilently(bufferedReader);
                    return;
                }
            }
        } catch (Exception unused2) {
            bufferedReader2 = bufferedReader;
            Utilities.closeSilently(bufferedReader2);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            Utilities.closeSilently(bufferedReader2);
            throw th;
        }
    }
}
