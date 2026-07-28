package com.android.launcher3.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class SimpleBroadcastReceiver extends BroadcastReceiver {
    private final Consumer<Intent> mIntentConsumer;

    public SimpleBroadcastReceiver(Consumer<Intent> intentConsumer) {
        this.mIntentConsumer = intentConsumer;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mIntentConsumer.accept(intent);
    }

    public void register(Context context, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String str : actions) {
            intentFilter.addAction(str);
        }
        context.registerReceiver(this, intentFilter);
    }
}
