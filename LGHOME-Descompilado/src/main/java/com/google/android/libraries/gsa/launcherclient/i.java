package com.google.android.libraries.gsa.launcherclient;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/* JADX INFO: compiled from: LauncherClient.java */
/* JADX INFO: loaded from: classes.dex */
final class i extends com.google.android.libraries.a.d implements Handler.Callback {
    private final Handler a = new Handler(Looper.getMainLooper(), this);
    private LauncherClient b;
    private WindowManager c;
    private int d;
    private Window e;

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    i() {
    }

    public final void a() {
        this.b = null;
        this.c = null;
        this.e = null;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        if (this.b == null) {
            return true;
        }
        int i = message.what;
        if (i == 2) {
            if ((this.b.m & 1) != 0) {
                float fFloatValue = ((Float) message.obj).floatValue();
                this.b.d.onOverlayScrollChanged(fFloatValue);
                if (fFloatValue <= 0.0f) {
                    this.b.g.a("onScroll 0, overlay closed");
                } else if (fFloatValue >= 1.0f) {
                    this.b.g.a("onScroll 1, overlay opened");
                } else {
                    this.b.g.a("onScroll", fFloatValue);
                }
            }
            return true;
        }
        if (i != 3) {
            if (i != 5) {
                return false;
            }
            Bundle bundle = (Bundle) message.obj;
            this.b.g.a("stateChanged", message.arg1);
            LauncherClient.a(this.b, bundle);
            return true;
        }
        WindowManager.LayoutParams attributes = this.e.getAttributes();
        if (((Boolean) message.obj).booleanValue()) {
            attributes.x = this.d;
            attributes.flags |= 512;
        } else {
            attributes.x = 0;
            attributes.flags &= -513;
        }
        this.c.updateViewLayout(this.e.getDecorView(), attributes);
        return true;
    }

    @Override // com.google.android.libraries.a.e
    public final void a(float f) throws RemoteException {
        this.a.removeMessages(2);
        Message.obtain(this.a, 2, Float.valueOf(f)).sendToTarget();
    }

    @Override // com.google.android.libraries.a.e
    public final void a(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("service_status", i);
        a(bundle);
    }

    @Override // com.google.android.libraries.a.e
    public final void a(Bundle bundle) {
        Message.obtain(this.a, 5, 0, 0, bundle).sendToTarget();
    }

    public final void a(LauncherClient launcherClient) {
        Display defaultDisplay;
        this.b = launcherClient;
        this.c = launcherClient.c.getWindowManager();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 30) {
            defaultDisplay = launcherClient.c.getDisplay();
        } else {
            defaultDisplay = this.c.getDefaultDisplay();
        }
        defaultDisplay.getRealSize(point);
        this.d = -Math.max(point.x, point.y);
        this.e = launcherClient.c.getWindow();
    }
}
