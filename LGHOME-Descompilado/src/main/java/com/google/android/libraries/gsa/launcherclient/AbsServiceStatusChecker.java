package com.google.android.libraries.gsa.launcherclient;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbsServiceStatusChecker {
    final Context a;

    public interface StatusCallback {
        void isRunning(boolean z);
    }

    protected AbsServiceStatusChecker(Context context) {
        this.a = context;
    }

    protected abstract boolean a(IBinder iBinder) throws RemoteException;
}
