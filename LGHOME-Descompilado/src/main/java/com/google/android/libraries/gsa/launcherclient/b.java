package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.libraries.gsa.launcherclient.AbsServiceStatusChecker;

/* JADX INFO: compiled from: AbsServiceStatusChecker.java */
/* JADX INFO: loaded from: classes.dex */
final class b implements ServiceConnection {
    final /* synthetic */ AbsServiceStatusChecker a;
    private final AbsServiceStatusChecker.StatusCallback b;

    public b(AbsServiceStatusChecker absServiceStatusChecker, AbsServiceStatusChecker.StatusCallback statusCallback) {
        this.a = absServiceStatusChecker;
        this.b = statusCallback;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            try {
                this.b.isRunning(this.a.a(iBinder));
            } catch (RemoteException e) {
                Log.w("AbsServiceStatusChecker", "isServiceRunning - remote call failed", e);
                this.a.a.unbindService(this);
                this.b.isRunning(false);
            }
        } finally {
            this.a.a.unbindService(this);
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
    }
}
