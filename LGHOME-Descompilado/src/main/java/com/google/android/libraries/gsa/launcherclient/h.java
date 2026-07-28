package com.google.android.libraries.gsa.launcherclient;

/* JADX INFO: compiled from: LauncherClient.java */
/* JADX INFO: loaded from: classes.dex */
final class h implements Runnable {
    final /* synthetic */ LauncherClient a;

    h(LauncherClient launcherClient) {
        this.a = launcherClient;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    @Override // java.lang.Runnable
    public final void run() {
        if (this.a.i.d() && this.a.h.d()) {
            return;
        }
        this.a.c.runOnUiThread(new g(this));
    }
}
