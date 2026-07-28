package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import java.lang.ref.WeakReference;

/* JADX INFO: compiled from: AppServiceConnection.java */
/* JADX INFO: loaded from: classes.dex */
final class c extends l {
    private static c a;
    private com.google.android.libraries.a.c b;
    private WeakReference<LauncherClient> c;
    private boolean d;

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    private c(Context context, Handler handler, String str) {
        super(context, 33, handler, str);
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    public final void a(LauncherClient launcherClient, boolean z) {
        LauncherClient launcherClientF = f();
        if (launcherClientF == null || !launcherClientF.equals(launcherClient)) {
            return;
        }
        this.c = null;
        if (z) {
            a();
            if (a == this) {
                a = null;
            }
        }
    }

    static c a(Context context, Handler handler, String str) {
        c cVar = a;
        if (cVar != null && !str.equals(cVar.b())) {
            cVar.a();
            a = null;
        }
        if (a == null) {
            a = new c(context.getApplicationContext(), handler, str);
        }
        return a;
    }

    private final LauncherClient f() {
        WeakReference<LauncherClient> weakReference = this.c;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    @Override // com.google.android.libraries.gsa.launcherclient.l, android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        a(com.google.android.libraries.a.b.a(iBinder));
    }

    @Override // com.google.android.libraries.gsa.launcherclient.l, android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        a((com.google.android.libraries.a.c) null);
        e();
    }

    public final void a(boolean z) {
        this.d = z;
        e();
    }

    public final com.google.android.libraries.a.c a(LauncherClient launcherClient) {
        this.c = new WeakReference<>(launcherClient);
        return this.b;
    }

    private final void a(com.google.android.libraries.a.c cVar) {
        this.b = cVar;
        LauncherClient launcherClientF = f();
        if (launcherClientF != null) {
            launcherClientF.a(this.b);
        }
    }

    private final void e() {
        if (this.d && this.b == null) {
            a();
        }
    }
}
