package com.google.android.libraries.gsa.launcherclient;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.libraries.gsa.launcherclient.AbsServiceStatusChecker;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class OverlayContentChecker extends AbsServiceStatusChecker {
    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    public OverlayContentChecker(Context context) {
        super(context);
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    public void checkOverlayContent(AbsServiceStatusChecker.StatusCallback statusCallback) {
        Intent intentA = LauncherClient.a(this.a, LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME);
        intentA.setPackage(LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME);
        if (this.a.bindService(intentA, new b(this, statusCallback), 1)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new a(this, statusCallback));
    }

    @Override // com.google.android.libraries.gsa.launcherclient.AbsServiceStatusChecker
    protected final boolean a(IBinder iBinder) throws RemoteException {
        return com.google.android.libraries.a.b.a(iBinder).f();
    }
}
