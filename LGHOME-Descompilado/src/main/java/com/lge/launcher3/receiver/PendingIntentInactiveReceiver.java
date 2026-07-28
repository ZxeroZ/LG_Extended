package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class PendingIntentInactiveReceiver extends BroadcastReceiver {
    private static final String TAG = "PendingIntentInactiveReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (PendingIntentReceiver.isActivated()) {
            LGLog.i(TAG, "Dynamic receiver activated. Skip this.");
            return;
        }
        if (PendingIntentReceiver.isUseQueue()) {
            LGLog.i(TAG, "onReceive intent: " + intent);
            PendingIntentReceiver.queuePendingIntent(context, intent);
        }
    }
}
