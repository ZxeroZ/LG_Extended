package com.google.android.libraries.gsa.launcherclient;

import android.os.Looper;
import com.google.android.libraries.gsa.launcherclient.AbsServiceStatusChecker;

/* JADX INFO: compiled from: AbsServiceStatusChecker.java */
/* JADX INFO: loaded from: classes.dex */
final class a implements Runnable {
    final /* synthetic */ AbsServiceStatusChecker.StatusCallback a;
    final /* synthetic */ AbsServiceStatusChecker b;

    a(AbsServiceStatusChecker absServiceStatusChecker, AbsServiceStatusChecker.StatusCallback statusCallback) {
        this.b = absServiceStatusChecker;
        this.a = statusCallback;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    @Override // java.lang.Runnable
    public final void run() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Must be called on the main thread.");
        }
        this.a.isRunning(false);
    }
}
