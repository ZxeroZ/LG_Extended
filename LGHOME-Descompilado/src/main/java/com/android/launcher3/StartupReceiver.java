package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public class StartupReceiver extends BroadcastReceiver {
    static final String SYSTEM_READY = "com.android.launcher3.SYSTEM_READY";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        context.sendStickyBroadcast(new Intent(SYSTEM_READY));
    }
}
