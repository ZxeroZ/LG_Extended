package com.google.android.libraries.gsa.launcherclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/* JADX INFO: compiled from: LauncherClient.java */
/* JADX INFO: loaded from: classes.dex */
final class f extends BroadcastReceiver {
    final /* synthetic */ LauncherClient a;

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    f(LauncherClient launcherClient) {
        this.a = launcherClient;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (Build.VERSION.SDK_INT >= 19 || (data != null && this.a.n.b.equals(data.getSchemeSpecificPart()))) {
            this.a.h.a();
            this.a.i.a();
            this.a.a(context);
            if ((this.a.k & 2) != 0) {
                this.a.reconnect();
            }
        }
    }
}
