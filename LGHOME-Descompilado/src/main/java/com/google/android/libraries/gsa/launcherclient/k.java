package com.google.android.libraries.gsa.launcherclient;

/* JADX INFO: compiled from: SimpleServiceConnection.java */
/* JADX INFO: loaded from: classes.dex */
final class k implements Runnable {
    final /* synthetic */ l a;

    k(l lVar) {
        this.a = lVar;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    @Override // java.lang.Runnable
    public final void run() {
        l.a(this.a);
    }
}
