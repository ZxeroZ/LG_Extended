package com.android.launcher3.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;

/* JADX INFO: loaded from: classes.dex */
public class UiThreadHelper {
    private static final int MSG_HIDE_KEYBOARD = 1;
    private static final int MSG_RUN_COMMAND = 3;
    private static final int MSG_SET_ORIENTATION = 2;
    private static Handler sHandler;
    private static HandlerThread sHandlerThread;

    public interface AsyncCommand {
        void execute(Context proxy, int arg1, int arg2);
    }

    public static Looper getBackgroundLooper() {
        if (sHandlerThread == null) {
            HandlerThread handlerThread = new HandlerThread("UiThreadHelper", -2);
            sHandlerThread = handlerThread;
            handlerThread.start();
        }
        return sHandlerThread.getLooper();
    }

    private static Handler getHandler(Context context) {
        if (sHandler == null) {
            sHandler = new Handler(getBackgroundLooper(), new UiCallbacks(context.getApplicationContext()));
        }
        return sHandler;
    }

    public static void hideKeyboardAsync(Context context, IBinder token) {
        Message.obtain(getHandler(context), 1, token).sendToTarget();
    }

    public static void setOrientationAsync(Activity activity, int orientation) {
        Message.obtain(getHandler(activity), 2, orientation, 0, activity).sendToTarget();
    }

    public static void setBackButtonAlphaAsync(Context context, AsyncCommand asyncCommand, float f, boolean z) {
        runAsyncCommand(context, asyncCommand, Float.floatToIntBits(f), z ? 1 : 0);
    }

    public static void runAsyncCommand(Context context, AsyncCommand command, int arg1, int arg2) {
        Message.obtain(getHandler(context), 3, arg1, arg2, command).sendToTarget();
    }

    private static class UiCallbacks implements Handler.Callback {
        private final Context mContext;
        private final InputMethodManager mIMM;

        UiCallbacks(Context context) {
            this.mContext = context;
            this.mIMM = (InputMethodManager) context.getApplicationContext().getSystemService("input_method");
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mIMM.hideSoftInputFromWindow((IBinder) message.obj, 0);
                return true;
            }
            if (i == 2) {
                ((Activity) message.obj).setRequestedOrientation(message.arg1);
                return true;
            }
            if (i != 3) {
                return false;
            }
            ((AsyncCommand) message.obj).execute(this.mContext, message.arg1, message.arg2);
            return true;
        }
    }
}
