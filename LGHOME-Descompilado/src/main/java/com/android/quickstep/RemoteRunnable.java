package com.android.quickstep;

import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
@FunctionalInterface
public interface RemoteRunnable {
    void run() throws RemoteException;

    static void executeSafely(RemoteRunnable r) {
        try {
            r.run();
        } catch (RemoteException e) {
            Log.e("RemoteRunnable", "Error calling remote method", e);
        }
    }
}
