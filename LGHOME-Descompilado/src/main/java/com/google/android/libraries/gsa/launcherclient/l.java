package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/* JADX INFO: compiled from: SimpleServiceConnection.java */
/* JADX INFO: loaded from: classes.dex */
class l implements ServiceConnection {
    private final Context a;
    private final int b;
    private final Handler c;
    private final Runnable d = new k(this);
    private final String e;
    private boolean f;

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    l(Context context, int i, Handler handler, String str) {
        this.a = context;
        this.b = i;
        this.c = handler;
        this.e = str;
    }

    static /* synthetic */ void a(l lVar) {
        if (lVar.f) {
            lVar.a.unbindService(lVar);
            lVar.f = false;
        }
    }

    public final String b() {
        return this.e;
    }

    public final boolean c() {
        return this.f;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    public final boolean d() {
        if (this.c.getLooper() != Looper.myLooper()) {
            throw new IllegalThreadStateException();
        }
        if (!this.f) {
            try {
                Context context = this.a;
                this.f = context.bindService(LauncherClient.a(context, this.e), this, this.b);
            } catch (SecurityException e) {
                Log.e("LauncherClient", "Unable to connect to overlay service", e);
            }
        }
        return this.f;
    }

    public static void a(Handler handler, Runnable runnable) {
        if (handler.getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

    public final void a() {
        a(this.c, this.d);
    }
}
