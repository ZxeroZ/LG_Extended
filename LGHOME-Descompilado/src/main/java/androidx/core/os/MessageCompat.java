package androidx.core.os;

import android.os.Build;
import android.os.Message;

/* JADX INFO: loaded from: classes.dex */
public final class MessageCompat {
    private static boolean sTryIsAsynchronous = true;
    private static boolean sTrySetAsynchronous = true;

    public static void setAsynchronous(Message message, boolean async) {
        if (Build.VERSION.SDK_INT >= 22) {
            message.setAsynchronous(async);
        } else {
            if (!sTrySetAsynchronous || Build.VERSION.SDK_INT < 16) {
                return;
            }
            try {
                message.setAsynchronous(async);
            } catch (NoSuchMethodError unused) {
                sTrySetAsynchronous = false;
            }
        }
    }

    public static boolean isAsynchronous(Message message) {
        if (Build.VERSION.SDK_INT >= 22) {
            return message.isAsynchronous();
        }
        if (sTryIsAsynchronous && Build.VERSION.SDK_INT >= 16) {
            try {
                return message.isAsynchronous();
            } catch (NoSuchMethodError unused) {
                sTryIsAsynchronous = false;
            }
        }
        return false;
    }

    private MessageCompat() {
    }
}
